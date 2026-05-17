package com.ott.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway — Main Entry Point
 *
 * WHAT IS API GATEWAY? (Interview answer)
 * ────────────────────────────────────────
 * API Gateway is the single entry point for all client requests.
 * Instead of the client (ESPN website) calling each microservice
 * directly, it calls the Gateway on port 8080.
 * Gateway then routes the request to the correct service.
 *
 * RESPONSIBILITIES of our Gateway:
 * 1. Routing       — /catalog/**  → catalog-service:8081
 *                    /subscriptions/** → subscription-service:8082
 * 2. JWT Auth      — validates token before forwarding request
 * 3. Rate Limiting — max 10 requests/sec per user via Redis
 * 4. Logging       — logs every request/response centrally
 *
 * IMPORTANT: This gateway uses Spring WebFlux (reactive/non-blocking).
 * That is why we use ServerWebExchange instead of HttpServletRequest.
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
