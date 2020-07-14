package com.sagademo.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sagademo.domain.entity.Room;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ReservationResult {

    private String order;

    private Integer room;

    private Double price;

    private RoomSituation reservationStatus;

    @JsonProperty(required = false)
    private String exceptionCause;

    public ReservationResult() {
    }

    public ReservationResult(Room room) {
        this.order = room.getOrder();
        this.room = room.getRoomNumber();
        this.price = room.getPrice();
        this.reservationStatus = room.getRoomSituation();
    }

    public ReservationResult(String order, Integer room, Double price, RoomSituation reservationStatus,
            String exceptionCause) {
        this.order = order;
        this.room = room;
        this.price = price;
        this.reservationStatus = reservationStatus;
        this.exceptionCause = exceptionCause;
    }

    public String getOrder() {
        return this.order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Integer getRoom() {
        return this.room;
    }

    public void setRoom(Integer room) {
        this.room = room;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public RoomSituation getReservationStatus() {
        return this.reservationStatus;
    }

    public void setReservationStatus(RoomSituation reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public String getExceptionCause() {
        return this.exceptionCause;
    }

    public void setExceptionCause(String exceptionCause) {
        this.exceptionCause = exceptionCause;
    }

    @Override
    public String toString() {
        return "{" + " order='" + getOrder() + "'" + ", room='" + getRoom() + "'" + ", price='" + getPrice() + "'"
                + ", reservationStatus='" + getReservationStatus() + "'" + ", exceptionCause='" + getExceptionCause()
                + "'" + "}";
    }

}