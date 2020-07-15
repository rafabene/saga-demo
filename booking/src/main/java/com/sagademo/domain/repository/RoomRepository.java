package com.sagademo.domain.repository;

import java.util.List;

import com.sagademo.domain.entity.Room;
import com.sagademo.domain.vo.RoomSituation;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomRepository extends MongoRepository<Room, Integer>{

    public List<Room> findByRoomSituation(RoomSituation roomSituation);

    public List<Room> findByRoomSituationAndOrder(RoomSituation roomSituation, String order);
    
}