package com.ott.payment.client;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ott.payment.exception.UserNotFoundException;
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
        // Log the remote service error cleanly using SLF4J
    	log.error("Remote service call failed — Method: {}, Status: {}", methodKey, response.status());

        switch (response.status()) {
            case 404:
                // Map a 404 cleanly to your payment-service custom exception
                return new UserNotFoundException(0L); 
                
            case 400:
                return new IllegalArgumentException("Omsairam---->Invalid request arguments sent via Feign Client.");
                
            case 503:
                return new RuntimeException("Omsairam---->Remote microservice core instance is currently down/unavailable.");
                
            default:
                // Best Practice: Fallback to standard Feign parsing for anything else
                return feign.FeignException.errorStatus(methodKey, response);
        }
    }
}
}