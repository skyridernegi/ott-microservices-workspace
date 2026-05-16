package com.ott.catalog.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;


/**
 * CENTRALIZED EXCEPTION HANDLER for Catalog Service.
 *
 * @RestControllerAdvice means this class handles exceptions
 * thrown by ANY controller in this service — in one place.
 *
 * Without this, you'd need try-catch in every controller method.
 * This is the enterprise standard pattern used in projects like Disney.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(PlanNotFoundException.class)
	public ResponseEntity<ErrorResponse> handlePlanNotFoundException(PlanNotFoundException ex,HttpServletRequest request) {

		/**
		 * Handles: Plan not found (404)
		 * Triggered when: GET /catalog/plans/{planId} and plan doesn't exist
		 */
		log.error("Plan not found: {}", ex.getPlanId());
		log.error(ex.getMessage());
		ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
}
