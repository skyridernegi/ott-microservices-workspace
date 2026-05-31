package com.ott.campaign.service;
import com.ott.campaign.model.*;
import com.ott.campaign.repository.*;
import org.slf4j.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

/**
 * PURPOSE: Campaign business logic.
 *
 * Key methods:
 * getActiveCampaigns() → ESPN Offers page
 * applyPromoCode()     → User applies promo during checkout
 * checkEligibleCampaign() → subscription-service calls this via Feign
 *
 * Disney use case: When user subscribes to PLAN001 with promo "WELCOME90",
 * campaign-service validates the code, checks eligibility, records application.
 */
@Service
public class CampaignService {

    private static final Logger log = LoggerFactory.getLogger(CampaignService.class);
    private final CampaignRepository campaignRepo;
    private final CampaignApplicationRepository applicationRepo;

    public CampaignService(CampaignRepository cr, CampaignApplicationRepository ar) {
        campaignRepo = cr; applicationRepo = ar;
    }

    /** Returns all active campaigns — for ESPN Offers page (public) */
    public List<Campaign> getActiveCampaigns() {
        return campaignRepo.findAllActiveCampaigns();
    }

    /** Returns campaigns applicable to a specific plan (used by subscription-service) */
    public List<Campaign> getCampaignsForPlan(String planId) {
        return campaignRepo.findCampaignsForPlan(planId);
    }

    /**
     * Apply promo code during subscription checkout.
     * Validates: code exists, not expired, not over limit, user hasn't used it.
     * Returns the discount amount to apply.
     */
    @Transactional
    public Map<String, Object> applyPromoCode(String promoCode, Long userId, String planId, BigDecimal originalPrice) {
        Map<String, Object> result = new LinkedHashMap<>();

        Campaign campaign = campaignRepo.findByPromoCode(promoCode.toUpperCase())
            .orElseThrow(() -> new RuntimeException("Invalid promo code: " + promoCode));

        // Validate campaign is still active
        if (!campaign.getIsActive())
            throw new RuntimeException("Promo code is no longer active");

        // Check if user already used this campaign
        if (applicationRepo.existsByCampaignIdAndUserId(campaign.getCampaignId(), userId))
            throw new RuntimeException("You have already used this promo code");

        // Check usage limit
        if (campaign.getMaxUses() != null && campaign.getCurrentUses() >= campaign.getMaxUses())
            throw new RuntimeException("Promo code has reached maximum usage limit");

        // Check plan eligibility
        if (!"ALL".equals(campaign.getEligiblePlans()) &&
            !campaign.getEligiblePlans().contains(planId))
            throw new RuntimeException("Promo code not applicable for this plan");

        // Calculate discount
        BigDecimal discountAmount = originalPrice
            .multiply(campaign.getDiscountPercent())
            .divide(BigDecimal.valueOf(100));
        BigDecimal finalPrice = originalPrice.subtract(discountAmount);

        // Record application
        CampaignApplication application = new CampaignApplication();
        application.setCampaignId(campaign.getCampaignId());
        application.setUserId(userId);
        application.setPlanId(planId);
        application.setDiscountApplied(discountAmount);
        applicationRepo.save(application);

        // Increment usage count
        campaign.setCurrentUses(campaign.getCurrentUses() + 1);
        campaignRepo.save(campaign);

        log.info("Promo {} applied for user {} — discount: {}", promoCode, userId, discountAmount);

        result.put("campaignId", campaign.getCampaignId());
        result.put("campaignName", campaign.getName());
        result.put("originalPrice", originalPrice);
        result.put("discountAmount", discountAmount);
        result.put("finalPrice", finalPrice);
        result.put("freeTrialDays", campaign.getFreeTrialDays());
        result.put("message", "Promo applied! You save ₹" + discountAmount);
        return result;
    }

    /** Create a new campaign (admin only) */
    public Campaign createCampaign(Campaign campaign) {
        return campaignRepo.save(campaign);
    }

    /** Deactivate a campaign (soft delete) */
    @Transactional
    public Campaign deactivateCampaign(Long id) {
        Campaign c = campaignRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Campaign not found"));
        c.setIsActive(false);
        return campaignRepo.save(c);
    }
}