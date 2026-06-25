

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

Microservicio de Usuarios (localhost:8080)

Microservicio RESTful construido con Spring Boot 4.0.6 y Java 25. Gestiona el ciclo de vida completo de los usuarios de la plataforma.

Patrones de Diseño usados:

- Repository Pattern
- Service Layer
- MVC: Variacion de Modelo, Servicio, Controlador


Cómo levantar el proyecto

Prerrequisitos


Java 25
Maven
PostgreSQL corriendo en localhost:5432


Crear la base de datos

sqlCREATE DATABASE user_service_db;

Ejecutar

Correr el siguiente comando o ejecutar en archivo bash:

bashcd service-user
./mvnw spring-boot:run


Configuración (application.properties)

PropiedadValorserver.port9090spring.datasource.urljdbc:postgresql://localhost:5432/user_service_dbspring.datasource.usernamepostgresspring.datasource.passwordInformatica.25brevo.api.key(variable de entorno BREVO_API_KEY)brevo.sender.email(variable de entorno BREVO_SENDER_EMAIL)


✉️ Envío de correo de bienvenida (API de Brevo)

Cuando alguien se registra (POST /api/users/register), el sistema envía automáticamente un correo de bienvenida al email ingresado. Esto se hace con la API de Brevo (antes llamada Sendinblue), un servicio de envío de correos transaccionales con plan gratuito (300 correos/día, sin tarjeta de crédito).

Si el correo no se logra enviar por algún motivo, el registro de todas formas se completa — el envío de correo nunca bloquea ni rompe el registro, solo queda un mensaje de error en la consola.

Cómo configurarlo (cada integrante del equipo debe hacer esto en su máquina)


Crea una cuenta gratis en brevo.com (no pide tarjeta). Puede ser tu cuenta personal, no se comparte entre el equipo.
Ve a tu menú de cuenta → Settings → SMTP & API → pestaña "API Keys" → Generate a new API key. Cópiala (solo se muestra una vez).
Ve a Settings → Senders, Domains, IPs → pestaña "Senders" → Add a sender, agrega tu correo (puede ser tu Gmail) y verifícalo con el código de 6 dígitos que te llega a ese correo.
En la raíz del proyecto, crea un archivo .env (cópialo desde .env.example, que sí está en el repo) con tus datos:


   BREVO_API_KEY=tu_clave_real_de_brevo
   BREVO_SENDER_EMAIL=tu_correo_verificado

⚠️ Este archivo .env no se sube a git (está en .gitignore) porque contiene una credencial personal — cada integrante usa la suya.
5. Levanta el servicio con Docker (docker-compose up --build ms-user desde la raíz) o, si corres con ./mvnw spring-boot:run directo, exporta esas dos variables en tu terminal antes de ejecutar.

Cómo probar que funciona


Abre http://localhost:9090/swagger-ui/index.html#/
Ejecuta POST /api/users/register con un correo real al que tengas acceso:


json   { "username": "prueba", "email": "tu_correo_de_prueba@gmail.com", "password": "12345678" }


Revisa la bandeja de entrada (y spam) de ese correo — debería llegar un correo de bienvenida en pocos segundos.


Archivos involucrados

ArchivoRolEmailService.javaConstruye y envía la petición HTTP a la API de Brevo (POST https://api.brevo.com/v3/smtp/email)UserService.javaLlama a EmailService justo después de guardar el nuevo usuarioapplication.propertiesLee las credenciales desde variables de entorno.env (no subido a git)Guarda la API key y el correo remitente de cada integrante


Endpoints

MétodoRutaDescripciónGET/api/usersLista todos los usuariosGET/api/users/{id}Obtiene usuario por IDPOST/api/users/registerRegistra un nuevo usuarioPOST/api/users/loginAutentica un usuarioPUT/api/users/{id}/usernameActualiza el usernameDELETE/api/users/{id}Elimina un usuario

Ejemplos de body

Login:

json{ "username": "juan123", "password": "mipassword" }

Registro:

json{ "username": "juan123", "email": "juan@example.com", "password": "mipassword" }

Actualizar username:

json{ "username": "nuevonombre" }


User.java — Entidad JPA

Modelo de datos persistido en base de datos con los siguientes campos:

CampoTipoRestricciónid_userLongPK, autoincrementalusernameStringNOT NULLemailStringNOT NULLpasswordStringNOT NULL

Utiliza Lombok (@Data, @AllArgsConstructor, @NoArgsConstructor) para eliminar código repetitivo. La tabla se mapea con el nombre "user" (entre comillas para evitar conflictos con palabras reservadas en SQL).

UserRepository.java — Repositorio Spring Data JPA

Extiende JpaRepository<User, Long>, lo que provee automáticamente los métodos CRUD estándar. Agrega dos consultas personalizadas derivadas del nombre del método:


findByUsernameAndPassword(String username, String password) — usada para el login.
findByEmail(String email) — disponible para búsquedas por email.


UserService.java — Capa de lógica de negocio

Clase de servicio que encapsula todas las operaciones sobre usuarios:


listarUsuarios() — Retorna todos los usuarios registrados.
obtenerPorId(Long id) — Busca un usuario por ID; lanza RuntimeException si no existe.
registrarUsuario(User user) — Persiste un nuevo usuario en la base de datos.
login(String username, String password) — Valida credenciales; lanza RuntimeException("Credenciales incorrectas") si no coinciden.
actualizarUsername(Long id, String nuevoUsername) — Obtiene el usuario, actualiza su username y lo guarda.
eliminarUsuario(Long id) — Elimina el usuario por ID.


UserController.java — Controlador REST

El servicio queda disponible en: http://localhost:9090

Documentacion

http://localhost:9090/swagger-ui/index.html#/

Notas


Las migraciones de base de datos se ejecutan automáticamente con Flyway al iniciar.
El BFF consume este servicio en http://localhost:9090.
Este servicio es de uso interno: el frontend no lo llama directamente, sino a través del BFF.