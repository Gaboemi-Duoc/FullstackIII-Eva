# SmartLogix

## API Rest de Backend

Se levanta el backend ejecutando el siguiente comando .\mvnw spring-boot:run

Http Endpoint: http://localhost:8080/api/users

Documentacion Swagger UI: http://localhost:8080/swagger-ui.html

Acceso a Base de Datis H2: http://localhost:8080/h2-console Username: sa Password: (vacio)

Patrones de Diseño usados:
    Repository Pattern
    Service Layer
    MVC: Variacion de Modelo, Servicio, Controlador

NOTAS: La base de datos se reinicia cada vez que se inicia el repositorio, util para el ambiente de desarrollo, pero a la hora de desplegarlo la configuracion debe ser cambiada

## Frontend

Requiere Node.js React

Setup: npm install npm install axios react-router-dom

Levantar el Frontend: npm run dev

Acceso: http://localhost:5173

Patrones de Diseño usados:
    MVVM
    

NOTAS: Debe estar corriendo el Backend para que el Frontend tenga cualquier forma de utilidad.