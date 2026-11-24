package com.ott.payment.kafka;

import com.ott.payment.event.NotificationEvent;
import org.slf4j.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

/**
 * PURPOSE: Publishes payment events to Kafka topics.
 *
 * Called by: PaymentService AFTER payment is processed.
 * Publishes to: "payment.success" or "payment.failed"
 * Consumed by: notification-service (sends email)
 *              subscription-service (activates or keeps pending)
 *
 * KEY: userId is used as the Kafka message KEY.
 * WHY: Messages with same key always go to same partition.
 * This guarantees order — all events for user 5 are processed in order.
 */
@Component
public class PaymentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventPublisher.class);

    private static final String TOPIC_SUCCESS = "payment.success";
    private static final String TOPIC_FAILED  = "payment.failed";

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public PaymentEventPublisher(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publish payment SUCCESS event.
     * Called after payment-service confirms payment went through.
     * notification-service will send "Welcome to Disney+!" email.
     */
    public void publishPaymentSuccess(NotificationEvent event) {
        event.setEventType("PAYMENT_SUCCESS");
        sendMessage(TOPIC_SUCCESS, event);
    }

    /**
     * Publish payment FAILED event.
     * Called after payment-service detects card declined / error.
     * notification-service will send "Payment failed, please retry" email.
     */
    public void publishPaymentFailed(NotificationEvent event) {
        event.setEventType("PAYMENT_FAILED");
        sendMessage(TOPIC_FAILED, event);
    }

    private void sendMessage(String topic, NotificationEvent event) {
        // Key = userId as String — ensures ordering per user
        String key = event.getUserId() != null ? event.getUserId().toString() : "unknown";

        CompletableFuture<SendResult<String, NotificationEvent>> future =
            kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("✅ Published to topic: {} | userId: {} | offset: {}",
                    topic, key, result.getRecordMetadata().offset());
            } else {
                log.error("❌ Failed to publish to topic: {} | userId: {} | error: {}",
                    topic, key, ex.getMessage());
            }
        });
    }
}
