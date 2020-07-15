package com.sagademo.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.sagademo.domain.entity.Room;
import com.sagademo.domain.repository.RoomRepository;
import com.sagademo.domain.vo.ReservationCommand;
import com.sagademo.domain.vo.ReservationResult;
import com.sagademo.domain.vo.RoomSituation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private KafkaTemplate<String, ReservationResult> kafkaTemplate;

    public ReservationResult processReservationCommand(ReservationCommand reservationCommand) {
        ReservationResult result = null;
        switch (reservationCommand.getReservationRequest()) {
            case RESERVE:
                result = reserveRoom(reservationCommand);
                break;
            case CANCEL:
                result = cancelRoom(reservationCommand);
                break;

            case CONFIRM:
                result  = confirmRoom(reservationCommand);
                break;
        }
        logger.info("Producing Reservation Response: " + result);
        kafkaTemplate.send("reservation_response", result);
        return result;
    }

    private ReservationResult confirmRoom(ReservationCommand reservationCommand) {
        List<Room> rooms = roomRepository.findByRoomSituationAndOrder(RoomSituation.RESERVED, reservationCommand.getOrder());
        if (rooms.size() > 0) {
            Room room = rooms.get(0);
            room.setOrder(reservationCommand.getOrder());
            room.setRoomSituation(RoomSituation.CONFIRMED);
            roomRepository.save(room);
            return new ReservationResult(room);
        } else {
            return new ReservationResult(reservationCommand.getOrder(), null, null, null,
                    "There are no rooms reserved for the Order " + reservationCommand.getOrder());
        }
    }

    private ReservationResult cancelRoom(ReservationCommand reservationCommand) {
        List<Room> rooms = roomRepository.findByRoomSituationAndOrder(RoomSituation.RESERVED, reservationCommand.getOrder());
        if (rooms.size() > 0){
            Room room = rooms.get(0);
            room.setOrder(null);
            room.setRoomSituation(RoomSituation.FREE);
            roomRepository.save(room);
            return new ReservationResult(room);
        }else{
            return new ReservationResult(reservationCommand.getOrder(), null, null, null,
                    "There's no reserved room for the order " + reservationCommand.getOrder());
        }
    }

    private ReservationResult reserveRoom(ReservationCommand reservationCommand) {
        List<Room> rooms = roomRepository.findByRoomSituation(RoomSituation.FREE);
        if (rooms.size() > 0) {
            Room room = rooms.get(0);
            room.setOrder(reservationCommand.getOrder());
            room.setRoomSituation(RoomSituation.RESERVED);
            roomRepository.save(room);
            return new ReservationResult(room);
        } else {
            return new ReservationResult(reservationCommand.getOrder(), null, null, null,
                    "There are no rooms available");
        }

    }

	public ReservationResult cancelRoom(Integer id, ReservationCommand reservationCommand) {
        Optional<Room> roomResult = roomRepository.findById(id);
        if (roomResult.isPresent() && !roomResult.get().getRoomSituation().equals(RoomSituation.FREE)){
            Room room = roomResult.get();
            room.setOrder(null);
            room.setRoomSituation(RoomSituation.FREE);
            roomRepository.save(room);
            return new ReservationResult(room);
        }else{
            return new ReservationResult(null, id, null, null, "There's no such room to be canceled");
        }
	}

}