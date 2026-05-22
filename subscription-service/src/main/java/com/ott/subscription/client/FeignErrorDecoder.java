package com.ott.subscription.client;
import com.ott.subscription.exception.PlanServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
/**
 * PURPOSE: Converts HTTP error responses from catalog-service
 * into meaningful Java exceptions.
 *
 * CONNECTS TO:
 * - AppConfig registers this as a bean
 * - Feign calls decode() automatically when catalog returns 4xx/5xx
 * - PlanServiceException is caught by GlobalExceptionHandler → 502
 *
 * WITHOUT THIS: catalog 404 → FeignException (ugly, hard to handle)
 * WITH THIS:    catalog 404 → PlanServiceException (clean, meaningful)
 */
@Component
public class FeignErrorDecoder implements ErrorDecoder {
    private static final Logger log =
        LoggerFactory.getLogger(FeignErrorDecoder.class);
    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Catalog service error — method: {}, status: {}",
            methodKey, response.status());
        switch (response.status()) {
            case 404:
                return new PlanServiceException(
                    "Plan not found in Catalog Service. Check the plan ID.");
            case 400:
                return new PlanServiceException(
                    "Invalid request sent to Catalog Service.");
            case 503:
                return new PlanServiceException(
                    "Catalog Service is currently unavailable.");
            default:
                return new PlanServiceException(
                    "Unexpected error from Catalog Service. Status: "
                    + response.status());
        }
    }
}