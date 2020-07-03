package com.sagademo.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderRequest {

    private Long order;

    private OrderStatus status;

    public OrderRequest() {
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "{" + " order='" + getOrder() + "'" + ", status='" + getStatus() + "'" + "}";
    }

    public static enum OrderStatus {
        PENDING, APPROVED, DENIED
    }

}