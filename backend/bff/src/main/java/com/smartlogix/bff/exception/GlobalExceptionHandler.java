package com.smartlogix.bff.exception;

import com.smartlogix.bff.dto.DtoApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DtoApiResponse<Void>> handleGenericException(Exception e, WebRequest request) {
        // This log will appear in BOTH Logs AND Issues sections
        // Logs section: as a log entry
        // Issues section: as an error event with stack trace
        log.error("Unexpected error occurred for request: {}", request.getDescription(false), e);
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new DtoApiResponse<>(false, "Internal server error", null, 500));
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<DtoApiResponse<Void>> handleRestClientException(RestClientException e, WebRequest request) {
        if (e instanceof HttpClientErrorException.NotFound) {
            // This will appear ONLY in Logs section (WARN level, no exception)
            log.warn("Resource not found in downstream service: {}", e.getMessage());
        } else if (e instanceof HttpServerErrorException) {
            // This will appear in BOTH Logs AND Issues (ERROR with exception)
            log.error("Downstream service error: {}", e.getMessage(), e);
        } else {
            // This will appear in BOTH Logs AND Issues (ERROR with exception)
            log.error("Rest client error: {}", e.getMessage(), e);
        }
        
        HttpStatus status = determineHttpStatus(e);
        return ResponseEntity
            .status(status)
            .body(new DtoApiResponse<>(false, "Service temporarily unavailable", null, status.value()));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<DtoApiResponse<Void>> handleHttpClientError(HttpClientErrorException e) {
        HttpStatus status = (HttpStatus) e.getStatusCode();
        
        if (status == HttpStatus.NOT_FOUND) {
            // Logs section only
            log.warn("Resource not found: {}", e.getMessage());
        } else if (status == HttpStatus.BAD_REQUEST) {
            // Logs section only
            log.info("Bad request to downstream service: {}", e.getMessage());
        } else {
            // BOTH Logs AND Issues
            log.error("Unexpected client error: {}", e.getMessage(), e);
        }
        
        return ResponseEntity
            .status(status)
            .body(new DtoApiResponse<>(false, "Service error", null, status.value()));
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<DtoApiResponse<Void>> handleHttpServerError(HttpServerErrorException e) {
        // BOTH Logs AND Issues
        log.error("Server error from downstream service - Status: {}, Response: {}", 
            e.getStatusCode(), e.getResponseBodyAsString(), e);
        
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(new DtoApiResponse<>(false, "Downstream service unavailable", null, 503));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DtoApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException e) {
        
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        // Logs section only (WARN level)
        log.warn("Validation error: {}", errors);
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new DtoApiResponse<>(false, "Validation failed", errors, 400));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<DtoApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        // Logs section only (WARN level)
        log.warn("Illegal argument: {}", e.getMessage());
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new DtoApiResponse<>(false, e.getMessage(), null, 400));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<DtoApiResponse<Void>> handleIllegalStateException(IllegalStateException e) {
        // BOTH Logs AND Issues
        log.error("Illegal state: {}", e.getMessage(), e);
        
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new DtoApiResponse<>(false, e.getMessage(), null, 409));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<DtoApiResponse<Void>> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        // Logs section only (WARN level)
        log.warn("Type mismatch for parameter {}: {}", e.getName(), e.getMessage());
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new DtoApiResponse<>(false, "Invalid parameter type: " + e.getName(), null, 400));
    }

    private HttpStatus determineHttpStatus(Exception e) {
        if (e instanceof HttpClientErrorException) {
            return (HttpStatus) ((HttpClientErrorException) e).getStatusCode();
        } else if (e instanceof HttpServerErrorException) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        } else if (e instanceof java.net.ConnectException) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        } else if (e instanceof java.net.SocketTimeoutException) {
            return HttpStatus.GATEWAY_TIMEOUT;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}