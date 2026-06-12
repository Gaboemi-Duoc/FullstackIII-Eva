package com.smartlogix.bff.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "API SmartLogix BFF",
        version = "1.0.0",
        description = """
            # API Backend for Frontend (BFF) de SmartLogix
            
            ## Sobre el Sistema
            Esta API actúa como la capa de orquestación y agregación para la plataforma logística **SmartLogix**.
            
            ### Funcionalidades Principales
            - **Gestión de Usuarios**: Autenticación, actualización de perfiles y consulta de usuarios
            - **Gestión de Inventario**: CRUD de productos, monitoreo de stock y actualización de precios
            - **Agregación de Respuestas**: Combina datos de múltiples microservicios
            - **Normalización**: Formato de respuesta consistente para el frontend
            
            ### Arquitectura
            Este BFF se comunica con:
            - **ms-user**: Servicio de usuarios (autenticación y datos de perfil)
            - **ms-inventory**: Servicio de inventario (gestión de productos y stock)
            - **ms-orders**: Servicio de pedidos (procesamiento de órdenes)
            - **ms-restock**: Servicio de reabastecimiento (gestión automática de stock mínimo)
            
            ### Flujo de Autenticación
            1. Envíe credenciales a `/api/bff/users/login`
            2. Reciba un token JWT en la respuesta
            3. Incluya el token en todas las peticiones protegidas:
               `Authorization: Bearer <token>`
            
            ### Códigos de Respuesta Comunes
            - `200`: Operación exitosa
            - `201`: Recurso creado correctamente
            - `400`: Error de validación en los datos enviados
            - `401`: No autenticado - Token inválido o ausente
            - `403`: No autorizado - Permisos insuficientes
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
        @Server(
            url = "http://localhost:8081",
            description = "Servidor de Desarrollo"
        ),
        @Server(
            url = "http://bff:8081",
            description = "Docker Compose / Kubernetes Interno"
        ),
        @Server(
            url = "https://api.smartlogix.com",
            description = "Servidor de Producción"
        )
    },
    security = @SecurityRequirement(name = "Bearer Authentication")
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = """
        Autenticación mediante token JWT.
        
        Para obtener el token:
        1. Use el endpoint /api/bff/users/login
        2. Copie el valor del campo 'token' en la respuesta
        3. Inclúyalo en el header Authorization: Bearer {token}
        """
)
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .tags(Arrays.asList(
                        new Tag().name("User Management")
                                .description("Operaciones de autenticación y perfiles de usuario"),
                        new Tag().name("Gestion de Inventario")
                                .description("Operaciones CRUD para productos y control de stock"),
                        new Tag().name("Health Check")
                                .description("Endpoints para verificar la salud y disponibilidad del BFF")
                ));
    }
}