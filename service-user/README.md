

## Microservicio de Usuarios (`localhost:8080`)

Microservicio RESTful construido con Spring Boot 4.0.6 y Java 25. Gestiona el ciclo de vida completo de los usuarios de la plataforma.

#### Patrones de Diseño usados:
    - Repository Pattern
    - Service Layer
    - MVC: Variacion de Modelo, Servicio, Controlador

---

## Cómo levantar el proyecto

### Prerrequisitos
- Java 25
- Maven
- PostgreSQL corriendo en `localhost:5432`

### Crear la base de datos
```sql
CREATE DATABASE user_service_db;
```

### Ejecutar
Correr el siguiente comando o ejecutar en archivo bash:
```bash
cd service-user
./mvnw spring-boot:run
```


---

## Configuración (`application.properties`)

| Propiedad | Valor |
|-----------|-------|
| `server.port` | `9090` |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/user_service_db` |
| `spring.datasource.username` | `postgres` |
| `spring.datasource.password` | `Informatica.25` |

---

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/users` | Lista todos los usuarios |
| `GET` | `/api/users/{id}` | Obtiene usuario por ID |
| `POST` | `/api/users/register` | Registra un nuevo usuario |
| `POST` | `/api/users/login` | Autentica un usuario |
| `PUT` | `/api/users/{id}/username` | Actualiza el username |
| `DELETE` | `/api/users/{id}` | Elimina un usuario |

### Ejemplos de body

**Login:**
```json
{ "username": "juan123", "password": "mipassword" }
```

**Registro:**
```json
{ "username": "juan123", "email": "juan@example.com", "password": "mipassword" }
```

**Actualizar username:**
```json
{ "username": "nuevonombre" }
```

---

#### `User.java` — Entidad JPA

Modelo de datos persistido en base de datos con los siguientes campos:

| Campo | Tipo | Restricción |
|---|---|---|
| `id_user` | Long | PK, autoincremental |
| `username` | String | NOT NULL |
| `email` | String | NOT NULL |
| `password` | String | NOT NULL |

Utiliza Lombok (`@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`) para eliminar código repetitivo. La tabla se mapea con el nombre `"user"` (entre comillas para evitar conflictos con palabras reservadas en SQL).

#### `UserRepository.java` — Repositorio Spring Data JPA

Extiende `JpaRepository<User, Long>`, lo que provee automáticamente los métodos CRUD estándar. Agrega dos consultas personalizadas derivadas del nombre del método:

- `findByUsernameAndPassword(String username, String password)` — usada para el login.
- `findByEmail(String email)` — disponible para búsquedas por email.

#### `UserService.java` — Capa de lógica de negocio

Clase de servicio que encapsula todas las operaciones sobre usuarios:

- **`listarUsuarios()`** — Retorna todos los usuarios registrados.
- **`obtenerPorId(Long id)`** — Busca un usuario por ID; lanza `RuntimeException` si no existe.
- **`registrarUsuario(User user)`** — Persiste un nuevo usuario en la base de datos.
- **`login(String username, String password)`** — Valida credenciales; lanza `RuntimeException("Credenciales incorrectas")` si no coinciden.
- **`actualizarUsername(Long id, String nuevoUsername)`** — Obtiene el usuario, actualiza su username y lo guarda.
- **`eliminarUsuario(Long id)`** — Elimina el usuario por ID.

#### `UserController.java` — Controlador REST

El servicio queda disponible en: **`http://localhost:9090`**

## Documentacion
http://localhost:9090/swagger-ui/index.html#/

## Notas

- Las migraciones de base de datos se ejecutan automáticamente con **Flyway** al iniciar.
- El BFF consume este servicio en `http://localhost:9090`.
- Este servicio es de uso **interno**: el frontend no lo llama directamente, sino a través del BFF.