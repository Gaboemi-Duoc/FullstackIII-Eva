package com.smartlogix.ms_orders.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "API ms-orders - SmartLogix",
        version = "1.0.0",
        description = """
            # Microservicio de Pedidos

            Gestiona el ciclo de vida de las órdenes/pedidos dentro de la
            plataforma logística **SmartLogix**.

            ### Funcionalidades Principales
            - Creación y consulta de órdenes
            - Actualización del estado de una orden
            - Filtrado de órdenes por estado
            - Eliminación de órdenes

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
        @Server(url = "http://localhost:9092", description = "Servidor de Desarrollo"),
        @Server(url = "http://ms-orders:9092", description = "Docker Compose / Kubernetes Interno")
    }
)
public class OpenAPIConfig {
}
