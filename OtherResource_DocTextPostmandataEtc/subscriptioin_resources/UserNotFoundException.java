package com.ott.subscription.exception;

/**
 * Thrown when a user is not found in the database.
 */
public class UserNotFoundException extends RuntimeException {
    private final Long userId;

    public UserNotFoundException(Long userId) {
        super("User not found with id: " + userId);
        this.userId = userId;
    }

    public Long getUserId() { return userId; }
}
