package com.financetracker.notification_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;  // The user to whom the notification belongs

    private String message;  // The content of the notification

    private String notificationType;  // E.g., "BUDGET_EXCEEDED", "TRANSACTION_ALERT"

    private boolean isRead;  // Flag to check if the notification has been read

    private LocalDateTime sentDate;  // The date and time when the notification was sent

}
