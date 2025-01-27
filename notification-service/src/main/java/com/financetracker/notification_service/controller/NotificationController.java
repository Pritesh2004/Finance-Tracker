package com.financetracker.notification_service.controller;

import com.financetracker.notification_service.entity.Notification;
import com.financetracker.notification_service.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Create and send a notification
    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotification(
            @RequestParam Long userId,
            @RequestParam String email,
            @RequestParam String message,
            @RequestParam String notificationType) {
        Notification notification = notificationService.createAndSendNotification(userId, email, message, notificationType);
        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }

    // Get all notifications for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    // Mark a notification as read
    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        Notification notification = notificationService.markAsRead(id);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }
}
