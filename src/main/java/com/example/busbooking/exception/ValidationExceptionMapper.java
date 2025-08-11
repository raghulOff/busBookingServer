package com.example.busbooking.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;


/**
 * Exception mapper for handling validation errors in the application.

 * This class intercepts any {@link ConstraintViolationException} thrown by the
 * Bean Validation API (JSR 380) — typically when user input fails validation
 * (e.g., @NotNull, @Size, etc. on request DTOs).

 * The mapper ensures that the user receives a 400 Bad Request response
 * with the validation error message in plain text.
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse( ConstraintViolationException exception) {

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}
