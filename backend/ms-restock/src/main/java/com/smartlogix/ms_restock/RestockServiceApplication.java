package com.smartlogix.ms_restock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del microservicio de reabastecimiento.
 * <p>
 * Punto de arranque de la aplicación Spring Boot para {@code ms-restock}.
 */
@SpringBootApplication

public class RestockServiceApplication {

    /**
     * Método de entrada de la aplicación.
     *
     * @param args argumentos de línea de comandos recibidos al iniciar la aplicación
     */
    public static void main(String[] args) {
        SpringApplication.run(RestockServiceApplication.class, args);
    }
}