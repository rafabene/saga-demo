package com.sagademo.payment;

import javax.json.bind.annotation.JsonbProperty;

public class Transaction {

    @JsonbProperty(value = "account", nillable = true)
    private Integer account_id;

    private String transactionIdentifier;

    @JsonbProperty("type")
    private TransactionType transactionType;

    @JsonbProperty("value")
    private Double value;


    public Integer getAccount() {
        return account_id;
    }

    public void setAccount(Integer account_id) {
        this.account_id = account_id;
    }

    public void setTransactionIdentifier(String transactionIdentifier) {
        this.transactionIdentifier = transactionIdentifier;
    }

    public String getTransactionIdentifier() {
        return transactionIdentifier;
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