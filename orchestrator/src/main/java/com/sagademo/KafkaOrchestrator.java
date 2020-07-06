package com.sagademo;

import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import com.sagademo.models.OrderRequest;
import com.sagademo.models.OrderResponse;
import com.sagademo.models.OrderResponse.OrderStatus;
import com.sagademo.models.PaymentRequest;
import com.sagademo.models.PaymentRequest.TransactionType;
import com.sagademo.models.PaymentResponse;
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
    }

    /**
    * Creates a topic in Kafka. If the topic already exists this does nothing.
    * @param topicName - the namespace name to create.
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
               // TopicExistsException - Swallow this exception, just means the topic already exists.
           }
       }
   }
    

    private Topology buildTopology() {
        final StreamsBuilder builder = new StreamsBuilder();
        // Get Order and send to Payment
        KStream<String, OrderRequest> orderRequestStream = getOrderRequestStream(builder);
        sendPaymentRequest(orderRequestStream);

        // Get Payment response and send to Order
        KStream<String, PaymentResponse> paymentResponseStream = getPaymentResponseStream(builder);
        sendOrderResponse(paymentResponseStream);

        final Topology topology = builder.build();
        System.out.println(topology.describe());
        return topology;
    }

    private void sendOrderResponse(KStream<String, PaymentResponse> paymentResponseStream) {
        KStream<String, OrderResponse> orderResponseStream = paymentResponseStream.mapValues((paymentReponse) -> {
            OrderStatus orderStatus = null;
            String cause = paymentReponse.getCause();
            switch (paymentReponse.getResultType()) {
                case APPROVED:
                    orderStatus = OrderStatus.CONFIRMED;
                    break;
                case DENIED:
                    orderStatus = OrderStatus.CANCELED;
                    break;
            }
            return new OrderResponse(Long.valueOf(paymentReponse.getTransactionIdentifier()), orderStatus, cause);
        });
        orderResponseStream.foreach((key, value) -> logger.info("Processing Order response: " + value));
        orderResponseStream.to("order_response",
                Produced.with(Serdes.String(), SerdesFactory.getSerde(OrderResponse.class)));
    }

    private KStream<String, PaymentResponse> getPaymentResponseStream(StreamsBuilder builder) {
        KStream<String, PaymentResponse> paymentOutputStream = builder.stream("payment_response",
                Consumed.with(Serdes.String(), SerdesFactory.getSerde(PaymentResponse.class)));
        paymentOutputStream.filter((key, value) -> value != null)
                .foreach((key, value) -> logger.info("Processing Payment response: " + value));
        return paymentOutputStream;
    }

    private KStream<String, PaymentRequest> sendPaymentRequest(KStream<String, OrderRequest> orderRequestStream) {
        KStream<String, PaymentRequest> paymentStream = orderRequestStream.mapValues((order) -> {
            return new PaymentRequest(1, String.valueOf(order.getId()), TransactionType.WITHDRAW, 30.0);
        });
        paymentStream.foreach((key, value) -> logger.info("Sending Payment info: " + value));
        paymentStream.to("payment_request",
                Produced.with(Serdes.String(), SerdesFactory.getSerde(PaymentRequest.class)));

        return paymentStream;
    }

    private KStream<String, OrderRequest> getOrderRequestStream(StreamsBuilder builder) {
        KStream<String, OrderRequest> orderInputStream = builder.stream("order_request",
                Consumed.with(Serdes.String(), SerdesFactory.getSerde(OrderRequest.class)));
        orderInputStream.filter((key, value) -> value != null)
                .foreach((key, value) -> logger.info("Processing Order: " + value));
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