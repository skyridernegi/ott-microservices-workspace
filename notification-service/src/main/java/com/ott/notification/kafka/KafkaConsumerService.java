package com.ott.notification.kafka;
import com.ott.notification.event.NotificationEvent;
import com.ott.notification.service.EmailNotificationService;
import org.slf4j.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * PURPOSE: Listens to Kafka topics and triggers email notifications.
 *
 * WHY @KafkaListener:
 * Each method with @KafkaListener is automatically called by Spring
 * whenever a message arrives on the specified topic.
 * No manual polling needed — Spring handles it.
 *
 * groupId = "notification-group":
 * All notification-service instances share one group.
 * Kafka ensures only ONE instance processes each message —
 * prevents duplicate emails if you scale to 2 instances.
 *
 * NOTE: These listeners are INACTIVE until:
 *   1. Kafka is running (localhost:9092)
 *   2. spring.kafka.bootstrap-servers is uncommented in application.properties
 *   3. Topics are created (auto-created on first publish if configured)
 *
 * For NOW without Kafka: use NotificationController POST endpoint to test.
 */
@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final EmailNotificationService emailService;

    public KafkaConsumerService(EmailNotificationService e) { emailService = e; }

    /** Triggered when payment.success is published by payment-service */
    @KafkaListener(topics = "payment.success", groupId = "notification-group")
    public void handlePaymentSuccess(NotificationEvent event) {
        log.info("Kafka: payment.success received for user: {}", event.getUserId());
        emailService.sendPaymentSuccessEmail(event);
    }

    /** Triggered when payment.failed is published by payment-service */
    @KafkaListener(topics = "payment.failed", groupId = "notification-group")
    public void handlePaymentFailed(NotificationEvent event) {
        log.info("Kafka: payment.failed received for user: {}", event.getUserId());
        emailService.sendPaymentFailedEmail(event);
    }

    /** Triggered when subscription.expiring is published by subscription-service scheduler */
    @KafkaListener(topics = "subscription.expiring", groupId = "notification-group")
    public void handleSubscriptionExpiring(NotificationEvent event) {
        log.info("Kafka: subscription.expiring for user: {}", event.getUserId());
        emailService.sendSubscriptionExpiringEmail(event);
    }

    /** Triggered when subscription.expired is published by subscription-service scheduler */
    @KafkaListener(topics = "subscription.expired", groupId = "notification-group")
    public void handleSubscriptionExpired(NotificationEvent event) {
        log.info("Kafka: subscription.expired for user: {}", event.getUserId());
        emailService.sendSubscriptionExpiredEmail(event);
    }
}