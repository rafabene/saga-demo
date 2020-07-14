package com.sagademo.models;

public class OrderCreatedEvent {

    private Long id;

    private OrderStatus status;

    public OrderCreatedEvent() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "{" + " order='" + getId() + "'" + ", status='" + getStatus() + "'" + "}";
    }

    public static enum OrderStatus {
        PENDING, APPROVED, DENIED
    }

}