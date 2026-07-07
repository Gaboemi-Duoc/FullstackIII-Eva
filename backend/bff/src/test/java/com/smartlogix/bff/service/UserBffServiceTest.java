package com.smartlogix.bff.service;

import com.smartlogix.bff.client.UserServiceClient;
import com.smartlogix.bff.dto.DtoApiResponse;
import com.smartlogix.bff.dto.LoginRequest;
import com.smartlogix.bff.dto.LoginResponse;
import com.smartlogix.bff.dto.RegisterRequest;
import com.smartlogix.bff.dto.UpdateUsernameRequest;
import com.smartlogix.bff.model.User;
import com.smartlogix.bff.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserBffServiceTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserBffService userBffService;

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
    @DisplayName("login: credenciales correctas retorna token")
    void login_credencialesCorrectas_retornaToken() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        when(userServiceClient.login(any(LoginRequest.class))).thenReturn(usuarioMock);
        when(jwtService.generateToken("admin")).thenReturn("fake.jwt.token");

        DtoApiResponse<LoginResponse> response = userBffService.login(request);

        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatusCode());
        assertEquals("fake.jwt.token", response.getData().getToken());
    }

    @Test
    @DisplayName("login: credenciales incorrectas retorna 401")
    void login_credencialesIncorrectas_retorna401() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("wrongpass");

        when(userServiceClient.login(any(LoginRequest.class)))
            .thenThrow(new RuntimeException("Credenciales incorrectas"));

        DtoApiResponse<LoginResponse> response = userBffService.login(request);

        assertFalse(response.isSuccess());
        assertEquals(401, response.getStatusCode());
    }

    @Test
    @DisplayName("register: registra usuario correctamente")
    void register_registraCorrectamente() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("nuevo");
        request.setEmail("nuevo@smartlogix.com");
        request.setPassword("pass123");

        when(userServiceClient.register(any(RegisterRequest.class))).thenReturn(usuarioMock);

        User resultado = userBffService.register(request);

        assertNotNull(resultado);
        assertEquals("admin", resultado.getUsername());
    }

    @Test
    @DisplayName("updateUsername: actualiza correctamente")
    void updateUsername_actualizaCorrectamente() {
        UpdateUsernameRequest request = new UpdateUsernameRequest();
        request.setUsername("nuevo_admin");

        usuarioMock.setUsername("nuevo_admin");
        when(userServiceClient.updateUsername(eq(1L), any(UpdateUsernameRequest.class)))
            .thenReturn(usuarioMock);

        DtoApiResponse<User> response = userBffService.updateUsername(1L, request);

        assertTrue(response.isSuccess());
        assertEquals("nuevo_admin", response.getData().getUsername());
    }

    @Test
    @DisplayName("getUserDetails: elimina el password de la respuesta")
    void getUserDetails_eliminaPassword() {
        when(userServiceClient.getUserById(1L)).thenReturn(usuarioMock);

        DtoApiResponse<User> response = userBffService.getUserDetails(1L);

        assertTrue(response.isSuccess());
        assertNull(response.getData().getPassword());
    }

    @Test
    @DisplayName("getUserDetails: retorna 404 si no existe")
    void getUserDetails_noExistente_retorna404() {
        when(userServiceClient.getUserById(99L)).thenThrow(new RuntimeException("no encontrado"));

        DtoApiResponse<User> response = userBffService.getUserDetails(99L);

        assertFalse(response.isSuccess());
        assertEquals(404, response.getStatusCode());
    }
}