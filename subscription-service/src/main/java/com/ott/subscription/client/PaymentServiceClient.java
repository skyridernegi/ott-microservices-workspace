package com.ott.subscription.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * PURPOSE: Feign Client in subscription-service to call payment-service.
 * subscription-service calls this during the subscribe flow.
 * Payment result determines if subscription is activated.
 */
@FeignClient(name = "payment-service", url = "${payment.service.url}")
public interface PaymentServiceClient {

    @PostMapping("/payments/process")
    Map<String, Object> processPayment(@RequestBody Map<String, Object> request);
}