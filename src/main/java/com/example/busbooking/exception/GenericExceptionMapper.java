package com.example.busbooking.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse( Exception exception ) {
        System.out.println(exception.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Something went wrong.").build();
    }
}
