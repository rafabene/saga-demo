package com.sagademo.payment.entity;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * BalanceRepository
 */
@ApplicationScoped
public class BalanceRepository {

    @PersistenceContext
    private EntityManager em;

    public Balance getBalanceByAccount(int id) throws NoResultException{
        return em.createQuery("FROM Balance b WHERE b.account_id = :id", 
            Balance.class)
        .setParameter("id", id)
        .getSingleResult();
    }

	public void save(Balance balance) {
        em.persist(balance);
	}
}