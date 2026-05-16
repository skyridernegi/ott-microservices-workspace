package com.ott.catalog.exception;

import java.time.LocalDateTime;

public class ErrorResponse {

	private int status;           // HTTP status code e.g. 404, 400, 500
    private String error;         // Short error type e.g. "Not Found"
    private String message;       // Detailed message e.g. "Plan not found with id: PLAN999"
    private String path;          // Which API was called e.g. "/catalog/plans/PLAN999"
    private LocalDateTime timestamp; // When the error occurred
	
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
