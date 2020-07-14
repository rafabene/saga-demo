package com.sagademo.kafka;

import java.util.logging.Logger;

import com.sagademo.domain.service.ReservationService;
import com.sagademo.domain.vo.ReservationCommand;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BookingKafkaConsumer {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private ReservationService reservationService;

    @KafkaListener(topics = "reservation_request")
    public void listen(ConsumerRecord<String, ReservationCommand> cr) throws Exception {
        ReservationCommand reservationCommand = cr.value();
        logger.info("Received Reservation Command: " + reservationCommand);
        reservationService.processReservationCommand(reservationCommand);
    }


    
}