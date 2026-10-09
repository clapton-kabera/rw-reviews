package com.kabera.rw_reviews.exception;

/** Thrown for invalid client input that bean validation cannot express; mapped to HTTP 400. */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
