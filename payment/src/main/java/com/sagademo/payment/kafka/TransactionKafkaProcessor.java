package com.sagademo.payment.kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;

import javax.persistence.NoResultException;

import com.sagademo.payment.Transaction;
import com.sagademo.payment.TransactionResult;
import com.sagademo.payment.TransactionResult.ResultType;
import com.sagademo.payment.entity.InsuficentBalanceException;
import com.sagademo.payment.TransactionService;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class TransactionKafkaProcessor implements Runnable {

    private static final String PAYMENT_REQUEST_TOPIC = "payment_request";
    private static final String PAYMENT_RESPONSE_TOPIC = "payment_response";

    private Properties properties = new Properties();
    private TransactionService transactionService;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public TransactionKafkaProcessor(final String kafkaURL, TransactionService transactionService) {
        this.transactionService = transactionService;
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TransactionResultSerializer.class);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TransactionDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "payment");
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "payment");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaURL);
    }

    @Override
    public void run() {
        Consumer<String, Transaction> consumer = new KafkaConsumer<>(properties);
        Producer<String, TransactionResult> producer = new KafkaProducer<>(properties);
        consumer.subscribe(Collections.singletonList(PAYMENT_REQUEST_TOPIC));
        try {
            while (true) {
                ConsumerRecords<String, Transaction> records = consumer.poll(Duration.ofSeconds(100));
                for (ConsumerRecord<String, Transaction> record : records) {
                    Transaction transaction = record.value();
                    // Check if transaction came from the Deserializer
                    if (transaction != null) {
                        processRequestTransaction(transaction, producer);
                    }
                }
            }
        } finally {
            consumer.close();
            producer.close();
        }

    }

    /**
     * 
     * @param transaction Requested transaction
     * @param producer Kafka Producer that will be used to send the result of this transaction
     */
    private void processRequestTransaction(Transaction transaction, Producer<String, TransactionResult> producer) {
        TransactionResult transactionResult = null;
        try {
            transactionService.processTransaction(transaction.getAccount(), transaction);
            transactionResult = new TransactionResult(transaction.getAccount(),
                    transaction.getValue(), ResultType.APPROVED, null);
        } catch (NoResultException e) {
            transactionResult = new TransactionResult(transaction.getAccount(),
                    transaction.getValue(), ResultType.DENIED,
                    "No Balance for account " + transaction.getAccount());
        } catch (InsuficentBalanceException e) {
            transactionResult = new TransactionResult(transaction.getAccount(),
                    transaction.getValue(), ResultType.DENIED, e.getMessage());
        }finally{
            ProducerRecord<String, TransactionResult> producerRecord =
                new ProducerRecord<>(PAYMENT_RESPONSE_TOPIC, transactionResult);
            // Produce the result for the transaction    
            producer.send(producerRecord);
        }
    }

}