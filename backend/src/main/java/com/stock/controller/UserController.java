package com.stock.controller;

import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.stock.config.security.JwtUtil;
import com.stock.dto.JwtRequest;
import com.stock.dto.JwtResponse;
import com.stock.entity.User;
import com.stock.helper.UserNotFoundException;
import com.stock.service.UserDetailsServiceImpl;
import com.stock.service.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService userService;
	
	 @Autowired
	 private AuthenticationManager authenticationManager;

	
	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;
	
	@Autowired
    private JwtUtil jwtUtils;

	@GetMapping("/")
	public String home() {
		return "home";
	}

	@GetMapping("/secure")
	public String secured() {
		return "secured";
	}
	
	@PostMapping("/generate-token")
    public ResponseEntity<?> generateToken(@RequestBody JwtRequest jwtRequest) {
        try {
            UserDetails userDetails = this.userDetailsServiceImpl.loadUserByUsername(jwtRequest.getEmail());
            String token = this.jwtUtils.generateToken(userDetails);
            boolean valid = jwtUtils.validateToken(token, userDetails);
            if(!valid) throw new UserNotFoundException();
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed: " + e.getMessage());
        }
    }

//    private void authenticate(String username, String password) throws Exception {
//        try {
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//        } catch (DisabledException e) {
//            throw new Exception("User disabled: " + e.getMessage());
//        } catch (BadCredentialsException e) {
//            throw new Exception("Invalid credentials: " + e.getMessage());
//        }
//    }

	@PostMapping("/user")
	public String createUser(@RequestBody User user) throws ExecutionException, InterruptedException {
		return userService.saveUser(user);
	}

	@GetMapping("/user/{email}")
	public User getUser(@PathVariable String email) throws ExecutionException, InterruptedException {
		return userService.getUser(email);
	}

//	@PostMapping("/user/{email}/prediction")
//	public String addPrediction(@PathVariable String email, @RequestBody Prediction prediction)
//			throws ExecutionException, InterruptedException {
//		return userService.addPrediction(email, prediction);
//	}
}
