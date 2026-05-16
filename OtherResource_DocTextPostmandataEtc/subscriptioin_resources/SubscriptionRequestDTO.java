package com.ott.subscription.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO = Data Transfer Object
 *
 * Why use DTO instead of directly using the Entity?
 * - Entity has ALL database fields including internal ones (createdAt, updatedAt etc)
 * - DTO only exposes what the client NEEDS to send
 * - Keeps your API contract clean and secure
 *
 * This is the REQUEST body when a user wants to subscribe.
 * Postman will send this JSON body.
 */
public class SubscriptionRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Plan ID is required")
    private String planId;

    private Boolean autoRenewal = true;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }

    public Boolean getAutoRenewal() { return autoRenewal; }
    public void setAutoRenewal(Boolean autoRenewal) { this.autoRenewal = autoRenewal; }
}
