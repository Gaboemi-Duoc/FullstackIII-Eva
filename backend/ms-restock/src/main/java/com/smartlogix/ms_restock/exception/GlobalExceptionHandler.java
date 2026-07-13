package com.smartlogix.ms_restock.exception;

import java.util.HashMap;
import java.util.Map;
import io.sentry.Sentry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintErrors(
            ConstraintViolationException ex) {

        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        // Si el mensaje indica que algo "no fue encontrado" o "no existe", es un 404.
        // El resto de errores de negocio (estado inválido, etc.) son 400.
        String mensaje = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";
        HttpStatus status = (mensaje.contains("no encontrad") || mensaje.contains("no existe"))
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Sentry.captureException(ex);
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "Error interno del servidor: " + ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}