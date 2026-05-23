package com.ott.catalog.controller;

import com.ott.catalog.model.OttPlan;
import com.ott.catalog.service.CatalogService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Catalog Service.
 *
 * Exposes APIs for:
 * - Fetching OTT plans (used by Subscription Service internally)
 * - Managing plans (admin operations)
 *
 * BASE URL: http://localhost:8081/catalog
 *
 * NOTE: No JWT token needed for catalog-service in our setup.
 * Token validation will be handled by API Gateway (Day 2).
 * For now, all APIs are open so we can test easily via Postman.
 */
@RestController
@RequestMapping("/catalog")
public class CatalogController {

    private static final Logger log = LoggerFactory.getLogger(CatalogController.class);

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    /**
     * GET /catalog/plans
     * Returns all active OTT plans
     * Used by: ESPN website, Subscription Service
     *
     * Postman: GET http://localhost:8081/catalog/plans
     */
    @GetMapping("/plans")
    public ResponseEntity<List<OttPlan>> getAllPlans() {
        log.info("Request received: GET /catalog/plans");
        List<OttPlan> plans = catalogService.getAllActivePlans();
        return ResponseEntity.ok(plans);
    }

    /**
     * GET /catalog/plans/{planId}
     * Returns a specific plan by ID
     * Used by: Subscription Service to validate plan before subscribing
     *
     * Postman: GET http://localhost:8081/catalog/plans/PLAN001
     */
    @GetMapping("/plans/{planId}")
    public ResponseEntity<OttPlan> getPlanById(@PathVariable String planId) {
        log.info("Request received: GET /catalog/plans/{}", planId);
        System.out.println("Envoked omsairam----> http://localhost:8081/catalog/plans//{planId}: planId: " + planId);
        OttPlan plan = catalogService.getPlanById(planId);
        return ResponseEntity.ok(plan);
    }

    /**
     * GET /catalog/plans/duration/{duration}
     * Returns plans filtered by MONTHLY or YEARLY
     *
     * Postman: GET http://localhost:8081/catalog/plans/duration/MONTHLY
     */
    @GetMapping("/plans/duration/{duration}")
    public ResponseEntity<List<OttPlan>> getPlansByDuration(
            @PathVariable OttPlan.Duration duration) {
        log.info("Request received: GET /catalog/plans/duration/{}", duration);
        List<OttPlan> plans = catalogService.getPlansByDuration(duration);
        return ResponseEntity.ok(plans);
    }

    /**
     * POST /catalog/plans
     * Creates a new OTT plan (Admin only)
     *
     * Postman: POST http://localhost:8081/catalog/plans
     * Body (JSON):
     * {
     *   "planId": "PLAN006",
     *   "planName": "Hotstar Premium",
     *   "price": 349.00,
     *   "duration": "MONTHLY",
     *   "description": "Hotstar premium content",
     *   "isActive": true
     * }
     */
    @PostMapping("/plans")
    public ResponseEntity<OttPlan> createPlan(@Valid @RequestBody OttPlan ottPlan) {
        log.info("Request received: POST /catalog/plans - {}", ottPlan.getPlanId());
        OttPlan created = catalogService.createPlan(ottPlan);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /catalog/plans/{planId}
     * Updates an existing plan
     *
     * Postman: PUT http://localhost:8081/catalog/plans/PLAN001
     * Body (JSON):
     * {
     *   "planName": "Disney+ Basic Updated",
     *   "price": 219.00,
     *   "duration": "MONTHLY",
     *   "description": "Updated description",
     *   "isActive": true
     * }
     */
    @PutMapping("/plans/{planId}")
    public ResponseEntity<OttPlan> updatePlan(
            @PathVariable String planId,
            @Valid @RequestBody OttPlan ottPlan) {
        log.info("Request received: PUT /catalog/plans/{}", planId);
        OttPlan updated = catalogService.updatePlan(planId, ottPlan);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /catalog/plans/{planId}
     * Soft deletes (deactivates) a plan — does NOT delete from database
     *
     * Postman: DELETE http://localhost:8081/catalog/plans/PLAN001
     */
    @DeleteMapping("/plans/{planId}")
    public ResponseEntity<String> deactivatePlan(@PathVariable String planId) {
        log.info("Request received: DELETE /catalog/plans/{}", planId);
        catalogService.deactivatePlan(planId);
        return ResponseEntity.ok("Plan " + planId + " deactivated successfully");
    }
}
