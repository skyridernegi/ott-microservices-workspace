package com.ott.subscription.controller;

import com.ott.subscription.dto.SubscriptionRequestDTO;
import com.ott.subscription.dto.SubscriptionResponseDTO;
import com.ott.subscription.model.User;
import com.ott.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Subscription operations.
 * BASE URL: http://localhost:8082/subscriptions
 */
@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionController.class);

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * POST /subscriptions
     * Subscribe a user to an OTT plan.
     * Internally calls Catalog Service to validate plan.
     *
     * Postman: POST http://localhost:8082/subscriptions
     * Body (JSON):
     * {
     *   "userId": 1,
     *   "planId": "PLAN001",
     *   "autoRenewal": true
     * }
     */
    @PostMapping
    public ResponseEntity<SubscriptionResponseDTO> subscribe(
            @Valid @RequestBody SubscriptionRequestDTO request) {
        log.info("POST /subscriptions - userId: {}", request.getUserId());
        SubscriptionResponseDTO response = subscriptionService.subscribe(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /subscriptions/user/{userId}
     * Get all subscriptions for a specific user.
     *
     * Postman: GET http://localhost:8082/subscriptions/user/1
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SubscriptionResponseDTO>> getUserSubscriptions(
            @PathVariable Long userId) {
        log.info("GET /subscriptions/user/{}", userId);
        return ResponseEntity.ok(subscriptionService.getUserSubscriptions(userId));
    }

    /**
     * GET /subscriptions/{subscriptionId}
     * Get a specific subscription by ID.
     *
     * Postman: GET http://localhost:8082/subscriptions/1
     */
    @GetMapping("/{subscriptionId}")
    public ResponseEntity<SubscriptionResponseDTO> getSubscriptionById(
            @PathVariable Long subscriptionId) {
        log.info("GET /subscriptions/{}", subscriptionId);
        return ResponseEntity.ok(subscriptionService.getSubscriptionById(subscriptionId));
    }

    /**
     * PUT /subscriptions/{subscriptionId}/cancel
     * Cancel a subscription.
     *
     * Postman: PUT http://localhost:8082/subscriptions/1/cancel
     */
    @PutMapping("/{subscriptionId}/cancel")
    public ResponseEntity<SubscriptionResponseDTO> cancelSubscription(
            @PathVariable Long subscriptionId) {
        log.info("PUT /subscriptions/{}/cancel", subscriptionId);
        return ResponseEntity.ok(subscriptionService.cancelSubscription(subscriptionId));
    }
}
