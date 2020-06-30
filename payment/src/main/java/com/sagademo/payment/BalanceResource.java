package com.sagademo.payment;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/payment")
@ApplicationScoped
public class BalanceResource {

    @PersistenceContext
    private EntityManager em;

    @Path("/{account}")
    @GET
    public Response getBalance(@PathParam("account") Integer account){
        System.out.println(account);
        List<Balance> balances = em.createQuery("FROM Balance b", Balance.class).getResultList();
        return Response.ok(balances).build();
    }
    
}