package com.sagademo.models;

public class ReservationResult {
    
    private String order;

    private Integer room;

    private Double price;

    private RoomSituation reservationStatus;

    private String exceptionCause;
    
    public static enum RoomSituation{
        RESERVED,
        CONFIRMED,
        FREE
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Integer getRoom() {
        return room;
    }

    public void setRoom(Integer room) {
        this.room = room;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public RoomSituation getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(RoomSituation reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public String getExceptionCause() {
        return exceptionCause;
    }

    public void setExceptionCause(String exceptionCause) {
        this.exceptionCause = exceptionCause;
    }

    @Override
    public String toString() {
        return "ReservationResult [exceptionCause=" + exceptionCause + ", order=" + order + ", price=" + price
                + ", reservationStatus=" + reservationStatus + ", room=" + room + "]";
    }

    
}