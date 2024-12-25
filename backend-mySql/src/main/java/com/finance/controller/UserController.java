package com.finance.controller;

import com.finance.config.security.JwtUtil;
import com.finance.dto.JwtRequest;
import com.finance.dto.JwtResponse;
import com.finance.entity.User;
import com.finance.helper.UserNotFoundException;
import com.finance.repository.UserRepository;
import com.finance.service.UserDetailsService;
import com.finance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsServiceImpl;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtils;

    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // Generate token
    @PostMapping("/generate-token")
    public ResponseEntity<?> generateToken(@RequestBody JwtRequest jwtRequest) {
        try {
            authenticate(jwtRequest.getEmail(), jwtRequest.getPassword());
//            UserDetails userDetails = this.userDetailsServiceImpl.loadUserByUsername(jwtRequest.getEmail());
            String token = this.jwtUtils.generateToken(jwtRequest.getEmail());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed: " + e.getMessage());
        }
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("User disabled: " + e.getMessage());
        } catch (BadCredentialsException e) {
            throw new Exception("Invalid credentials: " + e.getMessage());
        }
    }

    // GET mapping to retrieve user by email
    @GetMapping("/user/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        System.out.println(email);
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(user);  // Return the user if found
        } else {
            return ResponseEntity.notFound().build();  // Return 404 if user not found
        }
    }

    // Return the details of the current user
    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        try {
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(principal.getName());
            if (userDetails instanceof User) {
                return ResponseEntity.ok((User) userDetails);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User details not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch user details: " + e.getMessage());
        }
    }
}
