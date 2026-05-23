package com.ott.subscription.service;

import com.ott.subscription.client.CatalogClient_RestTemplate;
import com.ott.subscription.dto.SubscriptionRequestDTO;
import com.ott.subscription.dto.SubscriptionResponseDTO;
import com.ott.subscription.exception.SubscriptionNotFoundException;
import com.ott.subscription.exception.UserNotFoundException;
import com.ott.subscription.model.Subscription;
import com.ott.subscription.model.User;
import com.ott.subscription.repository.SubscriptionRepository;
import com.ott.subscription.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic for Subscription Service.
 *
 * Key flow when user subscribes:
 * 1. Validate user exists in our DB
 * 2. Call Catalog Service to validate plan + get price
 * 3. Calculate start/end dates
 * 4. Save subscription to DB
 * 5. Return response to client
 */
@Service
public class SubscriptionService_ForRestTemplate {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionService_ForRestTemplate.class);

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final CatalogClient_RestTemplate CatalogClient_RestTemplate;

    public SubscriptionService_ForRestTemplate(SubscriptionRepository subscriptionRepository,
                                UserRepository userRepository,
                                CatalogClient_RestTemplate CatalogClient_RestTemplate) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.CatalogClient_RestTemplate = CatalogClient_RestTemplate;
    }

    /**
     * Create a new subscription.
     * This is the core business method — called when user buys a plan.
     */
    @Transactional
    public SubscriptionResponseDTO subscribe(SubscriptionRequestDTO request) {
        log.info("Processing subscription for userId: {} planId: {}",
                request.getUserId(), request.getPlanId());

        // Step 1: Validate user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(request.getUserId()));

        // Step 2: Call Catalog Service to validate plan and get price
        CatalogClient_RestTemplate.PlanDetails planDetails = CatalogClient_RestTemplate.getPlanDetails(request.getPlanId());

        if (!planDetails.getActive()) {
            throw new RuntimeException("Plan " + request.getPlanId() + " is not active");
        }

        // Step 3: Calculate subscription dates
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = planDetails.getDuration().equals("YEARLY")
                ? startDate.plusYears(1)
                : startDate.plusMonths(1);

        // Step 4: Build and save subscription
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlanId(request.getPlanId());
        subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        subscription.setAmountPaid(planDetails.getPrice());
        subscription.setAutoRenewal(request.getAutoRenewal());

        Subscription saved = subscriptionRepository.save(subscription);
        log.info("Subscription created with id: {}", saved.getSubscriptionId());

        // Step 5: Build and return response DTO
        return buildResponse(saved, planDetails.getPlanName());
    }

    /**
     * Get all subscriptions for a user
     */
    public List<SubscriptionResponseDTO> getUserSubscriptions(Long userId) {
        log.info("Fetching subscriptions for userId: {}", userId);

        // Validate user exists first
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return subscriptionRepository.findByUser_UserId(userId)
                .stream()
                .map(sub -> buildResponse(sub, null))
                .collect(Collectors.toList());
    }

    /**
     * Get a specific subscription by ID
     */
    public SubscriptionResponseDTO getSubscriptionById(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException(subscriptionId));
        return buildResponse(subscription, null);
    }

    /**
     * Cancel a subscription
     */
    @Transactional
    public SubscriptionResponseDTO cancelSubscription(Long subscriptionId) {
        log.info("Cancelling subscription: {}", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException(subscriptionId));

        subscription.setStatus(Subscription.SubscriptionStatus.CANCELLED);
        Subscription updated = subscriptionRepository.save(subscription);

        return buildResponse(updated, null);
    }

    /**
     * Register a new user
     * A transaction is a unit of work that follows:
		
		ACID properties
		A → Atomicity :ALL database operations succeed or everything rollback
		C → Consistency: lets suppose there are multiple interrelated db operation 
		like 1-user inserted 2- subscription details inserted. 
		if 1 fails, 2 should be rolled back, 3- payment detail got failed then every changes should be rolled back 
		otherwise this cause data inconsistency without @transaction
		I → Isolation
		D → Durability
		
		:: so By @Transactional Database remains clean and consistent.
     */
    @Transactional
    public User registerUser(User user) {
        log.info("Registering new user: {}", user.getUsername());
        return userRepository.save(user);
    }

    /**
     * Get user by ID
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    // -------------------------------------------------------
    // Private helper to build SubscriptionResponseDTO
    // -------------------------------------------------------
    private SubscriptionResponseDTO buildResponse(Subscription sub, String planName) {
        SubscriptionResponseDTO response = new SubscriptionResponseDTO();
        response.setSubscriptionId(sub.getSubscriptionId());
        response.setUserId(sub.getUser().getUserId());
        response.setUsername(sub.getUser().getUsername());
        response.setPlanId(sub.getPlanId());
        response.setPlanName(planName != null ? planName : sub.getPlanId());
        response.setAmountPaid(sub.getAmountPaid());
        response.setStatus(sub.getStatus());
        response.setStartDate(sub.getStartDate());
        response.setEndDate(sub.getEndDate());
        response.setAutoRenewal(sub.getAutoRenewal());
        response.setCreatedAt(sub.getCreatedAt());
        return response;
    }
}
