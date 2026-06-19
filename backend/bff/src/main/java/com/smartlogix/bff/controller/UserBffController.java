package com.smartlogix.bff.controller;

import com.smartlogix.bff.dto.LoginRequest;
import com.smartlogix.bff.dto.LoginResponse;
import com.smartlogix.bff.dto.DtoApiResponse;
import com.smartlogix.bff.dto.UpdateUsernameRequest;
import com.smartlogix.bff.model.User;
import com.smartlogix.bff.service.UserBffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/bff/users")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "User Management", description = "Endpoints for user authentication, profile management, and user data retrieval for SmartLogix logistics platform")
public class UserBffController {
    
    private final UserBffService userBffService;

    @Value("${user-service.url:http://localhost:9090}")
    private String userServiceUrl;
    
    public UserBffController(UserBffService userBffService) {
        this.userBffService = userBffService;
    }
    
    @PostMapping("/login")
    @Operation(
        summary = "Authenticate user",
        description = """
            Authenticates a user with username and password credentials.
            
            Upon successful authentication, returns a JWT token that must be included in the Authorization header 
            for all subsequent authenticated requests.
            
            The token follows the format: `Bearer <jwt-token>`
            
            This endpoint is publicly accessible and does not require authentication.
            """,
        tags = {"User Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful - Authentication token generated and returned",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = LoginResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials - Username or password is incorrect",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = DtoApiResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validation error - Request body validation failed (e.g., missing username/password)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error - Unable to process login request",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DtoApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        log.info("BFF: Login request received for username: {}", loginRequest.getUsername());
        log.debug("BFF: Using user-service at: {}", userServiceUrl);
        
        DtoApiResponse<LoginResponse> response = userBffService.login(loginRequest);
        
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    
    @PutMapping("/users/{id}/username")
    @Operation(
        summary = "Update username",
        description = """
            Updates the username for a specific user.
            
            This operation requires authentication. The new username must be unique across the system.
            
            Business rules:
            - Username must be between 3 and 50 characters
            - Username can only contain alphanumeric characters, dots, and underscores
            - Username cannot be changed more than once every 30 days (security policy)
            """,
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Username updated successfully",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = User.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found - No user exists with the provided ID",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = DtoApiResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Username already exists - The requested username is taken",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = DtoApiResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validation error - Username format invalid",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DtoApiResponse<User>> updateUsername(
            @Parameter(description = "Unique identifier of the user", required = true, example = "12345")
            @PathVariable Long id,
            @Valid @RequestBody UpdateUsernameRequest request) {
        
        log.info("BFF: Update username request for user ID: {}", id);
        
        DtoApiResponse<User> response = userBffService.updateUsername(id, request);
        
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    
    @GetMapping("/users/{id}")
    @Operation(
        summary = "Get user details",
        description = """
            Retrieves detailed information for a specific user.
            
            Returns user profile including:
            - User ID
            - Username
            - Email address
            - Registration date
            - Account status (active/suspended)
            - Role and permissions
            
            Requires authentication. Users can only access their own profile unless they have ADMIN role.
            """,
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User details retrieved successfully",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = User.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found - No user exists with the provided ID",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = DtoApiResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Insufficient permissions to access this user's data",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DtoApiResponse<User>> getUser(
            @Parameter(description = "Unique identifier of the user to retrieve", required = true, example = "12345")
            @PathVariable Long id) {
        log.info("BFF: Get user details for ID: {}", id);
        
        DtoApiResponse<User> response = userBffService.getUserDetails(id);
        
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}