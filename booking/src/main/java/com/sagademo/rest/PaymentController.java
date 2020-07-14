package com.sagademo.rest;

import java.util.List;

import com.sagademo.domain.entity.Room;
import com.sagademo.domain.repository.RoomRepository;
import com.sagademo.domain.service.ReservationService;
import com.sagademo.domain.vo.ReservationCommand;
import com.sagademo.domain.vo.ReservationResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class PaymentController {
 
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationService reservationService;


    @GetMapping("/rooms")
    public List<Room> getRooms(){
        return roomRepository.findAll();
    }

    @PostMapping("/rooms")
    public ResponseEntity<ReservationResult> reserveRoom(@RequestBody ReservationCommand reservationCommand) {
        if (!reservationCommand.getReservationRequest().equals(ReservationCommand.ReservationRequest.RESERVE)){
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "This URL can only accept reservationReques=" + ReservationCommand.ReservationRequest.RESERVE);
        }
        ReservationResult reservationResult = reservationService.processReservationCommand(reservationCommand);
        if (reservationResult.getReservationStatus() == null){
            return new ResponseEntity<ReservationResult>(reservationResult, HttpStatus.NOT_ACCEPTABLE);
        }else{
            return new ResponseEntity<ReservationResult>(reservationResult, HttpStatus.ACCEPTED);           
        }
    }
    
}