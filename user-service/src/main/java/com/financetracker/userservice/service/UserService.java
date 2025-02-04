package com.financetracker.userservice.service;

import com.financetracker.userservice.client.NotificationClient;
import com.financetracker.userservice.entity.User;
import com.financetracker.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        user.setPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));
        User newUser = userRepository.save(user);

        notificationClient.sendUserCreatedNotification((newUser));

        return newUser;
    }

    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    if (!user.getEmail().equals(updatedUser.getEmail()) &&
                            userRepository.existsByEmail(updatedUser.getEmail())) {
                        throw new RuntimeException("Email already exists: " + updatedUser.getEmail());
                    }
                    user.setFullName(updatedUser.getFullName());
                    user.setEmail(updatedUser.getEmail());
                    user.setPassword(updatedUser.getPassword());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User getUserById(Long id, String loggedInEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Authorization check
        if (!user.getEmail().equals(loggedInEmail)) {
            throw new AccessDeniedException("You are not authorized to access this resource.");
        }

        return user;
    }

    public void deleteUser(Long id, String loggedInEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Authorization check
        if (!user.getEmail().equals(loggedInEmail)) {
            throw new AccessDeniedException("You are not authorized to delete this resource.");
        }

        userRepository.deleteById(id);
    }
}
