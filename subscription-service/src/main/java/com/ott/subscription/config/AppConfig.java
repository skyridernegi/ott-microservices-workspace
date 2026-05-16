package com.ott.subscription.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Application Configuration class.
 *
 * @Configuration means Spring reads this class at startup
 * and registers the beans defined here.
 *
 * RestTemplate is the HTTP client used by CatalogClient
 * to call the Catalog Service REST APIs.
 */
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
