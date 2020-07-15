package com.sagademo.domain.vo;

public class ReservationCommand {
    
    private String order;

    private ReservationRequest reservationRequest;


    public ReservationCommand() {
    }

    public ReservationCommand(String order, Integer room, ReservationRequest reservationRequest) {
        this.order = order;
        this.reservationRequest = reservationRequest;
        switch (reservationRequest) {
            case CANCEL:
            case CONFIRM:
                if (room == null || order == null){
                    throw new IllegalArgumentException(reservationRequest + " requires an order number");
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
            ", reservationRequest='" + getReservationRequest() + "'" +
            "}";
    }


    
    public static enum ReservationRequest{
        RESERVE, CONFIRM, CANCEL
    }


}