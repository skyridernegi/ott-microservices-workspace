package com.ott.campaign.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Campaign Entity — represents a promotional campaign.
 *
 * USE CASE: Disney runs a "Disney+ Free for 3 months" promotion
 * during a new Marvel movie launch. This entity stores that campaign.
 *
 * CONNECTS TO:
 * - CampaignController exposes REST APIs for this
 * - CampaignService applies campaigns to subscriptions
 * - subscription-service calls campaign-service via Feign before checkout
 *
 * DISNEY REAL WORLD: This is the same model used for ESPN+
 * promotional campaigns — free trials, bundle discounts, renewal offers.
 */
@Entity
@Table(name = "campaigns")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_id")
    private Long campaignId;

    @NotBlank(message = "Campaign name is required")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    /**
     * Discount percentage applied to plan price.
     * e.g. 100 = 100% off (free), 50 = 50% off, 20 = 20% off
     */
    @NotNull(message = "Discount percent is required")
    @Column(name = "discount_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercent;

    /**
     * Free trial duration in days.
     * If set, user gets this many days free before billing starts.
     * e.g. 90 = 3 months free trial
     */
    @Column(name = "free_trial_days")
    private Integer freeTrialDays;

    @Column(name = "promo_code", length = 20, unique = true)
    private String promoCode;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * Comma-separated plan IDs this campaign applies to.
     * e.g. "PLAN001,PLAN002" or "ALL" for all plans
     */
    @Column(name = "eligible_plans", length = 500)
    private String eligiblePlans;

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "current_uses", nullable = false)
    private Integer currentUses = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "campaign_type", nullable = false)
    private CampaignType campaignType;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum CampaignType {
        FREE_TRIAL,    // First N days free
        DISCOUNT,      // % off the plan price
        BUNDLE,        // Buy one plan get another free
        RENEWAL        // Discount on renewal only
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

    public Campaign() {}

    // Getters and Setters
    public Long getCampaignId() { return campaignId; }
    public void setCampaignId(Long campaignId) { this.campaignId = campaignId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(BigDecimal discountPercent) { this.discountPercent = discountPercent; }
    public Integer getFreeTrialDays() { return freeTrialDays; }
    public void setFreeTrialDays(Integer freeTrialDays) { this.freeTrialDays = freeTrialDays; }
    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getEligiblePlans() { return eligiblePlans; }
    public void setEligiblePlans(String eligiblePlans) { this.eligiblePlans = eligiblePlans; }
    public Integer getMaxUses() { return maxUses; }
    public void setMaxUses(Integer maxUses) { this.maxUses = maxUses; }
    public Integer getCurrentUses() { return currentUses; }
    public void setCurrentUses(Integer currentUses) { this.currentUses = currentUses; }
    public CampaignType getCampaignType() { return campaignType; }
    public void setCampaignType(CampaignType campaignType) { this.campaignType = campaignType; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
