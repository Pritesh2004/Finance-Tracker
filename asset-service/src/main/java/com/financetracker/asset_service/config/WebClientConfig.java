package com.financetracker.asset_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    @LoadBalanced // Enables Eureka service resolution
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
