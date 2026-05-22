package com.ott.subscription.dto;
import java.math.BigDecimal;
/**
 * PURPOSE: Maps the JSON response from catalog-service
 * into a Java object that SubscriptionService can use.
 *
 * CONNECTS TO:
 * - CatalogServiceClient uses this as the return type
 * - SubscriptionService reads planName, price, duration from this
 *
 * WHY DTO (not Entity): This class lives in subscription-service
 * but represents data from catalog-service database. We never
 * persist this to subscription_db — we just read it and use it.
 *
 * FIELD NAMES must match catalog-service JSON keys exactly:
 * {"planId":"PLAN001","planName":"Disney+ Basic","price":199.0,...}
 */
public class OttPlanDTO {
    private String planId;
    private String planName;
    private BigDecimal price;
    private String duration;   // "MONTHLY" or "YEARLY"
    private Boolean isActive;
    private String description;
    // Default constructor — required by Jackson for JSON deserialization
    public OttPlanDTO() {}
    // Getters and Setters
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}