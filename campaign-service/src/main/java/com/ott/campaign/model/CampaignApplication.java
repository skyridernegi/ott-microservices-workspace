package com.ott.campaign.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Tracks which user applied which campaign — prevents reuse */
@Entity @Table(name = "campaign_applications",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "campaign_id"}))
public class CampaignApplication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;
    @Column(name = "campaign_id", nullable = false) private Long campaignId;
    @Column(name = "user_id",     nullable = false) private Long userId;
    @Column(name = "plan_id",     nullable = false) private String planId;
    @Column(name = "discount_applied", precision = 10, scale = 2) private BigDecimal discountApplied;
    @Column(name = "applied_at", updatable = false) private LocalDateTime appliedAt;
    @PrePersist protected void onCreate() { appliedAt = LocalDateTime.now(); }
    public CampaignApplication() {}
    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long v) { applicationId = v; }
    public Long getCampaignId() { return campaignId; }
    public void setCampaignId(Long v) { campaignId = v; }
    public Long getUserId() { return userId; }
    public void setUserId(Long v) { userId = v; }
    public String getPlanId() { return planId; }
    public void setPlanId(String v) { planId = v; }
    public BigDecimal getDiscountApplied() { return discountApplied; }
    public void setDiscountApplied(BigDecimal v) { discountApplied = v; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
}