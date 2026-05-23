package com.ott.subscription.service;
import com.ott.subscription.client.CatalogServiceClient;
import com.ott.subscription.dto.OttPlanDTO;
import com.ott.subscription.dto.SubscriptionRequestDTO;
import com.ott.subscription.dto.SubscriptionResponseDTO;
import com.ott.subscription.exception.PlanServiceException;
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
 * PURPOSE: Business logic for all subscription operations.
 *
 * CONNECTS TO:
 * - SubscriptionController calls this (receives HTTP requests)
 * - CatalogServiceClient (Feign) is called to validate plan
 * - SubscriptionRepository saves/reads subscription data
 * - UserRepository validates user exists before subscribing
 *
 * KEY CHANGE FROM RestTemplate:
 * OLD: restTemplate.getForObject(url, Map.class) — manual, verbose
 * NEW: catalogServiceClient.getPlanById(id) — clean, one line
 */
@Service
public class SubscriptionService {
    private static final Logger log =
        LoggerFactory.getLogger(SubscriptionService.class);
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final CatalogServiceClient catalogServiceClient; // Feign client
    // Constructor injection — Spring injects Feign proxy automatically
    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            UserRepository userRepository,
            CatalogServiceClient catalogServiceClient) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.catalogServiceClient = catalogServiceClient;
    }
    /**
     * Subscribe user to an OTT plan.
     * FLOW: validate user → call catalog via Feign → save subscription
     */
    @Transactional
    public SubscriptionResponseDTO subscribe(SubscriptionRequestDTO request) {
        log.info("Processing subscription — userId:{} planId:{}",
            request.getUserId(), request.getPlanId());
        // Step 1: Validate user exists in subscription_db
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new UserNotFoundException(request.getUserId()));
        // Step 2: Call catalog-service via Feign — ONE clean line
        // Feign builds: GET http://localhost:8081/catalog/plans/{planId}
        // Maps JSON response to OttPlanDTO automatically
        // If catalog returns 404 → FeignErrorDecoder throws PlanServiceException
        OttPlanDTO plan = catalogServiceClient.getPlanById(request.getPlanId());
        // Step 3: Validate plan is active
        if (!plan.getIsActive()) {
            throw new PlanServiceException(
                "Plan " + request.getPlanId() + " is currently inactive.");
        }
        // Step 4: Calculate end date based on plan duration
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = "YEARLY".equals(plan.getDuration())
            ? startDate.plusYears(1)
            : startDate.plusMonths(1);
        // Step 5: Build and save subscription
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlanId(request.getPlanId());
        subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        subscription.setAmountPaid(plan.getPrice());
        subscription.setAutoRenewal(request.getAutoRenewal());
        Subscription saved = subscriptionRepository.save(subscription);
        log.info("Subscription created — id:{}", saved.getSubscriptionId());
        return buildResponse(saved, plan.getPlanName());
    }
    public List<SubscriptionResponseDTO> getUserSubscriptions(Long userId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        return subscriptionRepository.findByUser_UserId(userId)
            .stream()
            .map(sub -> buildResponse(sub, sub.getPlanId()))
            .collect(Collectors.toList());
    }
    public SubscriptionResponseDTO getSubscriptionById(Long subscriptionId) {
        Subscription sub = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new SubscriptionNotFoundException(subscriptionId));
        return buildResponse(sub, sub.getPlanId());
    }
    @Transactional
    public SubscriptionResponseDTO cancelSubscription(Long subscriptionId) {
        Subscription sub = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new SubscriptionNotFoundException(subscriptionId));
        sub.setStatus(Subscription.SubscriptionStatus.CANCELLED);
        return buildResponse(subscriptionRepository.save(sub), sub.getPlanId());
    }
    @Transactional
    public User registerUser(User user) {
        return userRepository.save(user);
    }
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }
    // Builds the clean response DTO from Subscription entity
    private SubscriptionResponseDTO buildResponse(Subscription sub, String planName) {
        SubscriptionResponseDTO res = new SubscriptionResponseDTO();
        res.setSubscriptionId(sub.getSubscriptionId());
        res.setUserId(sub.getUser().getUserId());
        res.setUsername(sub.getUser().getUsername());
        res.setPlanId(sub.getPlanId());
        res.setPlanName(planName);
        res.setAmountPaid(sub.getAmountPaid());
        res.setStatus(sub.getStatus());
        res.setStartDate(sub.getStartDate());
        res.setEndDate(sub.getEndDate());
        res.setAutoRenewal(sub.getAutoRenewal());
        res.setCreatedAt(sub.getCreatedAt());
        return res;
    }
}