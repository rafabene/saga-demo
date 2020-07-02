package com.sagademo.payment.kafka;

import java.util.logging.Logger;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import com.sagademo.payment.Transaction;

import org.apache.kafka.common.serialization.Deserializer;

public class TransactionDeserializer implements Deserializer<Transaction> {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public Transaction deserialize(String topic, byte[] data) {
        String s = new String(data);
        logger.info("Raw value received from Kafka: " + s);
        Jsonb jsonb = JsonbBuilder.create();
        try {
            Transaction transaction = jsonb.fromJson(s, Transaction.class);
            return transaction;
        } catch (Exception e) {
            logger.warning("Failed to convert " + s + " to " + Transaction.class.getName());
            return null;
        }
    }
    
}