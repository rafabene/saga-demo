package com.sagademo.payment.kafka;

import java.util.logging.Logger;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import com.sagademo.payment.Transaction;
import com.sagademo.payment.TransactionResult;

import org.apache.kafka.common.serialization.Serializer;

public class TransactionResultSerializer implements Serializer<TransactionResult> {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public byte[] serialize(String topic, TransactionResult transactionResult) {
        Jsonb jsonb = JsonbBuilder.create();
        try {
            String s = jsonb.toJson(transactionResult);
            return s.getBytes();
        } catch (Exception e) {
            logger.warning("Failed to convert " + transactionResult + " to String.");
            return null;
        }
    }
    
}