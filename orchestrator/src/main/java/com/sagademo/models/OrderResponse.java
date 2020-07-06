package com.sagademo.models;

public class OrderResponse {

    private Long id;

    private OrderStatus status;

    private String cause;

    public OrderResponse() {

    }

    public OrderResponse(Long id, OrderStatus status, String cause) {
        this.id = id;
        this.status = status;
        if (status == OrderStatus.CANCELED && cause == null){
            throw new IllegalArgumentException("Cause must be informed for status = " + OrderStatus.CANCELED);
        }
        this.cause = cause;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public static enum OrderStatus {
        CONFIRMED, CANCELED
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", status='" + getStatus() + "'" +
            ", cause='" + getCause() + "'" +
            "}";
    }
    
}