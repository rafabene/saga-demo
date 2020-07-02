package com.sagademo.payment.kafka;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.sagademo.payment.TransactionService;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * BalanceKafkaConsumer
 */
@ApplicationScoped
public class KafkaInitializer {

    @Inject
    @ConfigProperty(name = "kafka_url", defaultValue = "localhost:19092")
    private String kafka_url;

    @Inject
    private TransactionService transactionService;
 
    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        Thread kafkaConsumer = new Thread( 
            new TransactionKafkaProcessor(kafka_url, transactionService),
            "TransactionKafkaConsumer");
        kafkaConsumer.start();
    }

    public void destroy(@Observes @Destroyed(ApplicationScoped.class) Object init) {
    }

}