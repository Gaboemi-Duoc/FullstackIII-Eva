package alumnoduoc.bff.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id_user;
    private String username;
    private String email;
    private String password;
}