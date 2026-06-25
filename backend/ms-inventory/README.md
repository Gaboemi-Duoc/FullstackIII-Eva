# ms-inventory

Microservicio REST de gestión de inventario de la plataforma SmartLogix. Construido con Spring Boot 4.0.6 y Java 25.

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
- PostgreSQL corriendo en `localhost:5432`

### Crear la base de datos
```sql
CREATE DATABASE inventorydb;
CREATE USER inventory WITH PASSWORD 'inventorypass123';
GRANT ALL PRIVILEGES ON DATABASE inventorydb TO inventory;
```

### Ejecutar
```bash
cd backend/ms-inventory
./mvnw spring-boot:run
```

Disponible en: `http://localhost:9091`

Swagger UI: `http://localhost:9091/swagger-ui/index.html`

---

## Configuración (`application.properties`)

| Propiedad | Valor |
|-----------|-------|
| `server.port` | `9091` |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/inventorydb` |
| `spring.datasource.username` | `inventory` |
| `spring.datasource.password` | `inventorypass123` |

---

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/inventory` | Lista todos los ítems |
| `GET` | `/api/inventory/{id}` | Obtiene ítem por ID |
| `GET` | `/api/inventory/buscar?nombre={n}` | Busca ítem por nombre |
| `GET` | `/api/inventory/stock-bajo?umbral={n}` | Ítems con stock bajo |
| `GET` | `/api/inventory/bodega?nombre={n}` | Ítems por bodega |
| `GET` | `/api/inventory/stock-por-bodega` | Stock total por bodega |
| `POST` | `/api/inventory` | Crea un nuevo ítem |
| `PUT` | `/api/inventory/{id}/cantidad` | Actualiza cantidad |
| `PUT` | `/api/inventory/{id}/precio` | Actualiza precio |
| `DELETE` | `/api/inventory/{id}` | Elimina un ítem |

### Ejemplos de body

**Crear ítem:**
```json
{
  "nombre": "Caja A",
  "descripcion": "Caja mediana",
  "cantidad": 100,
  "precio": 1500.0,
  "bodega": "Bodega Central"
}
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

## Pruebas unitarias

Ejecutar tests:
```bash
cd backend/ms-inventory
./mvnw test
```

Ver reporte de cobertura JaCoCo:
```bash
# El reporte se genera automáticamente al correr los tests
# Abrir en el navegador:
backend/ms-inventory/target/site/jacoco/index.html
```

Cobertura obtenida: **100%**

---

## Notas

- Las migraciones se ejecutan automáticamente con Flyway al iniciar
- El BFF consume este servicio en `http://localhost:9091`
- El frontend no llama a este servicio directamente, sino a través del BFF