# ms-user

Microservicio REST de gestión de usuarios de la plataforma SmartLogix. Construido con Spring Boot 4.0.6 y Java 25.

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

## Notas

- Las migraciones se ejecutan automáticamente con Flyway al iniciar
- El BFF consume este servicio en `http://localhost:9090`
- El frontend no llama a este servicio directamente, sino a través del BFF
