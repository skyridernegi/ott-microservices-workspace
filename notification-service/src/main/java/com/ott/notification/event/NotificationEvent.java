package com.ott.notification.event;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Common event model. Kafka messages are deserialized into this class.
 * payment-service and subscription-service serialize their events
 * using the same structure.
 *
 * eventType values:
 *   PAYMENT_SUCCESS      → "Your payment was successful"
 *   PAYMENT_FAILED       → "Your payment failed, please retry"
 *   SUBSCRIPTION_EXPIRED → "Your subscription has expired"
 *   SUBSCRIPTION_EXPIRING→ "Your subscription expires in 3 days"
 *   SUBSCRIPTION_CREATED → "Welcome! Your subscription is active"
 */
public class NotificationEvent {
    private String eventType;
    private Long userId;
    private String username;
    private String email;
    private String planId;
    private String planName;
    private BigDecimal amount;
    private String failureReason;
    private LocalDateTime eventTime;

    public NotificationEvent() {}

    public String getEventType() { return eventType; }
    public void setEventType(String v) { eventType = v; }
    public Long getUserId() { return userId; }
    public void setUserId(Long v) { userId = v; }
    public String getUsername() { return username; }
    public void setUsername(String v) { username = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }
    public String getPlanId() { return planId; }
    public void setPlanId(String v) { planId = v; }
    public String getPlanName() { return planName; }
    public void setPlanName(String v) { planName = v; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal v) { amount = v; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String v) { failureReason = v; }
    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime v) { eventTime = v; }
}