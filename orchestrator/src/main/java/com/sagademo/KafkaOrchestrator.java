package com.sagademo;

import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import com.sagademo.models.OrderCommand;
import com.sagademo.models.OrderCommand.OrderRequest;
import com.sagademo.models.OrderCreatedEvent;
import com.sagademo.models.PaymentRequest;
import com.sagademo.models.PaymentRequest.TransactionType;
import com.sagademo.models.PaymentResponse;
import com.sagademo.models.ReservationCommand;
import com.sagademo.models.ReservationCommand.ReservationRequest;
import com.sagademo.models.ReservationResult;
import com.sagademo.serdes.SerdesFactory;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

public class KafkaOrchestrator {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public static void main(String[] args) {
        new KafkaOrchestrator().startKafkaStreams();
    }

    private void startKafkaStreams() {
        createTopics();
        final KafkaStreams streams = new KafkaStreams(buildTopology(), getStreamsProperties());
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }

    private void createTopics() {
        createTopic("payment_request", 1);
        createTopic("payment_response", 1);
        createTopic("order_request", 1);
        createTopic("order_response", 1);
        createTopic("reservation_request", 1);
        createTopic("reservation_response", 1);
    }

    /**
     * Creates a topic in Kafka. If the topic already exists this does nothing.
     * 
     * @param topicName  - the namespace name to create.
     * @param partitions - the number of partitions to create.
     */
    public void createTopic(final String topicName, final int partitions) {
        final short replicationFactor = 1;
        logger.info(String.format("Trying to create topic %s", topicName));

        // Create admin client
        try (final AdminClient adminClient = KafkaAdminClient.create(getStreamsProperties())) {
            try {
                // Define topic
                final NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);

                // Create topic, which is async call.
                final CreateTopicsResult createTopicsResult = adminClient.createTopics(Collections.singleton(newTopic));

                // Since the call is Async, Lets wait for it to complete.
                createTopicsResult.values().get(topicName).get();
            } catch (InterruptedException | ExecutionException e) {
                if (!(e.getCause() instanceof TopicExistsException)) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                logger.warning(String.format("Topic %s already exists", topicName));
                // TopicExistsException - Swallow this exception, just means the topic already
                // exists.
            }
        }
    }

    private Topology buildTopology() {
        final StreamsBuilder builder = new StreamsBuilder();

        // Get Order and request reservation
        KStream<String, OrderCreatedEvent> orders = getPendingOrders(builder);
        requestRoomReservation(orders);

        // reservationResults[0] = Stream of Approved reservations
        // reservationResults[1] = Stream of Denied reservations
        KStream<String, ReservationResult>[] reservationResults = getRoomReservationResponse(builder);
        sendPaymentRequestForApprovedReservations(reservationResults[0]);
        cancelOrderForDeniedReservations(reservationResults[1]);

        // Get Payment response and send to Order
        KStream<String, PaymentResponse> paymentResponseStream = getPaymentResponseStream(builder);
        sendOrderResponse(paymentResponseStream);
        sendBookingCommand(paymentResponseStream);

        final Topology topology = builder.build();
        return topology;
    }

    private void cancelOrderForDeniedReservations(KStream<String, ReservationResult> reservationKStream) {
        KStream<String, OrderCommand> orderResponseStream = reservationKStream.mapValues((reservation) -> {
            return new OrderCommand(Long.valueOf(reservation.getOrder()), OrderCommand.OrderRequest.CANCEL, reservation.getExceptionCause());
        });
        orderResponseStream.foreach((key, value) -> logger.info("Sending Order response: " + value));
        orderResponseStream.to("order_response",
                Produced.with(Serdes.String(), SerdesFactory.getSerde(OrderCommand.class)));
    }

    private KStream<String, ReservationResult>[] getRoomReservationResponse(StreamsBuilder builder) {
        KStream<String, ReservationResult> results[] = builder.stream("reservation_response", 
            Consumed.with(Serdes.String(), SerdesFactory.getSerde(ReservationResult.class)))
            .branch(
                (key, value) -> ReservationResult.RoomSituation.RESERVED.equals(value.getReservationStatus()),
                (key, value) -> (value.getExceptionCause() != null)
            );
        results[0].foreach((key, value) -> logger.info("Processing Reservation response: " + value));
        results[1].foreach((key, value) -> logger.info("Processing Reservation exception: " + value));
        return results;
    }

    private void requestRoomReservation(KStream<String, OrderCreatedEvent> orderCreatedStream) {
        KStream<String, ReservationCommand> reservationRequesStream = orderCreatedStream.mapValues((value) -> {
            return new ReservationCommand(String.valueOf(value.getId()),
                    ReservationCommand.ReservationRequest.RESERVE);
        });
        reservationRequesStream.foreach((key, value) -> logger.info("Requesting a Room " + value));
        reservationRequesStream.to("reservation_request",
                Produced.with(Serdes.String(), SerdesFactory.getSerde(ReservationCommand.class)));
    }

    private void sendBookingCommand(KStream<String, PaymentResponse> paymentResponseStream) {
        KStream<String, ReservationCommand> orderResponseStream = paymentResponseStream.mapValues((paymentReponse) -> {
            ReservationRequest reservationRequest = null;
            switch (paymentReponse.getResultType()) {
                case APPROVED:
                    reservationRequest = ReservationRequest.CONFIRM;
                    break;
                case DENIED:
                    reservationRequest = ReservationRequest.CANCEL;
                    break;
            }
            return new ReservationCommand(paymentReponse.getTransactionIdentifier(), reservationRequest);
        });
        orderResponseStream.foreach((key, value) -> logger.info("Sending Reservation command: " + value));
        orderResponseStream.to("reservation_request",
                Produced.with(Serdes.String(), SerdesFactory.getSerde(ReservationCommand.class)));
    }


    private void sendOrderResponse(KStream<String, PaymentResponse> paymentResponseStream) {
        KStream<String, OrderCommand> orderResponseStream = paymentResponseStream.mapValues((paymentReponse) -> {
            OrderRequest orderRequest = null;
            String cause = paymentReponse.getCause();
            switch (paymentReponse.getResultType()) {
                case APPROVED:
                    orderRequest = OrderRequest.CONFIRM;
                    break;
                case DENIED:
                    orderRequest = OrderRequest.CANCEL;
                    break;
            }
            return new OrderCommand(Long.valueOf(paymentReponse.getTransactionIdentifier()), orderRequest, cause);
        });
        orderResponseStream.foreach((key, value) -> logger.info("Sending Order response: " + value));
        orderResponseStream.to("order_response",
                Produced.with(Serdes.String(), SerdesFactory.getSerde(OrderCommand.class)));
    }

    private KStream<String, PaymentResponse> getPaymentResponseStream(StreamsBuilder builder) {
        KStream<String, PaymentResponse> paymentOutputStream = builder.stream("payment_response",
                Consumed.with(Serdes.String(), SerdesFactory.getSerde(PaymentResponse.class)))
            .filter((key, value) -> value != null);
            paymentOutputStream.foreach((key, value) -> logger.info("Reading Payment result: " + value));
        return paymentOutputStream;
    }

    private KStream<String, PaymentRequest> sendPaymentRequestForApprovedReservations(KStream<String, ReservationResult> reservationResult) {
        KStream<String, PaymentRequest> paymentStream = reservationResult.mapValues((reservation) -> {
            return new PaymentRequest(1, reservation.getOrder(), TransactionType.WITHDRAW, reservation.getPrice());
        });
        paymentStream.foreach((key, value) -> logger.info("Sending Payment request: " + value));
        paymentStream.to("payment_request",
                Produced.with(Serdes.String(), SerdesFactory.getSerde(PaymentRequest.class)));

        return paymentStream;
    }

    private KStream<String, OrderCreatedEvent> getPendingOrders(StreamsBuilder builder) {
        KStream<String, OrderCreatedEvent> orderInputStream = builder.stream("order_request",
                Consumed.with(Serdes.String(), SerdesFactory.getSerde(OrderCreatedEvent.class)));
        orderInputStream.filter((key, value) -> value != null)
                .filter((key, value) -> value.getStatus() == com.sagademo.models.OrderCreatedEvent.OrderStatus.PENDING)
                .foreach((key, value) -> logger.info("Reading PENDING Order: " + value));
        return orderInputStream;
    }

    private Properties getStreamsProperties() {
        String kafka_url = Optional.ofNullable(System.getenv("KAFKA_URL")).orElse("localhost:19092");
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "orchestrator");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafka_url);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return props;
    }
}