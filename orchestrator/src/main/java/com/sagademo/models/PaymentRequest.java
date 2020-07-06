package com.sagademo.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentRequest {

    @JsonProperty(value = "account")
    private Integer account_id;

    private String transactionIdentifier;

    @JsonProperty("type")
    private TransactionType transactionType;

    @JsonProperty("value")
    private Double value;


    public PaymentRequest() {
    }

    public PaymentRequest(Integer account_id, String transactionIdentifier, TransactionType transactionType, Double value) {
        this.account_id = account_id;
        this.transactionIdentifier = transactionIdentifier;
        this.transactionType = transactionType;
        this.value = value;
    }

    public Integer getAccount() {
        return account_id;
    }

    public void setAccount(Integer account_id) {
        this.account_id = account_id;
    }

    public String getTransactionIdentifier() {
        return transactionIdentifier;
    }

    public void setTransactionIdentifier(String transactionIdentifier) {
        this.transactionIdentifier = transactionIdentifier;
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
        return "{" +
            " account_id='" + getAccount() + "'" +
            ", transactionIdentifier='" + getTransactionIdentifier() + "'" +
            ", transactionType='" + getTransactionType() + "'" +
            ", value='" + getValue() + "'" +
            "}";
    }


    public static enum TransactionType {
        WITHDRAW,
        DEPOSIT
    }
    
}