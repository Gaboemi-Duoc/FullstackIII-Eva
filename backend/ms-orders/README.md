# ms-orders

Microservicio REST de gestión de órdenes de compra de la plataforma SmartLogix. Construido con Spring Boot 4.0.6 y Java 25.

## Patrones de Diseño aplicados
- Repository Pattern
- Service Layer
- MVC: Modelo → Servicio → Controlador
- DTO: separación entre datos de entrada y entidad persistida

---

## Cómo levantar

### Prerrequisitos
- Java 25
- Maven
- PostgreSQL corriendo en `localhost:5434`

### Crear la base de datos
```sql
CREATE DATABASE ordersdb;
CREATE USER orders WITH PASSWORD 'orderspass123';
GRANT ALL PRIVILEGES ON DATABASE ordersdb TO orders;
```

### Ejecutar
```bash
cd backend/ms-orders
./mvnw spring-boot:run
```

Disponible en: `http://localhost:9092`

Swagger UI: `http://localhost:9092/swagger-ui/index.html`

---

## Configuración (`application.properties`)

| Propiedad | Valor |
|-----------|-------|
| `server.port` | `9092` |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5434/ordersdb` |
| `spring.datasource.username` | `orders` |
| `spring.datasource.password` | `orderspass123` |

> **Nota:** El puerto 5434 corresponde al puerto de host asignado a `orders-db` en `docker-compose.yml`.

---

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/orders` | Lista todas las órdenes |
| `GET` | `/api/orders/{id}` | Obtiene orden por ID |
| `GET` | `/api/orders/status/{status}` | Filtra órdenes por estado |
| `POST` | `/api/orders` | Crea una nueva orden |
| `PUT` | `/api/orders/{id}/status` | Actualiza el estado de una orden |
| `DELETE` | `/api/orders/{id}` | Elimina una orden |

### Estados válidos (`OrderStatus`)

`PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED`

### Ejemplos de body

**Crear orden:**
```json
{
  "idItem": 3,
  "nombreItem": "Caja B",
  "cantidadSolicitada": 10,
  "bodega": "Bodega Central"
}
```

**Actualizar estado:**
```json
{ "status": "CONFIRMED" }
```

---

## Pruebas unitarias

Ejecutar tests:
```bash
cd backend/ms-orders
./mvnw test
```

Ver reporte de cobertura JaCoCo:
```bash
# El reporte se genera automáticamente al correr los tests
# Abrir en el navegador:
backend/ms-orders/target/site/jacoco/index.html
```

---

## Notas

- Las migraciones se ejecutan automáticamente con Flyway al iniciar
- El BFF consume este servicio en `http://localhost:9092`
- El frontend no llama a este servicio directamente, sino a través del BFF
- La validación de stock disponible y el descuento del mismo se realiza en el BFF al crear la orden
    