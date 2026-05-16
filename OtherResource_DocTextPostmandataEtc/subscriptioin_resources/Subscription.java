package com.ott.subscription.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a user's OTT subscription.
 * Maps to the 'subscriptions' table in subscription_db.
 *
 * This is the core entity of the entire OTT platform —
 * it tracks who subscribed to what plan, when it expires,
 * and whether auto-renewal is on.
 */
@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long subscriptionId;

    @NotNull(message = "User ID is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Plan ID is required")
    @Column(name = "plan_id", nullable = false, length = 10)
    private String planId;  // References catalog_db.ott_plans (cross-service reference)

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status = SubscriptionStatus.PENDING;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "auto_renewal", nullable = false)
    private Boolean autoRenewal = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Subscription status enum
    public enum SubscriptionStatus {
        PENDING,    // payment not yet confirmed
        ACTIVE,     // currently active subscription
        EXPIRED,    // end_date has passed
        CANCELLED   // user cancelled manually
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Default constructor
    public Subscription() {}

    // Getters and Setters
    public Long getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(Long subscriptionId) { this.subscriptionId = subscriptionId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }

    public SubscriptionStatus getStatus() { return status; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public Boolean getAutoRenewal() { return autoRenewal; }
    public void setAutoRenewal(Boolean autoRenewal) { this.autoRenewal = autoRenewal; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
