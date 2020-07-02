package com.sagademo.payment;

import javax.json.bind.annotation.JsonbProperty;

public class TransactionResult {

    @JsonbProperty(value = "accountId", nillable = true)
    private Integer account_id;

    private Double requestedValue;

    private ResultType resultType;

    @JsonbProperty(nillable = true)
    private String cause;


    public TransactionResult() {
    }

    public TransactionResult(Integer account_id, Double requestedValue, ResultType resultType, String cause) {
        this.account_id = account_id;
        this.requestedValue = requestedValue;
        this.resultType = resultType;
        this.cause = cause;
    }

    public Integer getAccount_id() {
        return this.account_id;
    }

    public void setAccount_id(Integer account_id) {
        this.account_id = account_id;
    }

    public Double getRequestedValue() {
        return this.requestedValue;
    }

    public void setRequestedValue(Double requestedValue) {
        this.requestedValue = requestedValue;
    }

    public ResultType getResultType() {
        return this.resultType;
    }

    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    public String getCause() {
        return this.cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public TransactionResult account_id(Integer account_id) {
        this.account_id = account_id;
        return this;
    }

    public TransactionResult requestedValue(Double requestedValue) {
        this.requestedValue = requestedValue;
        return this;
    }

    public TransactionResult resultType(ResultType resultType) {
        this.resultType = resultType;
        return this;
    }

    public TransactionResult cause(String cause) {
        this.cause = cause;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
            " account_id='" + getAccount_id() + "'" +
            ", requestedValue='" + getRequestedValue() + "'" +
            ", resultType='" + getResultType() + "'" +
            ", cause='" + getCause() + "'" +
            "}";
    }
    

    public static enum ResultType{
        APPROVED,
        DENIED
    }
    
}