package com.smartlogix.bff.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;

import com.smartlogix.bff.dto.DtoApiResponse;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/bff/test");
    }

    @Test
    @DisplayName("handleGenericException: retorna 500")
    void handleGenericException_retorna500() {
        Exception ex = new Exception("error genérico");

        ResponseEntity<DtoApiResponse<Void>> response = handler.handleGenericException(ex, webRequest);

        assertEquals(500, response.getStatusCode().value());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    @DisplayName("handleHttpClientError: retorna 404 cuando el recurso no existe")
    void handleHttpClientError_notFound_retorna404() {
        HttpClientErrorException ex = HttpClientErrorException.create(
            HttpStatus.NOT_FOUND, "Not Found", null, null, null);

        ResponseEntity<DtoApiResponse<Void>> response = handler.handleHttpClientError(ex);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @DisplayName("handleHttpClientError: retorna 400 en bad request")
    void handleHttpClientError_badRequest_retorna400() {
        HttpClientErrorException ex = HttpClientErrorException.create(
            HttpStatus.BAD_REQUEST, "Bad Request", null, null, null);

        ResponseEntity<DtoApiResponse<Void>> response = handler.handleHttpClientError(ex);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    @DisplayName("handleHttpServerError: retorna 503")
    void handleHttpServerError_retorna503() {
        HttpServerErrorException ex = HttpServerErrorException.create(
            HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", null, null, null);

        ResponseEntity<DtoApiResponse<Void>> response = handler.handleHttpServerError(ex);

        assertEquals(503, response.getStatusCode().value());
        assertEquals("Downstream service unavailable", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleIllegalArgumentException: retorna 400 con el mensaje original")
    void handleIllegalArgumentException_retorna400() {
        IllegalArgumentException ex = new IllegalArgumentException("dato inválido");

        ResponseEntity<DtoApiResponse<Void>> response = handler.handleIllegalArgumentException(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("dato inválido", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleIllegalStateException: retorna 409")
    void handleIllegalStateException_retorna409() {
        IllegalStateException ex = new IllegalStateException("estado inválido");

        ResponseEntity<DtoApiResponse<Void>> response = handler.handleIllegalStateException(ex);

        assertEquals(409, response.getStatusCode().value());
    }
}