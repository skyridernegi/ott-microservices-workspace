package com.ott.payment.service;
import com.ott.payment.client.UserServiceClient;
import com.ott.payment.dto.*;
import com.ott.payment.event.NotificationEvent;
import com.ott.payment.exception.UserNotFoundException;
import com.ott.payment.kafka.PaymentEventPublisher;
import com.ott.payment.model.Payment;
import com.ott.payment.repository.PaymentRepository;
import org.slf4j.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.ott.payment.event.NotificationEvent;
import java.time.LocalDateTime;

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
    private final PaymentEventPublisher eventPublisher; // ADD 
    private final UserServiceClient userServiceClient; // Feign client // Constructor injection — Spring injects Feign proxy automatically
 // UPDATE constructor:
    public PaymentService(PaymentRepository r, PaymentEventPublisher p ,UserServiceClient userServiceClient) {		
        paymentRepository = r;
        eventPublisher = p; // ADD THIS
        this.userServiceClient=userServiceClient;
    }

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

     // ADD AFTER the save — build and publish Kafka event:
        NotificationEvent kafkaEvent = buildKafkaEvent(saved, req);
        if (Payment.PaymentStatus.SUCCESS.equals(saved.getStatus())) {
            eventPublisher.publishPaymentSuccess(kafkaEvent);
            log.info("Kafka: payment.success published for user: {}", saved.getUserId());
        } else {
            eventPublisher.publishPaymentFailed(kafkaEvent);
            log.info("Kafka: payment.failed published for user: {}", saved.getUserId());
        }
        // TODO: When Kafka is configured, publish event here:
        // kafkaTemplate.send("payment." + (success?"success":"failed"), event);

        return buildResponse(saved);//already there
    }
    private NotificationEvent buildKafkaEvent(Payment payment, PaymentRequestDTO req) {
        NotificationEvent event = new NotificationEvent();
        event.setUserId(payment.getUserId());
        event.setPlanId(payment.getPlanId());
        event.setAmount(payment.getAmount());
        event.setFailureReason(payment.getFailureReason());
        event.setEventTime(LocalDateTime.now());
        // username and email come from request if available
       
        
/*
  //this is the normal call to feign client without excaption handling       
  // In production: call user-service Feign to get email
        UserDto user =userServiceClient.anyMethodName(payment.getUserId());
        System.out.println("OMSAIRAM MY FIRST FEIGN CALL"+user+" EMAIL:"+ user.getEmail());
        
        event.setUsername("user-" + payment.getUserId());
//        event.setEmail("user" + payment.getUserId() + "@espn.com"); // placeholder
        event.setEmail(user.getEmail()); // actual email from user-service
        return event;
 //EOF-this is the normal call to feign client without excaption handling      
  */
        
     // ── ENTERPRISE FEIGN INTEGRATION FLOW ────────────────────────────────
        try {
            log.info("[SAGA] Attempting to fetch email profile via Feign for userId: {}", payment.getUserId());
            
            // Call the Feign client method
            UserDto user = userServiceClient.anyMethodNameForRND(payment.getUserId());
            System.out.println("OMSAIRAM MY FIRST FEIGN CALL"+user+" EMAIL:"+ user.getEmail());
            log.info("✅ Successfully retrieved user metadata for account: {}", user.getUsername());
            log.warn("⚠️ User metadata: {}", user);
            // Map the fresh remote data straight to your Kafka outbound event
            event.setUsername(user.getUsername());
            event.setEmail(user.getEmail()); // Using getEmail() to match your UserDto getter!
            
        } catch (UserNotFoundException ex) {
            // Log with ERROR level since a missing user breaks the transaction integrity
            log.error("❌ Transaction Blocked: User ID {} does not exist in the system core. Message: {}", 
                payment.getUserId(), ex.getMessage());
            
            // Re-throw so your GlobalExceptionHandler handles the API REST mapping back to the UI
            throw ex;

        } catch (Exception ex) {
            // Catching network timeouts or service-down scenarios gracefully
            log.warn("⚠️ User-service is unavailable. Falling back to temporary placeholder properties. Error: {}", 
                ex.getMessage());
            
            event.setUsername("customer-" + payment.getUserId());
            event.setEmail("customer.fallback@ottplatform.com");
        }

        return event;
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