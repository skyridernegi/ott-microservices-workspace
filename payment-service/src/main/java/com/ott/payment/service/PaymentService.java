package com.ott.payment.service;
import com.ott.payment.dto.*;
import com.ott.payment.model.Payment;
import com.ott.payment.repository.PaymentRepository;
import org.slf4j.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * PURPOSE: Core payment processing logic.
 *
 * SIMULATION MODE (no real payment gateway):
 * Card 4242 4242 4242 4242 → always SUCCESS
 * Card 4000 0000 0000 0002 → always FAILED
 * Any other card → SUCCESS (80% chance in real impl)
 *
 * In PRODUCTION: Replace processPaymentGateway() with
 * Stripe/Razorpay API call.
 *
 * KAFKA: When Kafka is added, publishPaymentEvent() will
 * publish to payment.success or payment.failed topics.
 * Notification-service and subscription-service consume these.
 */
@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository r) { paymentRepository = r; }

    /**
     * Process payment for subscription.
     * Called by subscription-service via Feign.
     */
    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO req) {
        log.info("Processing payment — userId:{} planId:{} amount:{}",
            req.getUserId(), req.getPlanId(), req.getAmount());

        // Save payment as PENDING first
        Payment payment = new Payment();
        payment.setUserId(req.getUserId());
        payment.setPlanId(req.getPlanId());
        payment.setAmount(req.getAmount());
        payment.setStatus(Payment.PaymentStatus.PENDING);
        // Store only last 4 digits — never full card number
        if (req.getCardNumber() != null && req.getCardNumber().length() >= 4) {
            String clean = req.getCardNumber().replaceAll("\\s+", "");
            payment.setCardLastFour(clean.substring(clean.length() - 4));
        }
        paymentRepository.save(payment);

        // Simulate payment gateway call
        boolean success = simulatePaymentGateway(req.getCardNumber());

        if (success) {
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0,8).toUpperCase());
            log.info("Payment SUCCESS for user:{}", req.getUserId());
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason("Card declined — insufficient funds");
            log.warn("Payment FAILED for user:{}", req.getUserId());
        }

        Payment saved = paymentRepository.save(payment);

        // TODO: When Kafka is configured, publish event here:
        // kafkaTemplate.send("payment." + (success?"success":"failed"), event);

        return buildResponse(saved);
    }

    public List<Payment> getUserPayments(Long userId) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Simulation: card ending 4242 = success, 0002 = fail
    private boolean simulatePaymentGateway(String cardNumber) {
        if (cardNumber == null) return true;
        String clean = cardNumber.replaceAll("\\s+", "");
        if (clean.endsWith("0002")) return false; // test fail card
        return true; // all other cards succeed
    }

    private PaymentResponseDTO buildResponse(Payment p) {
        PaymentResponseDTO r = new PaymentResponseDTO();
        r.setPaymentId(p.getPaymentId());
        r.setStatus(p.getStatus().name());
        r.setTransactionId(p.getTransactionId());
        r.setFailureReason(p.getFailureReason());
        r.setAmount(p.getAmount());
        r.setProcessedAt(p.getUpdatedAt());
        return r;
    }
}