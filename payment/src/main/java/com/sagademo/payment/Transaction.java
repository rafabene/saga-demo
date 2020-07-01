package com.sagademo.payment;

import javax.json.bind.annotation.JsonbProperty;

public class Transaction {

    @JsonbProperty("type")
    private TransactionType transactionType;

    @JsonbProperty("value")
    private Double value;

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
        return String.format("Transaction (%s, %d)", 
                this.transactionType, this.value);
    }
    
}