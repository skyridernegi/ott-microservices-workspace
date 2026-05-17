package com.ott.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * Rate Limiter Configuration.
 *
 * WHAT IS RATE LIMITING? (Interview answer)
 * ──────────────────────────────────────────
 * Rate limiting controls how many requests a user/IP can make
 * in a given time window.
 *
 * Example: Max 10 requests per second per user.
 * If user fires 15 requests in 1 second:
 *   - First 10 → processed normally (200 OK)
 *   - Next  5  → rejected immediately (429 Too Many Requests)
 *
 * WHY RATE LIMITING? (Interview answer)
 * ───────────────────────────────────────
 * 1. Prevents DDoS attacks
 * 2. Prevents one user from hogging all server resources
 * 3. Protects downstream microservices from being overwhelmed
 * 4. Enforces fair usage across all users
 *
 * HOW IT WORKS WITH REDIS:
 * ─────────────────────────
 * Uses "Token Bucket" algorithm:
 * - Redis stores a "bucket" per user
 * - Bucket refills at replenishRate (10 tokens/sec)
 * - Each request consumes 1 token
 * - If bucket is empty → 429 Too Many Requests
 *
 * KEY RESOLVER = decides WHO to rate limit by.
 * We rate limit by username (from JWT, set by JwtAuthFilter).
 * If no username, falls back to client IP address.
 */
@Configuration
public class RateLimiterConfig {

    /**
     * userKeyResolver — rate limit per logged-in username.
     *
     * JwtAuthFilter adds X-Username header to every request.
     * This resolver reads that header as the rate limit key.
     * So each USER gets their own 10 req/sec bucket in Redis.
     *
     * Example:
     * - john.doe  → his own bucket → 10 req/sec
     * - jane.smith → her own bucket → 10 req/sec
     * They don't share limits.
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // Read X-Username header set by JwtAuthFilter
            String username = exchange.getRequest()
                    .getHeaders()
                    .getFirst("X-Username");

            if (username != null && !username.isEmpty()) {
                return Mono.just(username); // rate limit by username
            }

            // Fallback: rate limit by IP address
            return Mono.just(
                exchange.getRequest()
                    .getRemoteAddress()
                    .getAddress()
                    .getHostAddress()
            );
        };
    }
}
