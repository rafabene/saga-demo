package com.sagademo.models;

public class ReservationCommand {
    
    private String order;

    private Integer room;

    private ReservationRequest reservationRequest;


    public ReservationCommand() {
    }

    public ReservationCommand(String order, Integer room, ReservationRequest reservationRequest) {
        this.order = order;
        this.room = room;
        this.reservationRequest = reservationRequest;
        switch (reservationRequest) {
            case CANCEL:
            case CONFIRM:
                if (room == null || order == null){
                    throw new IllegalArgumentException(reservationRequest + " requires a room number and a order number");
                }
                break;
            default:
                break;
        }
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

    public ReservationRequest getReservationRequest() {
        return this.reservationRequest;
    }

    public void setReservationRequest(ReservationRequest reservationRequest) {
        this.reservationRequest = reservationRequest;
    }



    @Override
    public String toString() {
        return "{" +
            " order='" + getOrder() + "'" +
            ", room='" + getRoom() + "'" +
            ", reservationRequest='" + getReservationRequest() + "'" +
            "}";
    }


    
    public static enum ReservationRequest{
        RESERVE, CONFIRM, CANCEL
    }


}