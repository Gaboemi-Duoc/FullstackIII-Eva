package com.smartlogix.ms_restock.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "API ms-restock - SmartLogix",
        version = "1.0.0",
        description = """
            # Microservicio de Reabastecimiento

            Gestiona las solicitudes de reabastecimiento (restock) de stock
            dentro de la plataforma logística **SmartLogix**.

            ### Funcionalidades Principales
            - Creación y consulta de solicitudes de restock
            - Filtrado por estado, por item o por bodega
            - Resumen de solicitudes agrupadas por estado
            - Actualización del estado de una solicitud

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
        @Server(url = "http://localhost:9093", description = "Servidor de Desarrollo"),
        @Server(url = "http://ms-restock:9093", description = "Docker Compose / Kubernetes Interno")
    }
)
public class OpenAPIConfig {
}
