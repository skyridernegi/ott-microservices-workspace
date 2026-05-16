package com.ott.subscription.exception;

/**
 * Thrown when a subscription is not found.
 */
public class SubscriptionNotFoundException extends RuntimeException {
    public SubscriptionNotFoundException(Long subscriptionId) {
        super("Subscription not found with id: " + subscriptionId);
    }
}
