package com.smartlogix.ms_user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del microservicio de usuarios.
 * <p>
 * Punto de arranque de la aplicación Spring Boot para {@code ms-user}.
 */
@SpringBootApplication
public class UserServiceApplication {

	/**
	 * Método de entrada de la aplicación.
	 *
	 * @param args argumentos de línea de comandos recibidos al iniciar la aplicación
	 */
	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
