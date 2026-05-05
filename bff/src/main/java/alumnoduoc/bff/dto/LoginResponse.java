package alumnoduoc.bff.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private Long id_user;
    private String username;
    private String email;
    private String token; // JWT or session token
    private String message;
}