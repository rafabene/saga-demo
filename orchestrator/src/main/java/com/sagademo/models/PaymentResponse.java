package com.sagademo.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentResponse {

    @JsonProperty(value = "accountId")
    private Integer account_id;

    private Double requestedValue;

    private ResultType resultType;

    @JsonProperty(required = false)
    private String cause;


    public PaymentResponse() {
    }

    public PaymentResponse(Integer account_id, Double requestedValue, ResultType resultType, String cause) {
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