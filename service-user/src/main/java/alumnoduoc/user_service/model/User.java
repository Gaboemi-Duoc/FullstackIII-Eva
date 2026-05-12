package alumnoduoc.user_service.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Table(name = "users")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_user;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    // @NotBlank(message = "Password is required")
    // @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    private String password;
}
