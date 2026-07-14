package com.smartlogix.ms_inventory.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para el microservicio de inventario.
 * <p>
 * Intercepta las excepciones de validación lanzadas por los controladores y las
 * transforma en respuestas HTTP con detalle de los errores encontrados.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja los errores de validación de argumentos anotados con {@code @Valid}.
     *
     * @param ex excepción lanzada por Spring cuando la validación de un {@code @RequestBody} falla
     * @return respuesta 400 con un mapa de nombre de campo y mensaje de error asociado
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Maneja las violaciones de restricciones de validación sobre parámetros sueltos
     * (por ejemplo, anotaciones como {@code @Min} o {@code @NotBlank} en parámetros de consulta).
     *
     * @param ex excepción lanzada al violarse una restricción de validación
     * @return respuesta 400 con un mapa que contiene el mensaje de error
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintErrors(
            ConstraintViolationException ex) {

        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());

        return ResponseEntity.badRequest().body(errors);
    }
}