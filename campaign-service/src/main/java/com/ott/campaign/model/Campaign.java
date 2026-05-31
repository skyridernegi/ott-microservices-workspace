package com.ott.campaign.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;

/**
 * PURPOSE: Represents a promotional campaign.
 * Your Disney experience: campaigns drive ESPN+ free trials,
 * bundle discounts, and renewal offers.
 */
@Entity @Table(name = "campaigns")
public class Campaign {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long campaignId;

    @Column(nullable = false) private String name;
    private String description;

    @Column(name = "discount_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercent = BigDecimal.ZERO;

    @Column(name = "free_trial_days")
    private Integer freeTrialDays = 0;

    @Column(name = "promo_code", unique = true) private String promoCode;

    @Column(name = "start_date", nullable = false) private LocalDate startDate;
    @Column(name = "end_date",   nullable = false) private LocalDate endDate;
    @Column(name = "eligible_plans")               private String eligiblePlans = "ALL";
    @Column(name = "max_uses")                       private Integer maxUses;
    @Column(name = "current_uses", nullable = false) private Integer currentUses = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "campaign_type", nullable = false)
    private CampaignType campaignType = CampaignType.DISCOUNT;

    @Column(name = "is_active", nullable = false) private Boolean isActive = true;

    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;

    public enum CampaignType { FREE_TRIAL, DISCOUNT, BUNDLE, RENEWAL }

    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate  protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public Campaign() {}
    public Long getCampaignId() { return campaignId; }
    public void setCampaignId(Long v) { campaignId = v; }
    public String getName() { return name; }
    public void setName(String v) { name = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { description = v; }
    public BigDecimal getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(BigDecimal v) { discountPercent = v; }
    public Integer getFreeTrialDays() { return freeTrialDays; }
    public void setFreeTrialDays(Integer v) { freeTrialDays = v; }
    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String v) { promoCode = v; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate v) { startDate = v; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate v) { endDate = v; }
    public String getEligiblePlans() { return eligiblePlans; }
    public void setEligiblePlans(String v) { eligiblePlans = v; }
    public Integer getMaxUses() { return maxUses; }
    public void setMaxUses(Integer v) { maxUses = v; }
    public Integer getCurrentUses() { return currentUses; }
    public void setCurrentUses(Integer v) { currentUses = v; }
    public CampaignType getCampaignType() { return campaignType; }
    public void setCampaignType(CampaignType v) { campaignType = v; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean v) { isActive = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}