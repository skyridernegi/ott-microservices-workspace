package com.ott.subscription.exception;

/**
 * Thrown when a subscription record is not found in the database.
 * Example: GET /subscriptions/999 → subscription doesn't exist
 * GlobalExceptionHandler catches this and returns 404 response.
 */
public class SubscriptionNotFoundException extends RuntimeException {

    private final Long subscriptionId;

    public SubscriptionNotFoundException(Long subscriptionId) {
        super("Subscription not found with id: " + subscriptionId);
        this.subscriptionId = subscriptionId;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }
}
