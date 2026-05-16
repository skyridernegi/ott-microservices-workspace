package com.ott.subscription.client;

import com.ott.subscription.exception.PlanServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

/**
 * HTTP Client to call Catalog Service APIs.
 *
 * This is how microservices communicate with each other —
 * Subscription Service calls Catalog Service via REST to
 * validate the plan and get the price before subscribing.
 *
 * In your Disney project, services called each other this way.
 * Later (Day 2) we replace this with API Gateway routing.
 */
@Component
public class CatalogClient {

    private static final Logger log = LoggerFactory.getLogger(CatalogClient.class);

    private final RestTemplate restTemplate;

    @Value("${catalog.service.url}")
    private String catalogServiceUrl;

    public CatalogClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Calls GET http://localhost:8081/catalog/plans/{planId}
     * Returns the plan price if found, throws exception if not.
     */
    @SuppressWarnings("unchecked")
    public PlanDetails getPlanDetails(String planId) {
        String url = catalogServiceUrl + "/catalog/plans/" + planId;
        log.info("Calling Catalog Service: GET {}", url);

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null) {
                throw new PlanServiceException("No response from Catalog Service for plan: " + planId);
            }

            PlanDetails details = new PlanDetails();
            details.setPlanId((String) response.get("planId"));
            details.setPlanName((String) response.get("planName"));
            details.setPrice(new BigDecimal(response.get("price").toString()));
            details.setDuration((String) response.get("duration"));
            details.setActive((Boolean) response.get("isActive"));

            log.info("Plan fetched from Catalog: {} - {}", details.getPlanId(), details.getPlanName());
            return details;

        } catch (HttpClientErrorException.NotFound e) {
            throw new PlanServiceException("Plan not found in Catalog Service: " + planId);
        } catch (Exception e) {
            log.error("Error calling Catalog Service: {}", e.getMessage());
            throw new PlanServiceException("Catalog Service unavailable. Please try again later.");
        }
    }

    /**
     * Inner class to hold plan details fetched from Catalog Service
     */
    public static class PlanDetails {
        private String planId;
        private String planName;
        private BigDecimal price;
        private String duration;
        private Boolean isActive;

        public String getPlanId() { return planId; }
        public void setPlanId(String planId) { this.planId = planId; }

        public String getPlanName() { return planName; }
        public void setPlanName(String planName) { this.planName = planName; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }

        public Boolean getActive() { return isActive; }
        public void setActive(Boolean active) { isActive = active; }
    }
}
