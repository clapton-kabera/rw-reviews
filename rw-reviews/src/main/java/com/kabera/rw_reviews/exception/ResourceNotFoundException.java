package com.kabera.rw_reviews.exception;

/** Thrown when a requested entity does not exist; mapped to HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}