package com.smartlogix.ms_inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Igual que UserServiceApplication — punto de arranque de Spring Boot

/**
 * Clase principal del microservicio de inventario.
 * <p>
 * Punto de arranque de la aplicación Spring Boot para {@code ms-inventory}.
 */
@SpringBootApplication
public class InventoryServiceApplication {

    /**
     * Método de entrada de la aplicación.
     *
     * @param args argumentos de línea de comandos recibidos al iniciar la aplicación
     */
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
