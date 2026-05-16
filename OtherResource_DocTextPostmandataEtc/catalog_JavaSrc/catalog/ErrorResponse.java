package com.ott.catalog.exception;

import java.time.LocalDateTime;

/**
 * Standard error response structure returned to the client
 * whenever an exception occurs.
 *
 * All error responses across the entire service follow this format
 * so the frontend (ESPN portal) always knows what to expect.
 */
public class ErrorResponse {

    private int status;           // HTTP status code e.g. 404, 400, 500
    private String error;         // Short error type e.g. "Not Found"
    private String message;       // Detailed message e.g. "Plan not found with id: PLAN999"
    private String path;          // Which API was called e.g. "/catalog/plans/PLAN999"
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
