// ── PaymentRequestDTO.java ─────────────────────────────
package com.ott.payment.dto;
import java.math.BigDecimal;

/** subscription-service sends this to payment-service via Feign */
public class PaymentRequestDTO {
    private Long userId;
    private String planId;
    private BigDecimal amount;
    private String cardNumber;   // last 4 digits stored only
    private String cardExpiry;
    private String cardCvv;      // never stored in DB

    public Long getUserId() { return userId; }
    public void setUserId(Long v) { userId = v; }
    public String getPlanId() { return planId; }
    public void setPlanId(String v) { planId = v; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal v) { amount = v; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String v) { cardNumber = v; }
    public String getCardExpiry() { return cardExpiry; }
    public void setCardExpiry(String v) { cardExpiry = v; }
    public String getCardCvv() { return cardCvv; }
    public void setCardCvv(String v) { cardCvv = v; }
}
