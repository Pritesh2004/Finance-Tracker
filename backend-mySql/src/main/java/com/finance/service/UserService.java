package com.finance.service;

import com.finance.entity.User;
import com.finance.helper.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.finance.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User createUser(User user) {
        // Check if user with the same email or userId already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists.");
        }

        user.setPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));

        // Save the user if no existing user is found
        return userRepository.save(user);
    }

}
