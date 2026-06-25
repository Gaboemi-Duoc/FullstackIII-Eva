# ms-restock

Microservicio REST de gestión de solicitudes de reposición de stock. Construido con Spring Boot 4.0.6 y Java 21.

## Patrones de Diseño aplicados
- Repository Pattern
- Service Layer
- MVC: Modelo → Servicio → Controlador
- DTO: separación entre datos de entrada y entidad persistida

---

## Cómo levantar

### Prerrequisitos
- Java 21
- Maven
- PostgreSQL corriendo en `localhost:5432`
- ms-inventory corriendo en `http://localhost:9091`

### Crear la base de datos
```sql
CREATE DATABASE restockdb;
CREATE USER restock WITH PASSWORD 'restockpass123';
GRANT ALL PRIVILEGES ON DATABASE restockdb TO restock;
```

### Ejecutar
```bash
cd backend/ms-restock
./mvnw spring-boot:run
```

Disponible en: `http://localhost:9093`

Swagger UI: `http://localhost:9093/swagger-ui/index.html`

---

## Configuración (`application.properties`)

| Propiedad | Valor |
|-----------|-------|
| `server.port` | `9093` |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/restockdb` |
| `spring.datasource.username` | `restock` |
| `spring.datasource.password` | `restockpass123` |
| `inventory-service.url` | `http://localhost:9091` |

---

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/restock` | Lista todas las solicitudes |
| `GET` | `/api/restock/{id}` | Obtiene solicitud por ID |
| `GET` | `/api/restock/estado?valor=PENDIENTE` | Filtra por estado |
| `GET` | `/api/restock/item/{id_item}` | Historial de un ítem |
| `GET` | `/api/restock/bodega?nombre=X` | Solicitudes por bodega |
| `GET` | `/api/restock/bodega/pendientes?nombre=X` | Pendientes por bodega |
| `GET` | `/api/restock/resumen` | Conteo agrupado por estado |
| `POST` | `/api/restock` | Crea nueva solicitud |
| `PUT` | `/api/restock/{id}/estado` | Actualiza estado |
| `DELETE` | `/api/restock/{id}` | Elimina solicitud |

### Ejemplos de body

**Crear solicitud:**
```json
{
  "idItem": 5,
  "nombreItem": "Caja A",
  "bodega": "Bodega Central",
  "cantidadSolicitada": 100
}
```

**Actualizar estado:**
```json
{ "estado": "APROBADA" }
```

Estados válidos: `PENDIENTE`, `APROBADA`, `RECHAZADA`, `COMPLETADA`

---

## Integración con ms-inventory

Al **crear** una solicitud valida que el ítem exista en ms-inventory.
Al **aprobar** una solicitud actualiza automáticamente el stock en ms-inventory sumando la cantidad solicitada.

---

## Pruebas unitarias

Ejecutar tests:
```bash
cd backend/ms-restock
./mvnw test
```

Ver reporte de cobertura JaCoCo:
```bash
# El reporte se genera automáticamente al correr los tests
# Abrir en el navegador:
backend/ms-restock/target/site/jacoco/index.html
```

Cobertura obtenida: **80%**

---

## Notas

- Puerto: `9093`
- Base de datos propia: `restockdb`
- Las migraciones Flyway se ejecutan automáticamente al iniciar
- El BFF consume este servicio en `http://localhost:9093`
- El frontend no llama a este servicio directamente, sino a través del BFF
- `idItem` es referencia lógica (sin FK física) al ítem en ms-inventory