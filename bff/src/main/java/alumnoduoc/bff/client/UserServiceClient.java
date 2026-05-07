package alumnoduoc.bff.client;

import alumnoduoc.bff.dto.LoginRequest;
import alumnoduoc.bff.dto.UpdateUsernameRequest;
import alumnoduoc.bff.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service", url = "http://localhost:9090") 
public interface UserServiceClient {
    
    @GetMapping("/api/users")
    List<User> getAllUsers();
    
    @GetMapping("/api/users/{id}")
    User getUserById(@PathVariable("id") Long id);
    
    @PostMapping("/api/users/register")
    User registerUser(@RequestBody User user);
    
    @PostMapping("/api/users/login")
    User login(@RequestBody LoginRequest loginRequest);
    
    @PutMapping("/api/users/{id}/username")
    User updateUsername(@PathVariable("id") Long id, 
                        @RequestBody UpdateUsernameRequest request);
    
    @DeleteMapping("/api/users/{id}")
    void deleteUser(@PathVariable("id") Long id);
}