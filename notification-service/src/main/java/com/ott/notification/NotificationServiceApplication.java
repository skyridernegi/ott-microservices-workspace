package com.ott.notification;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Notification Service — Main Entry Point
 * NO @EnableFeignClients needed — this service only CONSUMES events
 * NO database — stateless service
 * Only dependency: Kafka (to receive events) + Mail (to send emails)
 */
@SpringBootApplication
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

}
