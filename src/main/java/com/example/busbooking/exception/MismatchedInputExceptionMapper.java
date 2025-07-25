package com.example.busbooking.exception;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;


// When a number is passed instead of a json object, this exception is mapped.
@Provider
public class MismatchedInputExceptionMapper implements ExceptionMapper<MismatchedInputException> {
    @Override
    public Response toResponse(MismatchedInputException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("Invalid request format. Expected a JSON object, but got something else.")
                .build();
    }
}
