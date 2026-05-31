// ── PaymentResponseDTO.java ────────────────────────────
package com.ott.payment.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Returned to subscription-service after payment attempt */
public class PaymentResponseDTO {
    private Long paymentId;
    private String status;        // SUCCESS or FAILED
    private String transactionId;
    private String failureReason;
    private BigDecimal amount;
    private LocalDateTime processedAt;

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long v) { paymentId = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { status = v; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String v) { transactionId = v; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String v) { failureReason = v; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal v) { amount = v; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime v) { processedAt = v; }
}