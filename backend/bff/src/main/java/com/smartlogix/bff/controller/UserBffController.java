package com.smartlogix.bff.controller;

import com.smartlogix.bff.dto.LoginRequest;
import com.smartlogix.bff.dto.LoginResponse;
import com.smartlogix.bff.dto.ApiResponse;
import com.smartlogix.bff.dto.UpdateUsernameRequest;
import com.smartlogix.bff.model.User;
import com.smartlogix.bff.service.UserBffService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/bff/users")
@CrossOrigin(origins = "http://localhost:5173") // Frontend
public class UserBffController {
    
    private final UserBffService userBffService;
    
    public UserBffController(UserBffService userBffService) {
        this.userBffService = userBffService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("BFF: Login request received for username: {}", loginRequest.getUsername());
        
        ApiResponse<LoginResponse> response = userBffService.login(loginRequest);
        
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    
    @PutMapping("/users/{id}/username")
    public ResponseEntity<ApiResponse<User>> updateUsername(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUsernameRequest request) {
        
        log.info("BFF: Update username request for user ID: {}", id);
        
        ApiResponse<User> response = userBffService.updateUsername(id, request);
        
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long id) {
        log.info("BFF: Get user details for ID: {}", id);
        
        ApiResponse<User> response = userBffService.getUserDetails(id);
        
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(new ApiResponse<>(true, "BFF is running", "OK", 200));
    }
}