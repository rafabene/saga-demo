package com.sagademo.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentRequest {

    @JsonProperty(value = "account")
    private Integer account_id;

    @JsonProperty("type")
    private TransactionType transactionType;

    @JsonProperty("value")
    private Double value;


    public PaymentRequest() {
    }

    public PaymentRequest(Integer account_id, TransactionType transactionType, Double value) {
        this.account_id = account_id;
        this.transactionType = transactionType;
        this.value = value;
    }

    public Integer getAccount() {
        return account_id;
    }

    public void setAccount(Integer account_id) {
        this.account_id = account_id;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public Double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("Transaction (%d, %s, %.2f)", 
                this.account_id, this.transactionType, this.value);
    }

    public static enum TransactionType {
        WITHDRAW,
        DEPOSIT
    }
    
}