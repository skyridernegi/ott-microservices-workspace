package com.ott.subscription.controller;

import com.ott.subscription.model.User;
import com.ott.subscription.service.SubscriptionService_ForRestTemplate;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for User operations.
 * BASE URL: http://localhost:8082/users
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final SubscriptionService_ForRestTemplate subscriptionService;

    public UserController(SubscriptionService_ForRestTemplate subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * POST /users/register
     * Register a new user.
     *
     * Postman: POST http://localhost:8082/users/register
     * Body (JSON):
     * {
     *   "username": "skyri",
     *   "email": "skyri@gmail.com",
     *   "fullName": "Skyri Kumar",
     *   "phone": "9876543210"
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody User user) {
        log.info("POST /users/register - {}", user.getUsername());
        User created = subscriptionService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /users/{userId}
     * Get user details by ID.
     *
     * Postman: GET http://localhost:8082/users/1
     */
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        log.info("GET /users/{}", userId);
        return ResponseEntity.ok(subscriptionService.getUserById(userId));
    }
}
