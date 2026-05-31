package com.ott.campaign.controller;
import com.ott.campaign.model.Campaign;
import com.ott.campaign.service.CampaignService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;

/**
 * REST endpoints for campaign-service.
 *
 * PUBLIC endpoints (no JWT needed):
 *   GET /campaigns/active → ESPN Offers page
 *
 * PROTECTED endpoints (JWT required via Gateway):
 *   POST /campaigns/apply → user applies promo code at checkout
 *   GET  /campaigns/plan/{planId} → subscription-service calls this
 *   POST /campaigns → admin creates campaign
 *   DELETE /campaigns/{id} → admin deactivates
 *
 * Postman tests:
 *   GET  http://localhost:8080/campaigns/active (no token needed)
 *   POST http://localhost:8080/campaigns/apply (token needed)
 *        Body: {"promoCode":"WELCOME90","userId":1,"planId":"PLAN001","originalPrice":199.0}
 */
@RestController
@RequestMapping("/campaigns")
public class CampaignController {

    private final CampaignService campaignService;
    public CampaignController(CampaignService s) { campaignService = s; }

    /** GET /campaigns/active — PUBLIC, ESPN Offers page calls this */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveCampaigns() {
        return ResponseEntity.ok(campaignService.getActiveCampaigns());
    }

    /** GET /campaigns/plan/{planId} — subscription-service calls via Feign */
    @GetMapping("/plan/{planId}")
    public ResponseEntity<?> getCampaignsForPlan(@PathVariable String planId) {
        return ResponseEntity.ok(campaignService.getCampaignsForPlan(planId));
    }

    /**
     * POST /campaigns/apply
     * User enters promo code at checkout → validates + returns discount
     * Body: {"promoCode":"WELCOME90","userId":1,"planId":"PLAN001","originalPrice":199.0}
     */
    @PostMapping("/apply")
    public ResponseEntity<Map<String, Object>> applyPromoCode(
            @RequestBody Map<String, Object> req) {
        String promoCode = (String) req.get("promoCode");
        Long userId = Long.valueOf(req.get("userId").toString());
        String planId = (String) req.get("planId");
        BigDecimal price = new BigDecimal(req.get("originalPrice").toString());
        return ResponseEntity.ok(campaignService.applyPromoCode(promoCode, userId, planId, price));
    }

    /** POST /campaigns — admin creates a new campaign */
    @PostMapping
    public ResponseEntity<Campaign> createCampaign(@RequestBody Campaign campaign) {
        return ResponseEntity.status(HttpStatus.CREATED).body(campaignService.createCampaign(campaign));
    }

    /** DELETE /campaigns/{id} — admin deactivates (soft delete) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Campaign> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.deactivateCampaign(id));
    }
}