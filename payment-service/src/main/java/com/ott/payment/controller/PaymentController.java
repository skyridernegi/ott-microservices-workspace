package com.ott.payment.controller;
import com.ott.payment.dto.*;
import com.ott.payment.service.PaymentService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for payment-service.
 * Called by subscription-service via Feign (internal, not from ESPN UI directly).
 *
 * Postman test: POST http://localhost:8080/payments/process (with Bearer token)
 */
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    public PaymentController(PaymentService s) { paymentService = s; }

    /**
     * POST /payments/process
     * Called by subscription-service Feign Client.
     * Body: {"userId":1,"planId":"PLAN001","amount":199.0,"cardNumber":"4242424242424242"}
     * Test card SUCCESS: 4242 4242 4242 4242
     * Test card FAIL:    4000 0000 0000 0002
     */
    @PostMapping("/process")
    public ResponseEntity<PaymentResponseDTO> process(@RequestBody PaymentRequestDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.processPayment(req));
    }

    /**
     * GET /payments/user/{userId}
     * Returns all payment history for a user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserPayments(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getUserPayments(userId));
    }
}