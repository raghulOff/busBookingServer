//
//
//package com.example.busbooking.exception;
//import jakarta.ws.rs.ProcessingException;
//import jakarta.ws.rs.core.Response;
//import jakarta.ws.rs.ext.ExceptionMapper;
//import jakarta.ws.rs.ext.Provider;
//
//@Provider
//public class JsonDeserializationExceptionMapper implements ExceptionMapper<ProcessingException> {
//
//    @Override
//    public Response toResponse(ProcessingException exception) {
//        if (exception != null) {
//            return Response.status(Response.Status.BAD_REQUEST)
//                    .entity("Invalid JSON input.")
//                    .type("text/plain")
//                    .build();
//        }
//
//        return null;
//    }
//}
