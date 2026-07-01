package com.smartlogix.bff.exception;

import com.smartlogix.bff.dto.DtoApiResponse;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DtoApiResponse<Void>> handleGenericException(Exception e) {

        log.error("Unexpected error occurred", e);

        // Envía la excepción a GlitchTip
        Sentry.captureException(e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new DtoApiResponse<>(false, "Internal server error", null, 500));
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<DtoApiResponse<Void>> handleRestClientException(RestClientException e, WebRequest request) {
        log.error("Error calling downstream service: {}", e.getMessage(), e);
        
        // Capture as warning, not critical depending on context
        if (e instanceof HttpClientErrorException.NotFound) {
            Sentry.captureMessage("Resource not found in downstream service: " + e.getMessage(), SentryLevel.WARNING);
        } else {
            Sentry.captureException(e);
        }
        
        HttpStatus status = determineHttpStatus(e);
        return ResponseEntity
            .status(status)
            .body(new DtoApiResponse<>(false, "Service temporarily unavailable", null, status.value()));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<DtoApiResponse<Void>> handleHttpClientError(HttpClientErrorException e) {
        log.warn("Client error from downstream service - Status: {}, Response: {}", 
            e.getStatusCode(), e.getResponseBodyAsString());
        
        HttpStatus status = (HttpStatus) e.getStatusCode();
        
        if (status == HttpStatus.NOT_FOUND) {
            Sentry.captureMessage("Resource not found: " + e.getMessage(), SentryLevel.WARNING);
        } else if (status == HttpStatus.BAD_REQUEST) {
            Sentry.captureMessage("Bad request to downstream service: " + e.getMessage(), SentryLevel.INFO);
        } else {
            Sentry.captureException(e);
        }
        
        return ResponseEntity
            .status(status)
            .body(new DtoApiResponse<>(false, "Service error", null, status.value()));
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<DtoApiResponse<Void>> handleHttpServerError(HttpServerErrorException e) {
        log.error("Server error from downstream service - Status: {}, Response: {}", 
            e.getStatusCode(), e.getResponseBodyAsString());
        
        Sentry.captureException(e);
        
        HttpStatus status = (HttpStatus) e.getStatusCode();
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
        
        log.warn("Validation error: {}", errors);
        Sentry.captureMessage("Validation error: " + errors, SentryLevel.INFO);
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new DtoApiResponse<>(false, "Validation failed", errors, 400));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<DtoApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Illegal argument: {}", e.getMessage());
        Sentry.captureMessage("Illegal argument: " + e.getMessage(), SentryLevel.WARNING);
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new DtoApiResponse<>(false, e.getMessage(), null, 400));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<DtoApiResponse<Void>> handleIllegalStateException(IllegalStateException e) {
        log.error("Illegal state: {}", e.getMessage(), e);
        Sentry.captureException(e);
        
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new DtoApiResponse<>(false, e.getMessage(), null, 409));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<DtoApiResponse<Void>> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("Type mismatch for parameter {}: {}", e.getName(), e.getMessage());
        Sentry.captureMessage("Type mismatch: " + e.getMessage(), SentryLevel.WARNING);
        
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