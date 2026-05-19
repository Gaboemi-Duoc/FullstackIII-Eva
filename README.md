# SmartLogix — Fullstack III (Evaluación)

Aplicación web fullstack de arquitectura orientada a microservicios para la gestión de usuarios de la plataforma **SmartLogix**. El proyecto implementa un frontend en React, un microservicio de usuarios en Spring Boot, un API Gateway con KrakenD, y una infraestructura completa orquestada con Docker Compose.

---

## Tabla de contenidos

- [Arquitectura general](#arquitectura-general)
- [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Componentes implementados](#componentes-implementados)
  - [Frontend (React + Vite)](#frontend-react--vite)
  - [Microservicio de Usuarios (Spring Boot)](#microservicio-de-usuarios-spring-boot)
  - [API Gateway (KrakenD)](#api-gateway-krakend)
  - [Infraestructura Docker](#infraestructura-docker)
- [Endpoints disponibles](#endpoints-disponibles)
- [Cómo levantar el proyecto](#cómo-levantar-el-proyecto)
- [Servicios pendientes de implementar](#servicios-pendientes-de-implementar)

---

## Arquitectura general

El sistema sigue una arquitectura de microservicios donde cada componente tiene una responsabilidad bien definida:

```
[Frontend React :5173]
        │
        ▼
[BFF Spring Boot :8081]
        │
   ┌────┴────┐
   ▼         ▼
[service-user    [service-inventory
    :9090]             :9091]
      │                   │
 [PostgreSQL]        [PostgreSQL]
 user_service_db  inventory_service_db
```

El frontend se comunica **únicamente con el BFF**, que actúa como punto de entrada único, maneja la autenticación JWT y enruta las peticiones hacia los microservicios correspondientes.

> El diagrama de contenedores completo se encuentra en `docs/Diagrama de Contenedores - Fullstack III.drawio.png`.

---

## Tecnologías utilizadas

| Capa | Tecnología | Versión |
|---|---|---|
| Frontend Framework / Librerías | React | 19.x |
| Bundler | Vite | 8.x |
| Routing frontend | React Router DOM | 7.x |
| HTTP client frontend | Axios | 1.16.x |
| Backend Framework | Spring Boot | 3.x |
| Lenguaje backend | Java | 25 |
| Persistencia | PostgreSQL | 17 |
| Migraciones | Flyway | — |
| Reducción de boilerplate | Lombok | — |
| Autenticación | JWT | — |
| Contenedores | Docker + Docker Compose | — |

---

## Estructura del proyecto

```
FullstackIII-Eva-indev/
├── docker-compose.yml          # Orquestación de todos los servicios
├── docs/                       # Documentación extra del proyecto
├── frontend/                   # Aplicación React + Vite
├── bff/                        # Backend for Frontend (Spring Boot)
├── service-user/               # Microservicio de usuarios (Spring Boot)
├── service-inventory/          # Microservicio de inventario (Spring Boot)
├── service-orders/             # Microservicio de órdenes (pendiente)
└── service-restock/            # Microservicio de restock (pendiente)
```
---

### Infraestructura Docker

El archivo `docker-compose.yml` define cinco servicios dentro de la red `smartlogix-net` (bridge):

| Servicio | Imagen / Build | Puerto host | Descripción |
|---|---|---|---|
| `backend` | `./FullstackIII-Backend` | `8080` | Microservicio principal (Spring Boot) |
| `bff` | `./smartlogix-bff` | `8081` | Backend for Frontend (pendiente de implementar) |
| `krakend` | `devopsfaith/krakend:latest` | `8090` | API Gateway |
| `frontend` | `./FullstackIII-Front` | `5173` | Aplicación React |

---

### Prerrequisitos

- Docker y Docker Compose instalados
- Node.js 18+ (para desarrollo local del frontend)
- Java 25 + Maven (para desarrollo local del backend)

### Levantar con Docker Compose

```bash
# Clonar el repositorio
git clone <url-del-repositorio>
cd FullstackIII-Eva-main

# Levantar todos los servicios
docker compose up --build
```

Los servicios estarán disponibles en:

- **Frontend:** http://localhost:5173
- **API Gateway (KrakenD):** http://localhost:8090
- **Backend (service-user):** http://localhost:9090
- **Backend (service-inventory):** http://localhost:9091
- **BFF:** http://localhost:8080

## Servicios pendientes de implementar

Los siguientes servicios están estructurados en el repositorio pero aún no tienen código (contienen únicamente un `dummy.txt`):

| Servicio | Descripción esperada |
|---|---|
| `bff/` | Backend for Frontend — capa de adaptación entre el gateway y el frontend |
| `service-inventory/` | Microservicio de gestión de inventario |
| `service-orders/` | Microservicio de gestión de órdenes |
| `service-restock/` | Microservicio de reposición de stock |

Kafka ya está disponible en la infraestructura Docker, preparado para la comunicación asíncrona entre estos microservicios cuando sean implementados.
