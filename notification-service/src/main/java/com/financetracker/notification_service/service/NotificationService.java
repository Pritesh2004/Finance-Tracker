package com.financetracker.notification_service.service;

import com.financetracker.notification_service.entity.Notification;
import com.financetracker.notification_service.repo.NotificationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepo notificationRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${notification.default.sender}")
    private String defaultSender;

    // Send a notification email
    public void sendEmailNotification(String email, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(defaultSender);
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }

    // Save and send a notification
    public Notification createAndSendNotification(Long userId, String email, String message, String notificationType) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setNotificationType(notificationType);
        notification.setRead(false);
        notification.setEmail(email);
        notification.setSentDate(LocalDateTime.now());

        // Save the notification to the database
        Notification savedNotification = notificationRepository.save(notification);

        // Send the email
        sendEmailNotification(email, "Finance Tracker Notification", message);

        return savedNotification;
    }

    // Retrieve all notifications for a user
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    // Mark a notification as read
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }
}
