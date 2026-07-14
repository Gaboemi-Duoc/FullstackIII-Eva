package com.smartlogix.ms_user.repository;

import com.smartlogix.ms_user.model.User;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para el acceso y persistencia de la entidad {@link User}.
 * <p>
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar y
 * define consultas derivadas adicionales.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario cuya combinación de username y password coincida exactamente.
     * Utilizado para validar credenciales en el login.
     *
     * @param username nombre de usuario
     * @param password contraseña asociada
     * @return un {@link Optional} con el usuario encontrado, o vacío si las credenciales no coinciden
     */
    Optional<User> findByUsernameAndPassword(String username, String password);

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email correo electrónico a buscar
     * @return un {@link Optional} con el usuario encontrado, o vacío si no existe
     */
    Optional<User> findByEmail(String email);
}