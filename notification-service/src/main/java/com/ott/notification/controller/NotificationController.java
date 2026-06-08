package com.ott.notification.controller;
import com.ott.notification.event.NotificationEvent;
import com.ott.notification.service.EmailNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * PURPOSE: Fallback REST endpoint for testing notifications WITHOUT Kafka.
 * In production: remove this and use only Kafka consumers.
 * For testing: POST /notifications/send with event body.
 *
 * Also used by subscription-service as fallback when Kafka is not available.
 *
 * Postman: POST http://localhost:8080/notifications/send (with Bearer token)
 * Body: {"eventType":"PAYMENT_SUCCESS","userId":1,"username":"john.doe",
 *        "email":"john@gmail.com","planName":"Disney+ Basic","amount":199.0}
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final EmailNotificationService emailService;
    public NotificationController(EmailNotificationService e) { emailService = e; }

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationEvent event) {
        switch (event.getEventType()) {
            case "PAYMENT_SUCCESS"    -> emailService.sendPaymentSuccessEmail(event);
            case "PAYMENT_FAILED"     -> emailService.sendPaymentFailedEmail(event);
            case "SUBSCRIPTION_EXPIRING" -> emailService.sendSubscriptionExpiringEmail(event);
            case "SUBSCRIPTION_EXPIRED"  -> emailService.sendSubscriptionExpiredEmail(event);
            default -> throw new RuntimeException("Unknown event type: " + event.getEventType());
        }
        return ResponseEntity.ok("Notification sent for event: " + event.getEventType());
    }

    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("notification-service is running");
    }
}