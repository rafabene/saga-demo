package com.sagademo.payment.rest;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ProcessingExceptionMapper implements ExceptionMapper<ProcessingException> {

    @Override
    public Response toResponse(ProcessingException exception) {
      return Response
        .status(Status.NOT_ACCEPTABLE)
        .type(MediaType.TEXT_PLAIN)
        .entity(exception.getCause().getMessage())
        .build();
    }
    
}