package com.sagademo.models;

public class OrderCommand {

    private Long id;

    private OrderRequest status;

    private String cause;

    public OrderCommand() {

    }

    public OrderCommand(Long id, OrderRequest status, String cause) {
        this.id = id;
        this.status = status;
        if (status == OrderRequest.CANCEL && cause == null){
            throw new IllegalArgumentException("Cause must be informed for status = " + OrderRequest.CANCEL);
        }
        this.cause = cause;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderRequest getStatus() {
        return status;
    }

    public void setStatus(OrderRequest status) {
        this.status = status;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public static enum OrderRequest {
        CONFIRM, CANCEL
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