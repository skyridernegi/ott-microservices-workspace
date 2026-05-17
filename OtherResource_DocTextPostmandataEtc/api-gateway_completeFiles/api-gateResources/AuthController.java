package com.ott.gateway.controller;

import com.ott.gateway.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Auth Controller — generates JWT tokens for testing.
 *
 * WHY IS THIS IN THE GATEWAY? (Interview answer)
 * ───────────────────────────────────────────────
 * In production, authentication is a separate microservice
 * (auth-service) with its own database of users and passwords.
 *
 * For our learning project, we put a simple token generator
 * inside the Gateway itself so we can test without building
 * a full auth-service.
 *
 * In real Disney project:
 * POST /auth/login → auth-service validates credentials → returns JWT
 *
 * In our project:
 * POST /auth/token → Gateway generates JWT directly (no password check)
 *
 * ENDPOINTS:
 * POST /auth/token  → generate new token
 * POST /auth/validate → check if a token is valid (useful for testing)
 *
 * NOTE: These are PUBLIC paths — no JWT needed to call them.
 * (See JwtAuthFilter.PUBLIC_PATHS list)
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * POST /auth/token
     * Generate a JWT token for the given username and role.
     *
     * Postman: POST http://localhost:8080/auth/token
     * Body (JSON):
     * {
     *   "username": "john.doe",
     *   "role": "USER"
     * }
     *
     * Response:
     * {
     *   "token": "eyJhbGciOiJIUzI1NiJ9...",
     *   "username": "john.doe",
     *   "role": "USER",
     *   "expiresIn": "1 hour"
     * }
     *
     * COPY the token from response → use in Authorization header for all other calls.
     */
    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> generateToken(
            @RequestBody Map<String, String> request) {

        String username = request.get("username");
        String role = request.getOrDefault("role", "USER");

        if (username == null || username.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "username is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        String token = jwtUtil.generateToken(username, role);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("username", username);
        response.put("role", role);
        response.put("expiresIn", "1 hour");
        response.put("usage", "Add to header: Authorization: Bearer " + token);

        return ResponseEntity.ok(response);
    }

    /**
     * POST /auth/validate
     * Check if a token is valid — useful for debugging in Postman.
     *
     * Postman: POST http://localhost:8080/auth/validate
     * Body (JSON):
     * {
     *   "token": "eyJhbGciOiJIUzI1NiJ9..."
     * }
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(
            @RequestBody Map<String, String> request) {

        String token = request.get("token");
        boolean valid = jwtUtil.isTokenValid(token);

        Map<String, Object> response = new HashMap<>();
        response.put("valid", valid);

        if (valid) {
            response.put("username", jwtUtil.extractUsername(token));
            response.put("role", jwtUtil.extractRole(token));
            response.put("message", "Token is valid");
        } else {
            response.put("message", "Token is invalid or expired");
        }

        return ResponseEntity.ok(response);
    }
}
