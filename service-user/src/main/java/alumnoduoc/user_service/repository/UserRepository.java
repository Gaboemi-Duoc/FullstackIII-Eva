package alumnoduoc.user_service.repository;

import java.util.Optional;

import alumnoduoc.user_service.model.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameAndPassword(String username, String password);

    Optional<User> findByEmail(String email);
}