package com.sagademo.payment.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "balance")
public class Balance {

    @Id
    @Column(name = "account_id")
    private Integer account_id;

    @NotNull
    @Column(name = "balance")
    private Double balance;

    public Integer getAccount_id() {
        return account_id;
    }

    public Double getBalance() {
        return balance;
    }

    public void deposit(double value)    {
        balance = balance + value;
    }

    public void withdraw(double value) throws InsuficentBalanceException {
        if ((balance.doubleValue() - value) < 0){
            throw new InsuficentBalanceException(balance, value);
        }
        balance = balance - value;
    }
}