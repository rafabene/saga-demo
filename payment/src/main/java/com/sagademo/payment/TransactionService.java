package com.sagademo.payment;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;

import com.sagademo.payment.entity.Balance;
import com.sagademo.payment.entity.BalanceRepository;
import com.sagademo.payment.entity.InsuficentBalanceException;

@ApplicationScoped
public class TransactionService {

    @Inject
    private BalanceRepository balanceRepository;

    @Transactional
    public Balance processTransaction(Integer account, Transaction transaction)
            throws NoResultException, InsuficentBalanceException {
        Balance balance = balanceRepository.getBalanceByAccount(account);
        switch (transaction.getTransactionType()) {
            case DEPOSIT:
                balance.deposit(transaction.getValue());
                break;
            case WITHDRAW:
                balance.withdraw(transaction.getValue());
                break;
        }
        balanceRepository.save(balance);
        return balance;
    }

}