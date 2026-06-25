# SmartLogix — Fullstack III (Evaluación Parcial 3)

Plataforma web fullstack de arquitectura orientada a microservicios para la gestión logística de eCommerce. El proyecto implementa un frontend en React, un BFF, cuatro microservicios en Spring Boot, un API Gateway con KrakenD, y una infraestructura completa orquestada con Docker Compose.

---

## Tabla de contenidos

- [Arquitectura general](#arquitectura-general)
- [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Cómo levantar el proyecto](#cómo-levantar-el-proyecto)

---

## Arquitectura general

El sistema sigue una arquitectura de microservicios donde cada componente tiene una responsabilidad bien definida:

```
[Frontend React :5173]

│

▼

[API Gateway KrakenD :8080]

│

▼

[BFF Spring Boot :8081]

│

┌────┼────┬────────┐

▼    ▼    ▼        ▼

[ms-user  [ms-inventory  [ms-orders  [ms-restock

:9090]      :9091]       :9092]      :9093]

│            │            │           │

[PostgreSQL] [PostgreSQL] [PostgreSQL] [PostgreSQL]

userdb    inventorydb   ordersdb    restockdb
```

El frontend se comunica únicamente con el API Gateway (KrakenD), que enruta las peticiones al BFF, y este las distribuye hacia los microservicios correspondientes.

---

## Tecnologías utilizadas

| Capa | Tecnología | Versión |
|---|---|---|
| Frontend | React + Vite | 19.x / 8.x |
| Routing frontend | React Router DOM | 7.x |
| HTTP client frontend | Axios | 1.x |
| Backend Framework | Spring Boot | 4.0.6 |
| Lenguaje backend | Java | 21 |
| Persistencia | PostgreSQL | 17 |
| Migraciones | Flyway | 11.x |
| Reducción de boilerplate | Lombok | — |
| API Gateway | KrakenD | latest |
| Contenedores | Docker + Docker Compose | — |
| Testing | JUnit 5 + Mockito + JaCoCo | — |



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

## Cómo levantar el proyecto

### Prerrequisitos

- Docker y Docker Compose instalados
- Node.js 18+ (para desarrollo local del frontend)
- Java 21 + Maven (para desarrollo local del backend)

### Levantar con Docker Compose

```bash
git clone <url-del-repositorio>
cd FullstackIII-Eva
docker compose up --build
```

Los servicios estarán disponibles en:

- **Frontend:** http://localhost:5173
- **API Gateway (KrakenD):** http://localhost:8080
- **BFF:** http://localhost:8081
- **ms-user:** http://localhost:9090
- **ms-inventory:** http://localhost:9091
- **ms-orders:** http://localhost:9092
- **ms-restock:** http://localhost:9093

## Servicios pendientes de implementar

Los siguientes servicios están estructurados en el repositorio pero aún no tienen código (contienen únicamente un `dummy.txt`):

| Servicio | Descripción esperada |
|---|---|
| `bff/` | Backend for Frontend — capa de adaptación entre el gateway y el frontend |
| `service-inventory/` | Microservicio de gestión de inventario |
| `service-orders/` | Microservicio de gestión de órdenes |
| `service-restock/` | Microservicio de reposición de stock |

Kafka ya está disponible en la infraestructura Docker, preparado para la comunicación asíncrona entre estos microservicios cuando sean implementados.
