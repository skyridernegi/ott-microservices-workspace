package com.ott.subscription.client;

import com.ott.subscription.dto.OttPlanDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * PURPOSE: Declares which catalog-service APIs this service needs to call.
 * Feign reads this interface at startup and creates an HTTP client
 * implementation automatically — no code needed from us.
 *
 * CONNECTS TO:
 * - SubscriptionService injects and calls this interface
 * - FeignErrorDecoder handles errors thrown by this client
 * - application.properties provides the URL via ${catalog.service.url}
 *
 * @FeignClient params:
 *   name = logical name (used for load balancing if Eureka is added later)
 *   url  = base URL read from application.properties
 *         In production this would be replaced by service discovery
 */
@FeignClient(name = "catalog-service", url = "${catalog.service.url}")
public interface CatalogServiceClient {

    /**
     * Calls: GET http://localhost:8081/catalog/plans/{planId}
     *
     * Used by: SubscriptionService.subscribe() to validate plan
     * and get price before creating subscription.
     *
     * If plan not found → catalog returns 404
     * → FeignErrorDecoder converts it to PlanServiceException
     * → GlobalExceptionHandler returns 502 to client
     */
    @GetMapping("/catalog/plans/{planId}")
    OttPlanDTO getPlanById(@PathVariable("planId") String planId);
}