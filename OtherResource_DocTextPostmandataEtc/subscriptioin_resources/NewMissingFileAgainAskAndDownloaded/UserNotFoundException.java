package com.ott.subscription.exception;

/**
 * Thrown when a user is not found in the database.
 * Example: POST /subscriptions with userId 9999 → user doesn't exist
 * GlobalExceptionHandler catches this and returns 404 response.
 */
public class UserNotFoundException extends RuntimeException {

    private final Long userId;

    public UserNotFoundException(Long userId) {
        super("User not found with id: " + userId);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
