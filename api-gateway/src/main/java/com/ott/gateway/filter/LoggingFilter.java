package com.ott.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global Logging Filter — logs every request passing through Gateway.
 *
 * WHY THIS IS IMPORTANT (Interview answer):
 * ──────────────────────────────────────────
 * In microservices, a single user action (e.g. "subscribe") triggers
 * calls across multiple services. Without centralized logging at the
 * gateway, debugging is very hard.
 *
 * This filter logs:
 * - Incoming: method, path, headers
 * - Outgoing: response status code
 *
 * In production (your Disney project), this feeds into
 * ELK Stack (Elasticsearch + Logstash + Kibana) for log analytics.
 *
 * @Order(2) means it runs AFTER JwtAuthFilter (Order 1)
 */
@Component
@Order(2)
public class LoggingFilter implements GlobalFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        // Log incoming request
        log.info("──► GATEWAY REQUEST  | {} {} | from: {}",
                request.getMethod(),
                request.getPath(),
                request.getRemoteAddress());

        long startTime = System.currentTimeMillis();

        // chain.filter = pass to next filter, then execute after response returns
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {

            ServerHttpResponse response = exchange.getResponse();
            long duration = System.currentTimeMillis() - startTime;

            // Log outgoing response
            log.info("◄── GATEWAY RESPONSE | {} {} | status: {} | time: {}ms",
                    request.getMethod(),
                    request.getPath(),
                    response.getStatusCode(),
                    duration);
        }));
    }
}
