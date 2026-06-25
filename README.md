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
[Frontend React :30080]
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
| Lenguaje backend | Java | 25 |
| Persistencia | PostgreSQL | 17 |
| Migraciones | Flyway | 11.x |
| Reducción de boilerplate | Lombok | — |
| API Gateway | KrakenD | latest |
| Contenedores | Docker + Docker Compose | — |
| Testing | JUnit 5 + Mockito + JaCoCo | — |

---

## Estructura del proyecto

```
FullstackIII-Eva-main/
├── docker-compose.yml          # Orquestación de todos los servicios
├── docs/                       # Documentación extra del proyecto
├── frontend/                   # Aplicación React + Vite
├── krakend/                    # Configuración del API Gateway
└── backend/
    ├── bff/                    # Backend for Frontend (Spring Boot :8081)
    ├── ms-user/                # Microservicio de usuarios (Spring Boot :9090)
    ├── ms-inventory/           # Microservicio de inventario (Spring Boot :9091)
    ├── ms-orders/              # Microservicio de órdenes (Spring Boot :9092)
    └── ms-restock/             # Microservicio de reposición de stock (Spring Boot :9093)
```

---

## Infraestructura Docker

El archivo `docker-compose.yml` define todos los servicios dentro de la red `app-network` (bridge):

| Servicio | Build | Puerto host | Descripción |
|---|---|---|---|
| `ms-user` | `./backend/ms-user` | `9090` | Microservicio de usuarios |
| `ms-inventory` | `./backend/ms-inventory` | `9091` | Microservicio de inventario |
| `ms-orders` | `./backend/ms-orders` | `9092` | Microservicio de órdenes |
| `ms-restock` | `./backend/ms-restock` | `9093` | Microservicio de reposición de stock |
| `bff` | `./backend/bff` | `8081` | Backend for Frontend |
| `krakend` | `devopsfaith/krakend:latest` | `8080` | API Gateway |
| `frontend` | `./frontend` | `30080` | Aplicación React |

---

## Cómo levantar el proyecto

### Prerrequisitos

- Docker y Docker Compose instalados
- Node.js 22+ (para desarrollo local del frontend)
- Java 25 + Maven (para desarrollo local del backend)

### Levantar con Docker Compose

```bash
git clone <url-del-repositorio>
cd FullstackIII-Eva-main
docker compose up --build
```

Los servicios estarán disponibles en:

- **Frontend:** http://localhost:30080
- **API Gateway (KrakenD):** http://localhost:8080
- **BFF:** http://localhost:8081
- **ms-user:** http://localhost:9090
- **ms-inventory:** http://localhost:9091
- **ms-orders:** http://localhost:9092
- **ms-restock:** http://localhost:9093

### Levantar servicios individualmente (desarrollo local)

Cada microservicio requiere su propia base de datos PostgreSQL corriendo. Los puertos de host para las bases de datos en Docker son:

| Base de datos | Puerto host |
|---|---|
| `userdb` | 5432 |
| `inventorydb` | 5433 |
| `ordersdb` | 5434 |
| `restockdb` | 5435 |

```bash
# Levantar solo las bases de datos
docker compose up user-db inventory-db orders-db restock-db

# Luego en terminales separadas:
cd backend/ms-user && ./mvnw spring-boot:run
cd backend/ms-inventory && ./mvnw spring-boot:run
cd backend/ms-orders && ./mvnw spring-boot:run
cd backend/ms-restock && ./mvnw spring-boot:run
cd backend/bff && ./mvnw spring-boot:run
cd frontend && npm install && npm run dev
```
