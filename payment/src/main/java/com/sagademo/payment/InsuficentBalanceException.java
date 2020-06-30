package com.sagademo.payment;

public class InsuficentBalanceException extends Exception {

	private static final long serialVersionUID = 1L;

    public InsuficentBalanceException(Double balance, double value) {
        super(String.format(
            "Account has a balance of %d, but it tried to with withdraw %d", 
            balance, value));
	}

}
