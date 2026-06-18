# ms-restock

Microservicio REST de gestión de **solicitudes de reposición de stock**.
Permite crear, consultar, actualizar el estado y eliminar solicitudes
asociadas a ítems del inventario. Usa PostgreSQL con migraciones Flyway.

## Patrones de Diseño aplicados
- Repository Pattern
- Service Layer
- MVC: Modelo → Servicio → Controlador
- DTO: separación entre datos de entrada y entidad persistida

---

## Requisitos

- Java 25
- Maven
- PostgreSQL corriendo en `localhost:5432`

## Crear la base de datos

```sql
CREATE DATABASE restockdb;
CREATE USER restock WITH PASSWORD 'restockpass123';
GRANT ALL PRIVILEGES ON DATABASE restockdb TO restock;
```

## Ejecutar localmente

```bash
cd backend/ms-restock
./mvnw spring-boot:run
```

Disponible en: `http://localhost:9093`

Swagger UI: `http://localhost:9093/swagger-ui/index.html`

---

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/restock` | Lista todas las solicitudes |
| GET | `/api/restock/{id}` | Obtiene solicitud por ID |
| GET | `/api/restock/estado?valor=PENDIENTE` | Filtra por estado |
| GET | `/api/restock/item/{id_item}` | Historial de un ítem |
| GET | `/api/restock/bodega?nombre=X` | Solicitudes por bodega |
| GET | `/api/restock/bodega/pendientes?nombre=X` | Pendientes por bodega |
| GET | `/api/restock/resumen` | Conteo agrupado por estado |
| POST | `/api/restock` | Crea nueva solicitud |
| PUT | `/api/restock/{id}/estado` | Actualiza estado |
| DELETE | `/api/restock/{id}` | Elimina solicitud |

## Ejemplo body POST

```json
{
  "id_item": 5,
  "nombre_item": "Caja A",
  "bodega": "Bodega Central",
  "cantidad_solicitada": 100
}
```

## Ejemplo body PUT

```json
{ "estado": "APROBADA" }
```

---

## Notas

- Puerto: `9093`
- Base de datos propia: `restockdb` (aislamiento de microservicios)
- Las migraciones Flyway se ejecutan automáticamente al iniciar
- El BFF consume este servicio en `http://localhost:9093`
- El frontend no llama a este servicio directamente, sino a través del BFF
- `id_item` es referencia lógica (sin FK física) al ítem en `ms-inventory`