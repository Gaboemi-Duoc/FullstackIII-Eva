package com.smartlogix.bff.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartlogix.bff.dto.DtoApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/bff")
public class BffController {
    
    @GetMapping("/health")
    @Operation(
        summary = "Health check endpoint",
        description = """
            Returns the health status of the BFF service.
            
            This endpoint is used by:
            - Kubernetes liveness and readiness probes
            - Docker Compose health checks
            - Load balancer health monitoring
            - Monitoring systems (Prometheus, Grafana)
            
            No authentication required for this endpoint.
            """,
        tags = {"Health Check"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Service is healthy and operational",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = DtoApiResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "503",
            description = "Service is unhealthy - Check dependencies (user-service, inventory-service)",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DtoApiResponse<String>> healthCheck() {
        log.debug("BFF: Health check requested");
        return ResponseEntity.ok(new DtoApiResponse<>(true, "BFF is running", "OK", 200));
    }

    @GetMapping("/log-test")
    public ResponseEntity<DtoApiResponse<String>> testLogging() {
        log.trace("TRACE log message");
        log.debug("DEBUG log message");
        log.info("INFO log message");
        log.warn("WARN log message");
        log.error("ERROR log message");
        
        return ResponseEntity.ok(new DtoApiResponse<>(true, "Log levels tested", "Logs sent to GlitchTip", 200));
    }
}
