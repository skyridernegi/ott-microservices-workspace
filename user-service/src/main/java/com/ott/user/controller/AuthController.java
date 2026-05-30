package com.ott.user.controller;
import com.ott.user.dto.*;
import com.ott.user.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/**
 * PURPOSE: Exposes authentication REST APIs.
 * All endpoints under /auth/** are in PUBLIC_PATHS in JwtAuthFilter
 * so they bypass JWT validation — users can login without a token.
 *
 * Postman: POST http://localhost:8080/auth/login
 * Body: {"username":"john.doe","password":"password123"}
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService s) { authService = s; }

    /**
     * POST /auth/login
     * ESPN UI sends username+password → returns JWT token
     * Test: POST http://localhost:8080/auth/login
     * Body: {"username":"john.doe","password":"password123"}
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
    	System.out.println("incomming....LoginRequest::"+req);
        return ResponseEntity.ok(authService.login(req));
    }

    /**
     * POST /auth/register
     * ESPN UI sends new user data → creates account → returns JWT
     * Test: POST http://localhost:8080/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
    }

    /**
     * GET /auth/me
     * Returns current user details from userId in JWT
     * Test: GET http://localhost:8080/auth/me with Bearer token
     */
    @GetMapping("/me/{userId}")
    public ResponseEntity<?> getMe(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.getUserById(userId));
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getMe() {
        return ResponseEntity.ok(authService.getUsers());
    }
}