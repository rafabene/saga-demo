package com.sagademo.domain.entity;

import com.sagademo.domain.vo.RoomSituation;

import org.springframework.data.annotation.Id;


public class Room {

    @Id
    private Integer roomNumber;

    private String order;

    private Double price;

    private RoomSituation roomSituation;


    public Room() {
    }

    public Room(Integer roomNumber, String order, Double price, RoomSituation roomSituation) {
        this.roomNumber = roomNumber;
        this.order = order;
        this.price = price;
        this.roomSituation = roomSituation;
    }

    public Integer getRoomNumber() {
        return this.roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public RoomSituation getRoomSituation() {
        return this.roomSituation;
    }

    public void setRoomSituation(RoomSituation roomSituation) {
        this.roomSituation = roomSituation;
    }

  
    @Override
    public String toString() {
        return "{" +
            " roomNumber='" + getRoomNumber() + "'" +
            ", price='" + getPrice() + "'" +
            ", roomSituation='" + getRoomSituation() + "'" +
            "}";
    }

}