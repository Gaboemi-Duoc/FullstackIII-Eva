# BFF — Backend for Frontend

Capa intermediaria entre el frontend y los microservicios internos. Maneja autenticación JWT, autoriza las peticiones y reenvía las llamadas a `service-user` y `service-inventory`.

---

## Cómo levantar

### Prerrequisitos

- Java 25
- Maven
- `service-user` corriendo en `http://localhost:9090`
- `service-inventory` corriendo en `http://localhost:9091`

### Ejecutar

```bash
cd bff
./mvnw spring-boot:run
```

El BFF queda disponible en: **`http://localhost:8081`**

---

## Configuración (`application.properties`)

| Propiedad | Valor |
|-----------|-------|
| `server.port` | `8081` |
| `user-service.url` | `http://localhost:9090` |
| `inventory-service.url` | `http://localhost:9091` |

---

## Endpoints

### Usuarios

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| `POST` | `/api/bff/users/login` | No | Login, retorna JWT |
| `GET` | `/api/bff/users/users/{id}` | Bearer Token | Obtiene datos del usuario |
| `PUT` | `/api/bff/users/users/{id}/username` | Bearer Token | Actualiza username |
| `GET` | `/api/bff/users/health` | No | Health check del BFF |

### Inventario

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| `GET` | `/api/bff/inventory` | Bearer Token | Lista todos los ítems |
| `GET` | `/api/bff/inventory/{id}` | Bearer Token | Obtiene ítem por ID |
| `GET` | `/api/bff/inventory/stock-bajo?umbral={n}` | Bearer Token | Ítems con bajo stock |
| `POST` | `/api/bff/inventory` | Bearer Token | Crea un ítem |
| `PUT` | `/api/bff/inventory/{id}/cantidad` | Bearer Token | Actualiza cantidad |
| `PUT` | `/api/bff/inventory/{id}/precio` | Bearer Token | Actualiza precio |
| `DELETE` | `/api/bff/inventory/{id}` | Bearer Token | Elimina un ítem |

---

## Flujo de autenticación

```
Frontend → POST /api/bff/users/login → BFF → service-user:9090
BFF genera JWT → retorna token al Frontend

Frontend → GET /api/bff/inventory (Bearer token) → BFF valida JWT → service-inventory:9091
```

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
