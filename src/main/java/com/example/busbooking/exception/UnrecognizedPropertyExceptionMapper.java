package com.example.busbooking.exception;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

// This exception indicates when an unknown field is seen while deserialization.
@Provider
public class UnrecognizedPropertyExceptionMapper implements ExceptionMapper<UnrecognizedPropertyException> {

    @Override
    public Response toResponse(UnrecognizedPropertyException exception) {
        String unknownField = exception.getPropertyName();
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("Unknown field in JSON: \"" + unknownField + "\"")
                .type("text/plain")
                .build();
    }
}
