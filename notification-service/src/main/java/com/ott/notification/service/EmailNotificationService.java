package com.ott.notification.service;
import com.ott.notification.event.NotificationEvent;
import org.slf4j.*;
import org.springframework.stereotype.Service;
// Uncomment when spring.mail.* is configured:
// import org.springframework.mail.SimpleMailMessage;
// import org.springframework.mail.javamail.JavaMailSender;

/**
 * PURPOSE: Sends emails/SMS based on notification events.
 *
 * CURRENT STATE: Simulates email sending (logs to console).
 * TO ENABLE REAL EMAILS:
 *   1. Uncomment JavaMailSender injection
 *   2. Add Gmail App Password to application.properties
 *   3. Replace log.info() calls with mailSender.send()
 *
 * PRODUCTION: Use AWS SES (Simple Email Service) instead of Gmail
 * — more reliable, better delivery rates, used by Disney/Netflix.
 */
@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);
    // @Autowired private JavaMailSender mailSender; // Uncomment for real emails

    public void sendPaymentSuccessEmail(NotificationEvent event) {
        String subject = "Welcome to " + event.getPlanName() + "! Your subscription is active";
        String body = String.format(
            "Hi %s,\n\nYour payment of ₹%.2f for %s was successful!\n" +
            "Your subscription is now ACTIVE.\n\nEnjoy streaming!\nESPN+ Team",
            event.getUsername(), event.getAmount(), event.getPlanName()
        );
        sendEmail(event.getEmail(), subject, body);
    }

    public void sendPaymentFailedEmail(NotificationEvent event) {
        String subject = "Payment Failed — Action Required";
        String body = String.format(
            "Hi %s,\n\nYour payment for %s failed.\nReason: %s\n\n" +
            "Please update your payment method and try again.\nESPN+ Team",
            event.getUsername(), event.getPlanName(), event.getFailureReason()
        );
        sendEmail(event.getEmail(), subject, body);
    }

    public void sendSubscriptionExpiringEmail(NotificationEvent event) {
        String subject = "Your ESPN+ subscription expires in 3 days";
        String body = String.format(
            "Hi %s,\n\nYour %s subscription is expiring soon.\n" +
            "Renew now to continue uninterrupted streaming!\nESPN+ Team",
            event.getUsername(), event.getPlanName()
        );
        sendEmail(event.getEmail(), subject, body);
    }

    public void sendSubscriptionExpiredEmail(NotificationEvent event) {
        String subject = "Your ESPN+ subscription has expired";
        String body = String.format(
            "Hi %s,\n\nYour %s subscription has expired.\n" +
            "Subscribe again to continue watching your favourite sports.\nESPN+ Team",
            event.getUsername(), event.getPlanName()
        );
        sendEmail(event.getEmail(), subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        // SIMULATION — logs to console instead of sending real email
        log.info("========== EMAIL NOTIFICATION ==========");
        log.info("TO:      {}", to);
        log.info("SUBJECT: {}", subject);
        log.info("BODY:\n{}", body);
        log.info("========================================");

        /* Uncomment for real email:
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("noreply@espn.com");
        mailSender.send(message);
        */
    }
}