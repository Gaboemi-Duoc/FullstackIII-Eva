# Microservicio de Inventario

Microservicio REST de gestión de inventario. Permite crear, consultar, actualizar y eliminar ítems del inventario. Utiliza PostgreSQL con migraciones Flyway.

#### Patrones de Diseño usados:
    - Repository Pattern
    - Service Layer
    - MVC: Variacion de Modelo, Servicio, Controlador
    
---

## Cómo levantar

### Prerrequisitos

- Java 25
- Maven
- PostgreSQL corriendo en `localhost:5432`

### Crear la base de datos

```sql
CREATE DATABASE inventory_service_db;
```

### Ejecutar

```bash
cd service-inventory
./mvnw spring-boot:run
```

El servicio queda disponible en: **`http://localhost:9091`**

---

## Configuración (`application.properties`)

| Propiedad | Valor |
|-----------|-------|
| `server.port` | `9091` |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/inventory_service_db` |
| `spring.datasource.username` | `postgres` |
| `spring.datasource.password` | `Informatica.25` |

---

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/inventory` | Lista todos los ítems |
| `GET` | `/api/inventory/{id}` | Obtiene ítem por ID |
| `GET` | `/api/inventory/buscar?nombre={n}` | Busca ítem por nombre |
| `GET` | `/api/inventory/stock-bajo?umbral={n}` | Ítems con stock menor al umbral |
| `GET` | `/api/inventory/bodega?nombre={n}` | Ítems por bodega |
| `GET` | `/api/inventory/stock-por-bodega` | Stock total agrupado por bodega |
| `POST` | `/api/inventory` | Crea un nuevo ítem |
| `PUT` | `/api/inventory/{id}/cantidad` | Actualiza cantidad |
| `PUT` | `/api/inventory/{id}/precio` | Actualiza precio |
| `DELETE` | `/api/inventory/{id}` | Elimina un ítem |

### Ejemplos de body

**Crear ítem:**
```json
{ "name": "Caja A", "description": "Caja mediana", "cantidad": 100, "precio": 1500.0 }
```

**Actualizar cantidad:**
```json
{ "cantidad": 50 }
```

**Actualizar precio:**
```json
{ "precio": 1800.0 }
```

---

## Notas

- Las migraciones se ejecutan automáticamente con **Flyway** al iniciar.
- El BFF consume este servicio en `http://localhost:9091`.
- Este servicio es de uso **interno**: el frontend no lo llama directamente, sino a través del BFF.
