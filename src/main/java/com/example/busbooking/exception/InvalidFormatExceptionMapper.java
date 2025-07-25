package com.example.busbooking.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

// This exception is mapped when field assignment in the dto is invalid. (i.e int x = "10")

@Provider
public class InvalidFormatExceptionMapper implements ExceptionMapper<InvalidFormatException> {

    @Override
    public Response toResponse(InvalidFormatException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("Invalid value: " + exception.getOriginalMessage())
                .type("text/plain")
                .build();
    }
}
