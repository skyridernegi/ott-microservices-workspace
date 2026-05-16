package com.ott.subscription.dto;

import com.ott.subscription.model.Subscription;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * RESPONSE DTO — what we send back to the client after subscribing.
 * Clean, only relevant fields. No internal DB fields exposed.
 */
public class SubscriptionResponseDTO {

    private Long subscriptionId;
    private Long userId;
    private String username;
    private String planId;
    private String planName;
    private BigDecimal amountPaid;
    private Subscription.SubscriptionStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean autoRenewal;
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(Long subscriptionId) { this.subscriptionId = subscriptionId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public Subscription.SubscriptionStatus getStatus() { return status; }
    public void setStatus(Subscription.SubscriptionStatus status) { this.status = status; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Boolean getAutoRenewal() { return autoRenewal; }
    public void setAutoRenewal(Boolean autoRenewal) { this.autoRenewal = autoRenewal; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
