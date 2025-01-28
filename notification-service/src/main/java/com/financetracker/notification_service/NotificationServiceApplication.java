package com.financetracker.notification_service;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

	@PostConstruct
	public void checkEnvVariables() {
		System.out.println("EMAIL_USERNAME: " + System.getenv("EMAIL_USERNAME"));
		System.out.println("EMAIL_PASSWORD: " + System.getenv("EMAIL_PASSWORD"));
	}

}
