package com.ott.payment.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PURPOSE: Records every payment attempt in payment_db.
 * Even failed payments are stored — for retry logic and audit trail.
 */
@Entity @Table(name = "payments")
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false) private String planId;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    private String paymentMethod = "CARD";
    private String transactionId;
    private String failureReason;
    private String cardLastFour;

    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;

    public enum PaymentStatus { PENDING, SUCCESS, FAILED, REFUNDED }

    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public Payment() {}
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long v) { paymentId = v; }
    public Long getUserId() { return userId; }
    public void setUserId(Long v) { userId = v; }
    public String getPlanId() { return planId; }
    public void setPlanId(String v) { planId = v; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal v) { amount = v; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus v) { status = v; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String v) { paymentMethod = v; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String v) { transactionId = v; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String v) { failureReason = v; }
    public String getCardLastFour() { return cardLastFour; }
    public void setCardLastFour(String v) { cardLastFour = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}