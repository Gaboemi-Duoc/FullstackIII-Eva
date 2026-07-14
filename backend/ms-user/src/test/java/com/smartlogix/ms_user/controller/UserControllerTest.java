package com.smartlogix.ms_user.controller;

import com.smartlogix.ms_user.dto.LoginRequest;
import com.smartlogix.ms_user.dto.RegisterUserRequest;
import com.smartlogix.ms_user.dto.UpdateUsernameRequest;
import com.smartlogix.ms_user.model.User;
import com.smartlogix.ms_user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User usuarioMock;
    private String tokenMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new User();
        usuarioMock.setId_user(1L);
        usuarioMock.setUsername("admin");
        usuarioMock.setEmail("admin@smartlogix.com");
        usuarioMock.setPassword("admin123");
        
        tokenMock = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    }

    @Test
    @DisplayName("GET /api/users/{id} - retorna usuario existente")
    void obtenerUsuario_existente_retorna200() {
        when(userService.obtenerPorId(1L)).thenReturn(usuarioMock);

        ResponseEntity<User> response = userController.obtenerUsuario(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("admin", response.getBody().getUsername());
    }

    @Test
    @DisplayName("POST /api/users/register - registra usuario correctamente")
    void registrarUsuario_retorna200() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("admin");
        request.setEmail("admin@smartlogix.com");
        request.setPassword("admin123");

        when(userService.registrarUsuario(any(User.class))).thenReturn(usuarioMock);

        ResponseEntity<User> response = userController.registrarUsuario(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("admin@smartlogix.com", response.getBody().getEmail());
    }

    @Test
    @DisplayName("POST /api/users/login - login correcto retorna token y datos de usuario")
    void login_credencialesCorrectas_retorna200() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        when(userService.login("admin", "admin123")).thenReturn(usuarioMock);
        when(userService.generarToken(usuarioMock)).thenReturn(tokenMock);

        ResponseEntity<Map<String, Object>> response = userController.login(request);

        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        assertNotNull(data);
        assertEquals(1L, data.get("id_user"));
        assertEquals("admin", data.get("username"));
        assertEquals(tokenMock, data.get("token"));
    }

    @Test
    @DisplayName("POST /api/users/login - verifica estructura de respuesta")
    void login_verificaEstructuraRespuesta() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        when(userService.login(anyString(), anyString())).thenReturn(usuarioMock);
        when(userService.generarToken(any(User.class))).thenReturn(tokenMock);

        ResponseEntity<Map<String, Object>> response = userController.login(request);
        Map<String, Object> body = response.getBody();

        // Verificar que la respuesta contiene los campos esperados
        assertTrue(body.containsKey("success"));
        assertTrue(body.containsKey("data"));
        
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        assertTrue(data.containsKey("id_user"));
        assertTrue(data.containsKey("username"));
        assertTrue(data.containsKey("token"));
    }

    @Test
    @DisplayName("PUT /api/users/{id}/username - actualiza username correctamente")
    void actualizarUsername_retorna200() {
        UpdateUsernameRequest request = new UpdateUsernameRequest();
        request.setUsername("nuevo_admin");

        usuarioMock.setUsername("nuevo_admin");
        when(userService.actualizarUsername(1L, "nuevo_admin")).thenReturn(usuarioMock);

        ResponseEntity<User> response = userController.actualizarUsername(1L, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("nuevo_admin", response.getBody().getUsername());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - elimina usuario correctamente")
    void eliminarUsuario_retorna204() {
        doNothing().when(userService).eliminarUsuario(1L);

        ResponseEntity<Void> response = userController.eliminarUsuario(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(userService, times(1)).eliminarUsuario(1L);
    }
}