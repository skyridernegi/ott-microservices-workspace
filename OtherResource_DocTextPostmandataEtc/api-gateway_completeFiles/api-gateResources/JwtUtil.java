package com.ott.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JWT Utility — generates and validates JWT tokens.
 *
 * WHAT IS JWT? (Interview answer)
 * ─────────────────────────────────
 * JWT = JSON Web Token. It has 3 parts separated by dots:
 *
 * eyJhbGciOiJIUzI1NiJ9        ← Header  (algorithm used)
 * .eyJ1c2VybmFtZSI6InNreXJpIn0  ← Payload (your data: username, role, expiry)
 * .SflKxwRJSMeKKF2QT4fwpMeJf36  ← Signature (proves token is not tampered)
 *
 * WHY JWT? (Interview answer)
 * ─────────────────────────────
 * Traditional sessions store user data on the SERVER (memory/DB).
 * With 10 microservices, which server holds the session?
 * JWT stores user data IN THE TOKEN ITSELF — stateless.
 * Any service can validate it without calling a central server.
 *
 * FLOW:
 * 1. User calls POST /auth/token with username+password
 * 2. Gateway generates JWT token, returns it to user
 * 3. User sends token in every request: Authorization: Bearer <token>
 * 4. Gateway validates token before forwarding to microservice
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Get signing key from secret string.
     * HMAC-SHA256 algorithm used for signing.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generate JWT token for a user.
     * Called by AuthController when user requests a token.
     *
     * Token contains:
     * - subject: username
     * - role: USER or ADMIN
     * - issuedAt: current time
     * - expiration: current time + 1 hour
     */
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate JWT token.
     * Returns true if token is valid and not expired.
     * Returns false if token is tampered or expired.
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // expired, tampered, or invalid
        }
    }

    /**
     * Extract username from token.
     * Used by JWT filter to identify who is making the request.
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Extract role from token.
     */
    public String extractRole(String token) {
        return (String) extractClaims(token).get("role");
    }

    /**
     * Extract all claims (payload data) from token.
     */
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
