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

import org.apache.kafka.clients.CommonClientConfigs;
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
    private Consumer<String, Transaction> consumer;
    private Producer<String, TransactionResult> producer;

    public TransactionKafkaProcessor(final String kafkaURL, TransactionService transactionService) {
        this.transactionService = transactionService;
        properties.put(CommonClientConfigs.GROUP_ID_CONFIG, "payment");
        properties.put(CommonClientConfigs.CLIENT_ID_CONFIG, "payment-request");
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaURL);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TransactionResultSerializer.class);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TransactionDeserializer.class);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new KafkaConsumer<>(properties);
        producer = new KafkaProducer<>(properties);
    }

    @Override
    public void run() {
        consumer.subscribe(Collections.singletonList(PAYMENT_REQUEST_TOPIC));
        try {
            while (true) {
                ConsumerRecords<String, Transaction> records = consumer.poll(Duration.ofSeconds(100));
                for (ConsumerRecord<String, Transaction> record : records) {
                    Transaction transaction = record.value();
                    // Check if transaction came from the Deserializer
                    if (transaction != null) {
                        processTransaction(transaction);
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
     * @param producer    Kafka Producer that will be used to send the result of
     *                    this transaction
     */
    private void processTransaction(Transaction transaction) {
        TransactionResult transactionResult = null;
        try {
            Integer accountId = transaction.getAccount();
            transactionService.processTransaction(accountId, transaction);
            transactionResult = new TransactionResult(transaction.getAccount(), transaction.getTransactionIdentifier(),
                    transaction.getValue(), ResultType.APPROVED, null);
        } catch (NoResultException e) {
            transactionResult = new TransactionResult(transaction.getAccount(), transaction.getTransactionIdentifier(),
                    transaction.getValue(), ResultType.DENIED, "No Balance for account " + transaction.getAccount());
        } catch (InsuficentBalanceException e) {
            transactionResult = new TransactionResult(transaction.getAccount(), transaction.getTransactionIdentifier(),
                    transaction.getValue(), ResultType.DENIED, e.getMessage());
        } finally {
            if (transactionResult != null) {
                ProducerRecord<String, TransactionResult> producerRecord = new ProducerRecord<>(PAYMENT_RESPONSE_TOPIC,
                        transactionResult);
                producer.send(producerRecord);
            }
        }
    }

}