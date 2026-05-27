package com.ott.gateway.filter;

import com.ott.gateway.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * JWT Authentication Filter — runs on EVERY request.
 *
 * HOW IT WORKS (Interview answer):
 * ──────────────────────────────────
 * 1. Request arrives at Gateway (port 8080)
 * 2. This filter runs FIRST (Order = 1, highest priority)
 * 3. If path is /auth/** → skip validation (public endpoint)
 * 4. Otherwise → check Authorization header
 * 5. Extract token → validate with JwtUtil
 * 6. If valid → add username to request header → forward to service
 * 7. If invalid/missing → return 401 Unauthorized — request NEVER reaches service
 *
 * WHY GlobalFilter?
 * ─────────────────
 * GlobalFilter applies to ALL routes automatically.
 * We don't need to add it to each route in application.yml.
 *
 * WHY Mono<Void>?
 * ────────────────
 * Gateway uses reactive programming (WebFlux).
 * Mono = a stream that returns 0 or 1 result asynchronously.
 * chain.filter(exchange) = "pass this request to next filter/service"
 */
@Component
@Order(1) // Run this filter first among all filters
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    // These paths skip JWT validation — public endpoints
    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/token",
            "/auth/refresh",
            "/actuator"    // ADD THIS — Prometheus scrapes without JWT token
    );

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();

        log.debug("Gateway received request: {} {}", request.getMethod(), path);

        // Step 1: Check if this is a public path — skip JWT validation
        if (isPublicPath(path)) {
            log.debug("Public path — skipping JWT validation: {}", path);
            return chain.filter(exchange); // pass through without checking token
        }

        // Step 2: Check if Authorization header exists
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete(); // Stop — return 401
        }

        // Step 3: Extract token (remove "Bearer " prefix)
        String token = authHeader.substring(7);

        // Step 4: Validate token
        if (!jwtUtil.isTokenValid(token)) {
            log.warn("Invalid or expired JWT token for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete(); // Stop — return 401
        }

        // Step 5: Token is valid — extract username and add to request header
        // Downstream services (catalog, subscription) can read X-Username header
        // to know which user is making the request
        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);

        log.debug("JWT valid — user: {}, role: {}, path: {}", username, role, path);

        ServerHttpRequest modifiedRequest = exchange.getRequest()
                .mutate()
                .header("X-Username", username) // forwarded to microservice
                .header("X-Role", role)          // forwarded to microservice
                .build();

        // Step 6: Pass the modified request to the next filter/service
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    @Override
    public int getOrder() {
        return 1; // Priority 1 = runs first
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }
}
