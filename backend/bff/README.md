# BFF — Backend for Frontend

Capa intermediaria entre el frontend y los microservicios internos. Maneja autenticación JWT, autoriza las peticiones y las reenvía a ms-user, ms-inventory y ms-restock.

---

## Cómo levantar

### Prerrequisitos
- Java 21
- Maven
- ms-user corriendo en `http://localhost:9090`
- ms-inventory corriendo en `http://localhost:9091`
- ms-restock corriendo en `http://localhost:9093`

### Ejecutar
```bash
cd backend/bff
./mvnw spring-boot:run
```

Disponible en: `http://localhost:8081`

Swagger UI: `http://localhost:8081/swagger-ui/index.html`

---

## Configuración (`application.properties`)

| Propiedad | Valor |
|-----------|-------|
| `server.port` | `8081` |
| `user-service.url` | `http://localhost:9090` |
| `inventory-service.url` | `http://localhost:9091` |
| `restock-service.url` | `http://localhost:9093` |

---

## Endpoints

### Usuarios

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| `POST` | `/api/bff/users/login` | No | Login, retorna JWT |
| `GET` | `/api/bff/users/{id}` | Bearer Token | Obtiene datos del usuario |
| `PUT` | `/api/bff/users/{id}/username` | Bearer Token | Actualiza username |

### Inventario

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| `GET` | `/api/bff/inventory` | Bearer Token | Lista todos los ítems |
| `GET` | `/api/bff/inventory/{id}` | Bearer Token | Obtiene ítem por ID |
| `POST` | `/api/bff/inventory` | Bearer Token | Crea un ítem |
| `PUT` | `/api/bff/inventory/{id}/cantidad` | Bearer Token | Actualiza cantidad |
| `PUT` | `/api/bff/inventory/{id}/precio` | Bearer Token | Actualiza precio |
| `DELETE` | `/api/bff/inventory/{id}` | Bearer Token | Elimina un ítem |

### Restock

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| `GET` | `/api/bff/restock` | Bearer Token | Lista todas las solicitudes |
| `GET` | `/api/bff/restock/{id}` | Bearer Token | Obtiene solicitud por ID |
| `GET` | `/api/bff/restock/estado?valor=X` | Bearer Token | Filtra por estado |
| `GET` | `/api/bff/restock/resumen` | Bearer Token | Conteo por estado |
| `POST` | `/api/bff/restock` | Bearer Token | Crea solicitud |
| `PUT` | `/api/bff/restock/{id}/estado` | Bearer Token | Actualiza estado |
| `DELETE` | `/api/bff/restock/{id}` | Bearer Token | Elimina solicitud |

---

## Flujo de autenticación

Frontend → POST /api/bff/users/login → BFF → ms-user:9090

BFF genera JWT → retorna token al Frontend
Frontend → GET /api/bff/inventory (Bearer token) → BFF valida JWT → ms-inventory:9091

### Ejemplo — Login

**Request:**
```json
{ "username": "juan123", "password": "mipassword" }
```

**Response:**
```json
{
  "success": true,
  "data": { "token": "<jwt>", "id_user": 1, "username": "juan123" },
  "statusCode": 200
}
```

Usar el token recibido como `Authorization: Bearer <token>` en todas las peticiones siguientes.
