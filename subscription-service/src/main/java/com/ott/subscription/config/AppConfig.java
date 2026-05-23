package com.ott.subscription.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import com.ott.subscription.client.FeignErrorDecoder;
import feign.Logger;
import feign.codec.ErrorDecoder;
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
	/* As we are implementing FeignClient, we don't need RestTemplate
	 * @Bean public RestTemplate restTemplate() { return new RestTemplate(); }
	 * 
	 */    
	 /**
     * FULL logging = Feign logs:
     * - Request URL, method, headers, body
     * - Response status, headers, body
     * You will see these in STS console when subscribe is called.
     * Change to Logger.Level.NONE in production.
     */
	@Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
	 /**
     * Registers our custom error decoder.
     * Feign picks this up automatically and uses it
     * whenever catalog-service returns 4xx or 5xx.
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
    
    
    @Bean //this bean is just mentioned here to overcome the error of bean not found in CatalogClient_RestTemplate construction injection;
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
