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

    @BeforeEach
    void setUp() {
        usuarioMock = new User();
        usuarioMock.setId_user(1L);
        usuarioMock.setUsername("admin");
        usuarioMock.setEmail("admin@smartlogix.com");
        usuarioMock.setPassword("admin123");
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
    @DisplayName("POST /api/users/login - login correcto retorna usuario")
    void login_credencialesCorrectas_retorna200() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        when(userService.login("admin", "admin123")).thenReturn(usuarioMock);

        ResponseEntity<User> response = userController.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("admin", response.getBody().getUsername());
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
