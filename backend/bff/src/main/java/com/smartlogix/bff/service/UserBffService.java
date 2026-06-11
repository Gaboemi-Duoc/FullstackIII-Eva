package com.smartlogix.bff.service;

import com.smartlogix.bff.dto.LoginRequest;
import com.smartlogix.bff.dto.LoginResponse;
import com.smartlogix.bff.dto.DtoApiResponse;
import com.smartlogix.bff.dto.UpdateUsernameRequest;
import com.smartlogix.bff.client.UserServiceClient;
import com.smartlogix.bff.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.smartlogix.bff.security.JwtService;

import java.util.UUID;

@Slf4j
@Service
public class UserBffService {
    
    private final UserServiceClient userServiceClient;
    private final JwtService jwtService;
    
    public UserBffService(UserServiceClient userServiceClient,
                        JwtService jwtService) {

        this.userServiceClient = userServiceClient;
        this.jwtService = jwtService;
    }
    
    public DtoApiResponse<LoginResponse> login(LoginRequest loginRequest) {
        try {
            log.info("Processing login for user: {}", loginRequest.getUsername());
            
            // Call the actual user service
            User user = userServiceClient.login(loginRequest);
            
            // Generate a token (in production, use JWT)
            //String token = generateToken(user.getId_user());
            String token = jwtService.generateToken(user.getUsername());

            // Create response with additional BFF-specific data
            LoginResponse response = new LoginResponse(
                user.getId_user(),
                user.getUsername(),
                user.getEmail(),
                token,
                "Login successful"
            );
            
            return new DtoApiResponse<>(true, "Login successful", response, 200);
            
        } catch (Exception e) {
            log.error("Login failed for user: {}", loginRequest.getUsername(), e);
            return new DtoApiResponse<>(false, "Invalid credentials", null, 401);
        }
    }
    
    public DtoApiResponse<User> updateUsername(Long userId, UpdateUsernameRequest request) {
        try {
            log.info("Updating username for user ID: {}", userId);
            
            User updatedUser = userServiceClient.updateUsername(userId, request);
            
            return new DtoApiResponse<>(true, "Username updated successfully", updatedUser, 200);
            
        } catch (Exception e) {
            log.error("Failed to update username for user ID: {}", userId, e);
            return new DtoApiResponse<>(false, "Failed to update username", null, 500);
        }
    }
    
    public DtoApiResponse<User> getUserDetails(Long userId) {
        try {
            User user = userServiceClient.getUserById(userId);
            // Remove sensitive data
            user.setPassword(null);
            return new DtoApiResponse<>(true, "User found", user, 200);
        } catch (Exception e) {
            log.error("User not found with ID: {}", userId, e);
            return new DtoApiResponse<>(false, "User not found", null, 404);
        }
    }
    
    private String generateToken(Long userId) {
        // In production, implement proper JWT generation
        return UUID.randomUUID().toString();
    }
}