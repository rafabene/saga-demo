package com.sagademo.payment.rest;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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

import com.sagademo.payment.Transaction;
import com.sagademo.payment.TransactionService;
import com.sagademo.payment.entity.Balance;
import com.sagademo.payment.entity.BalanceRepository;
import com.sagademo.payment.entity.InsuficentBalanceException;

@Path("/payment")
@ApplicationScoped
public class BalanceResource {

   @Inject
   private BalanceRepository balanceRepository;

   @Inject
   private TransactionService transactionService;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{account}")
    public Response getBalance(@PathParam("account") Integer account){
        try {
            Balance balance = balanceRepository.getBalanceByAccount(account);
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
            Balance balance = transactionService.processTransaction(account, transaction);
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