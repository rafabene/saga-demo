package com.sagademo.payment.entity;

public class InsuficentBalanceException extends Exception {

	private static final long serialVersionUID = 1L;

    public InsuficentBalanceException(Double balance, double value) {
        super(String.format(
            "Account has a balance of %.2f, but it tried to withdraw %.2f", 
            balance, value));
	}

}
