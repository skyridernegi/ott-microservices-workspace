package com.ott.subscription.exception;

/**
 * Thrown when Catalog Service call fails or plan doesn't exist.
 * This happens during cross-service communication.
 */
public class PlanServiceException extends RuntimeException {
    public PlanServiceException(String message) {
        super(message);
    }
}
