//package com.example.busbooking.exception;
//
//import com.fasterxml.jackson.core.JsonParseException;
//import jakarta.ws.rs.core.Response;
//import jakarta.ws.rs.ext.ExceptionMapper;
//import jakarta.ws.rs.ext.Provider;
//
//@Provider
//public class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {
//
//    @Override
//    public Response toResponse(JsonParseException exception) {
//        return Response.status(Response.Status.BAD_REQUEST)
//                .entity("Invalid JSON input: " + exception.getOriginalMessage())
//                .type("text/plain")
//                .build();
//    }
//}
