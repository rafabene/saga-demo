package com.sagademo;

import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import com.sagademo.models.OrderRequest;
import com.sagademo.models.PaymentRequest;
import com.sagademo.models.PaymentResponse;
import com.sagademo.models.PaymentRequest.TransactionType;
import com.sagademo.serdes.SerdesFactory;

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

    private void startKafkaStreams(){
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


    private Topology buildTopology() {
        final StreamsBuilder builder = new StreamsBuilder();
        KStream<String, OrderRequest> orderRequestStream = getOrderRequestStream(builder); 
        getPaymentRequesStream(orderRequestStream);
        KStream<String, PaymentResponse> paymentResponseStream = getPaymentResponseStream(builder);
        final Topology topology = builder.build();
        System.out.println(topology.describe());
        return topology;
    }

    private KStream<String, PaymentResponse> getPaymentResponseStream(StreamsBuilder builder) {
        KStream<String, PaymentResponse> paymentOutputStream = builder.stream("payment_response", 
            Consumed.with(
                Serdes.String(), 
                SerdesFactory.getSerde(PaymentResponse.class)
            )
        );
        paymentOutputStream
            .filter((key, value) -> value != null)
            .foreach((key, value) -> logger.info("Processing Payment response: " + value));
        return paymentOutputStream;
    }

    private KStream<String, PaymentRequest> getPaymentRequesStream(
            KStream<String, ? extends Object> previousStream) {
        KStream<String, PaymentRequest> paymentStream = previousStream
            .mapValues( (value) -> {
                return new PaymentRequest(1, TransactionType.WITHDRAW, 30.0); 
            });
        paymentStream.foreach((key, value) -> logger.info("Sending Payment info: " + value));
        paymentStream.to("payment_request", Produced
            .with(
                Serdes.String(), 
                SerdesFactory.getSerde(PaymentRequest.class))
            );
        
        return paymentStream;
    }

    private KStream<String, OrderRequest> getOrderRequestStream(StreamsBuilder builder) {
        KStream<String, OrderRequest> orderInputStream = builder.stream("order_request", 
            Consumed.with(
                Serdes.String(), 
                SerdesFactory.getSerde(OrderRequest.class)
            )
        );
        orderInputStream
            .filter((key, value) -> value != null)
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