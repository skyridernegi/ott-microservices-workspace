package com.ott.catalog.exception;

/**
 * Thrown when a requested OTT plan is not found in the database.
 * Example: GET /catalog/plans/PLAN999 → plan doesn't exist
 */
public class PlanNotFoundException extends RuntimeException {

    private final String planId;

    public PlanNotFoundException(String planId) {
        super("OTT Plan not found with id: " + planId);
        this.planId = planId;
    }

    public String getPlanId() {
        return planId;
    }
}
