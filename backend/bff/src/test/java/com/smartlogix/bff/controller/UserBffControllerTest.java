package com.smartlogix.bff.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.smartlogix.bff.dto.DtoApiResponse;
import com.smartlogix.bff.dto.LoginRequest;
import com.smartlogix.bff.dto.LoginResponse;
import com.smartlogix.bff.dto.RegisterRequest;
import com.smartlogix.bff.dto.UpdateUsernameRequest;
import com.smartlogix.bff.model.User;
import com.smartlogix.bff.service.UserBffService;

@ExtendWith(MockitoExtension.class)
class UserBffControllerTest {

    @Mock
    private UserBffService userBffService;

    @InjectMocks
    private UserBffController userBffController;

    private User usuarioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new User();
        usuarioMock.setId_user(1L);
        usuarioMock.setUsername("admin");
        usuarioMock.setEmail("admin@smartlogix.com");
    }

    @Test
    @DisplayName("login: retorna 200 con token")
    void login_retorna200() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        LoginResponse loginResponse = new LoginResponse(1L, "admin", "admin@smartlogix.com", "fake.token", "Login successful");
        DtoApiResponse<LoginResponse> mockResponse = new DtoApiResponse<>(true, "Login successful", loginResponse, 200);
        when(userBffService.login(any(LoginRequest.class))).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<LoginResponse>> response = userBffController.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("fake.token", response.getBody().getData().getToken());
    }

    @Test
    @DisplayName("login: retorna 401 con credenciales incorrectas")
    void login_credencialesIncorrectas_retorna401() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("wrongpass");

        DtoApiResponse<LoginResponse> mockResponse = new DtoApiResponse<>(false, "Invalid credentials", null, 401);
        when(userBffService.login(any(LoginRequest.class))).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<LoginResponse>> response = userBffController.login(request);

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    @DisplayName("register: retorna 200 al registrar correctamente")
    void register_retorna200() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("nuevo");
        request.setEmail("nuevo@smartlogix.com");
        request.setPassword("pass123");

        when(userBffService.register(any(RegisterRequest.class))).thenReturn(usuarioMock);

        ResponseEntity<User> response = userBffController.register(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("admin", response.getBody().getUsername());
    }

    @Test
    @DisplayName("updateUsername: retorna 200 al actualizar correctamente")
    void updateUsername_retorna200() {
        UpdateUsernameRequest request = new UpdateUsernameRequest();
        request.setUsername("nuevo_admin");

        DtoApiResponse<User> mockResponse = new DtoApiResponse<>(true, "ok", usuarioMock, 200);
        when(userBffService.updateUsername(eq(1L), any(UpdateUsernameRequest.class))).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<User>> response = userBffController.updateUsername(1L, request);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("getUser: retorna 200 con detalles del usuario")
    void getUser_retorna200() {
        DtoApiResponse<User> mockResponse = new DtoApiResponse<>(true, "ok", usuarioMock, 200);
        when(userBffService.getUserDetails(1L)).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<User>> response = userBffController.getUser(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("admin", response.getBody().getData().getUsername());
    }

    @Test
    @DisplayName("getUser: retorna 404 si no existe")
    void getUser_noExistente_retorna404() {
        DtoApiResponse<User> mockResponse = new DtoApiResponse<>(false, "User not found", null, 404);
        when(userBffService.getUserDetails(99L)).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<User>> response = userBffController.getUser(99L);

        assertEquals(404, response.getStatusCode().value());
    }
}