package com.smartlogix.bff.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.smartlogix.bff.dto.DtoApiResponse;
import com.smartlogix.bff.model.RestockRequest;
import com.smartlogix.bff.service.RestockBffService;

@ExtendWith(MockitoExtension.class)
class RestockBffControllerTest {

    @Mock
    private RestockBffService restockBffService;

    @InjectMocks
    private RestockBffController restockBffController;

    private RestockRequest solicitudMock;

    @BeforeEach
    void setUp() {
        solicitudMock = new RestockRequest();
        solicitudMock.setIdRestock(1L);
        solicitudMock.setIdItem(5L);
        solicitudMock.setNombreItem("Caja A");
        solicitudMock.setBodega("Bodega Central");
        solicitudMock.setCantidadSolicitada(20);
        solicitudMock.setEstado("PENDIENTE");
    }

    @Test
    @DisplayName("listarSolicitudes: retorna 200 con lista")
    void listarSolicitudes_retorna200() {
        DtoApiResponse<List<RestockRequest>> mockResponse =
            new DtoApiResponse<>(true, "ok", Arrays.asList(solicitudMock), 200);
        when(restockBffService.getAllRestockRequests()).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<List<RestockRequest>>> response =
            restockBffController.listarSolicitudes();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getData().size());
    }

    @Test
    @DisplayName("obtenerSolicitud: retorna 200 con solicitud existente")
    void obtenerSolicitud_retorna200() {
        DtoApiResponse<RestockRequest> mockResponse =
            new DtoApiResponse<>(true, "ok", solicitudMock, 200);
        when(restockBffService.getRestockRequestById(1L)).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<RestockRequest>> response =
            restockBffController.obtenerSolicitud(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Caja A", response.getBody().getData().getNombreItem());
    }

    @Test
    @DisplayName("listarPorEstado: retorna 200 con lista filtrada")
    void listarPorEstado_retorna200() {
        DtoApiResponse<List<RestockRequest>> mockResponse =
            new DtoApiResponse<>(true, "ok", Arrays.asList(solicitudMock), 200);
        when(restockBffService.getRestockRequestsByStatus("PENDIENTE")).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<List<RestockRequest>>> response =
            restockBffController.listarPorEstado("PENDIENTE");

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("listarPorItem: retorna 200 con historial del item")
    void listarPorItem_retorna200() {
        DtoApiResponse<List<RestockRequest>> mockResponse =
            new DtoApiResponse<>(true, "ok", Arrays.asList(solicitudMock), 200);
        when(restockBffService.getRestockRequestsByItem(5L)).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<List<RestockRequest>>> response =
            restockBffController.listarPorItem(5L);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("listarPorBodega: retorna 200 con lista por bodega")
    void listarPorBodega_retorna200() {
        DtoApiResponse<List<RestockRequest>> mockResponse =
            new DtoApiResponse<>(true, "ok", Arrays.asList(solicitudMock), 200);
        when(restockBffService.getRestockRequestsByWarehouse("Bodega Central")).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<List<RestockRequest>>> response =
            restockBffController.listarPorBodega("Bodega Central");

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("pendientesPorBodega: retorna 200 con pendientes")
    void pendientesPorBodega_retorna200() {
        DtoApiResponse<List<RestockRequest>> mockResponse =
            new DtoApiResponse<>(true, "ok", Arrays.asList(solicitudMock), 200);
        when(restockBffService.getPendingRequestsByWarehouse("Bodega Norte")).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<List<RestockRequest>>> response =
            restockBffController.pendientesPorBodega("Bodega Norte");

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("resumenPorEstado: retorna 200 con resumen")
    void resumenPorEstado_retorna200() {
        Map<String, Long> resumen = new HashMap<>();
        resumen.put("PENDIENTE", 3L);
        DtoApiResponse<Map<String, Long>> mockResponse =
            new DtoApiResponse<>(true, "ok", resumen, 200);
        when(restockBffService.getRestockSummary()).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<Map<String, Long>>> response =
            restockBffController.resumenPorEstado();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(3L, response.getBody().getData().get("PENDIENTE"));
    }

    @Test
    @DisplayName("crearSolicitud: retorna 200 al crear correctamente")
    void crearSolicitud_retorna200() {
        Map<String, Object> datos = new HashMap<>();
        datos.put("idItem", 5L);
        DtoApiResponse<RestockRequest> mockResponse =
            new DtoApiResponse<>(true, "ok", solicitudMock, 200);
        when(restockBffService.createRestockRequest(anyMap())).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<RestockRequest>> response =
            restockBffController.crearSolicitud(datos);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("actualizarEstado: retorna 200 al actualizar correctamente")
    void actualizarEstado_retorna200() {
        Map<String, String> datos = new HashMap<>();
        datos.put("estado", "APROBADA");
        DtoApiResponse<RestockRequest> mockResponse =
            new DtoApiResponse<>(true, "ok", solicitudMock, 200);
        when(restockBffService.updateRestockStatus(eq(1L), anyMap())).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<RestockRequest>> response =
            restockBffController.actualizarEstado(1L, datos);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("eliminarSolicitud: retorna 204 al eliminar correctamente")
    void eliminarSolicitud_retorna204() {
        DtoApiResponse<Void> mockResponse = new DtoApiResponse<>(true, "ok", null, 204);
        when(restockBffService.deleteRestockRequest(1L)).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<Void>> response =
            restockBffController.eliminarSolicitud(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(restockBffService, times(1)).deleteRestockRequest(1L);
    }
}