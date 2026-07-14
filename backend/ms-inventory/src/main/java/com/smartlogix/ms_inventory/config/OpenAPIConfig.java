package com.smartlogix.ms_inventory.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la documentación OpenAPI/Swagger para el microservicio de inventario.
 * <p>
 * Define el título, versión, descripción, contacto, licencia y servidores disponibles
 * que se muestran en la interfaz de Swagger UI.
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "API ms-inventory - SmartLogix",
        version = "1.0.0",
        description = """
            # Microservicio de Inventario

            Gestiona el catálogo de productos y el control de stock por bodega
            dentro de la plataforma logística **SmartLogix**.

            ### Funcionalidades Principales
            - CRUD de items de inventario
            - Búsqueda de items por nombre o por bodega
            - Consulta de items con stock bajo un umbral
            - Actualización de cantidad y precio

            ### Códigos de Respuesta Comunes
            - `200`: Operación exitosa
            - `204`: Recurso eliminado correctamente
            - `400`: Error de validación en los datos enviados
            - `404`: Recurso no encontrado
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
        @Server(url = "http://localhost:9091", description = "Servidor de Desarrollo"),
        @Server(url = "http://ms-inventory:9091", description = "Docker Compose / Kubernetes Interno")
    }
)
public class OpenAPIConfig {
}