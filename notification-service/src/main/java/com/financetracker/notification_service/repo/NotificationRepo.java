package com.financetracker.notification_service.repo;

import com.financetracker.notification_service.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {
}
