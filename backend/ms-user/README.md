

# ms-user

Microservicio REST de gestión de usuarios de la plataforma SmartLogix. Construido con Spring Boot 4.0.6 y Java 21.

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

### Crear la base de datos
```sql
CREATE DATABASE userdb;
CREATE USER "user" WITH PASSWORD 'userpass123';
GRANT ALL PRIVILEGES ON DATABASE userdb TO "user";
```

### Ejecutar
```bash
cd backend/ms-user
./mvnw spring-boot:run
```

Disponible en: `http://localhost:9090`

Swagger UI: `http://localhost:9090/swagger-ui/index.html`

---

## Configuración (`application.properties`)

| Propiedad | Valor |
|-----------|-------|
| `server.port` | `9090` |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/userdb` |
| `spring.datasource.username` | `user` |
| `spring.datasource.password` | `userpass123` |

---

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/users/{id}` | Obtiene usuario por ID |
| `POST` | `/api/users/register` | Registra un nuevo usuario |
| `POST` | `/api/users/login` | Autentica un usuario |
| `PUT` | `/api/users/{id}/username` | Actualiza el username |
| `DELETE` | `/api/users/{id}` | Elimina un usuario |

### Ejemplos de body

**Registro:**
```json
{ "username": "juan123", "email": "juan@example.com", "password": "mipassword" }
```

**Login:**
```json
{ "username": "juan123", "password": "mipassword" }
```

**Actualizar username:**
```json
{ "username": "nuevonombre" }
```

---

## Pruebas unitarias

Ejecutar tests:
```bash
cd backend/ms-user
./mvnw test
```

Ver reporte de cobertura JaCoCo:
```bash
# El reporte se genera automáticamente al correr los tests
# Abrir en el navegador:
backend/ms-user/target/site/jacoco/index.html
```

Cobertura obtenida: **100%**

---

## Notas

- Las migraciones se ejecutan automáticamente con Flyway al iniciar
- El BFF consume este servicio en `http://localhost:9090`
- El frontend no llama a este servicio directamente, sino a través del BFF