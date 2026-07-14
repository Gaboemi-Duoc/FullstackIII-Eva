package com.smartlogix.ms_user.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la documentación OpenAPI/Swagger para el microservicio de usuarios.
 * <p>
 * Define el título, versión, descripción, contacto, licencia y servidores disponibles
 * que se muestran en la interfaz de Swagger UI.
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "API ms-user - SmartLogix",
        version = "1.0.0",
        description = """
            # Microservicio de Usuarios

            Gestiona el registro, autenticación y administración de perfiles
            de usuario dentro de la plataforma logística **SmartLogix**.

            ### Funcionalidades Principales
            - Registro de nuevos usuarios
            - Login de usuarios
            - Consulta de usuario por id
            - Actualización de username
            - Eliminación de usuarios

            ### Códigos de Respuesta Comunes
            - `200`: Operación exitosa
            - `204`: Recurso eliminado correctamente
            - `400`: Error de validación en los datos enviados
            - `401`: Credenciales inválidas
            - `404`: Recurso no encontrado
            - `409`: Conflicto - Recurso ya existe
            - `500`: Error interno del servidor
            """,
        contact = @Contact(
            name = "Equipo SmartLogix",
            email = "soporte@smartlogix.com",
            url = "https://www.smartlogix.com"
        ),
        license = @License(
            name = "Licencia Propietaria",
            url = "https://www.smartlogix.com/licencia"
        )
    ),
    servers = {
        @Server(url = "http://localhost:9090", description = "Servidor de Desarrollo"),
        @Server(url = "http://ms-user:9090", description = "Docker Compose / Kubernetes Interno")
    }
)
public class OpenAPIConfig {
}
