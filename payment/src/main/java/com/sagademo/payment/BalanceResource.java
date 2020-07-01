package com.sagademo.payment;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/payment")
@ApplicationScoped
public class BalanceResource {

    @PersistenceContext
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{account}")
    public Response getBalance(@PathParam("account") Integer account){
        try {
            Balance balance = em.createQuery("FROM Balance b WHERE b.account_id = :id", 
            Balance.class)
            .setParameter("id", account)
            .getSingleResult();
            return Response.ok(balance).build();
        } catch (NoResultException e) {
            return Response.status(Status.NOT_FOUND)
                .type(MediaType.TEXT_PLAIN)
                .entity("No Balance for account " + account)
                .build();
        }
    }

    @POST
    @Path("/{account}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response transaction(@PathParam("account") Integer account, Transaction transaction){
        try {
            Balance balance = em.createQuery("FROM Balance b WHERE b.account_id = :id", 
            Balance.class)
            .setParameter("id", account)
            .getSingleResult();
            switch(transaction.getTransactionType()){
                case DEPOSIT:
                    balance.deposit(transaction.getValue());
                    break;
                case WITHDRAW:
                    balance.withdraw(transaction.getValue());
                    break;
            }
            em.persist(balance);
            return Response.ok(balance).build();
        } catch (NoResultException e) {
            return Response.status(Status.NOT_FOUND)
                .type(MediaType.TEXT_PLAIN)
                .entity("No Balance for account " + account)
                .build();
        } catch (InsuficentBalanceException e) {
            return Response.status(Status.NOT_ACCEPTABLE)
                .type(MediaType.TEXT_PLAIN)
                .entity(e.getMessage())
                .build();
        }
    }


    
}