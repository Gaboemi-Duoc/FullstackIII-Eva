package com.smartlogix.ms_restock.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.smartlogix.ms_restock.dto.CreateRestockRequest;
import com.smartlogix.ms_restock.dto.UpdateEstadoRequest;
import com.smartlogix.ms_restock.model.RestockRequest;
import com.smartlogix.ms_restock.service.RestockService;

@ExtendWith(MockitoExtension.class)
class RestockControllerTest {

    @Mock
    private RestockService restockService;

    @InjectMocks
    private RestockController restockController;

    private RestockRequest solicitudMock;

    @BeforeEach
    void setUp() {
        solicitudMock = new RestockRequest();
        solicitudMock.setIdRestock(1L);
        solicitudMock.setIdItem(10L);
        solicitudMock.setNombreItem("Caja A");
        solicitudMock.setBodega("Bodega Central");
        solicitudMock.setCantidadSolicitada(50);
        solicitudMock.setEstado("PENDIENTE");
    }

    @Test
    @DisplayName("GET /api/restock - retorna lista de solicitudes")
    void listarSolicitudes_retornaLista() {
        when(restockService.listarSolicitudes()).thenReturn(Arrays.asList(solicitudMock));

        ResponseEntity<List<RestockRequest>> response = restockController.listarSolicitudes();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("GET /api/restock/{id} - retorna solicitud existente")
    void obtenerSolicitud_existente_retorna200() {
        when(restockService.obtenerPorId(1L)).thenReturn(solicitudMock);

        ResponseEntity<RestockRequest> response = restockController.obtenerSolicitud(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Caja A", response.getBody().getNombreItem());
    }

    @Test
    @DisplayName("GET /api/restock/estado - retorna solicitudes por estado")
    void listarPorEstado_retornaLista() {
        when(restockService.listarPorEstado("PENDIENTE")).thenReturn(Arrays.asList(solicitudMock));

        ResponseEntity<List<RestockRequest>> response = restockController.listarPorEstado("PENDIENTE");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("GET /api/restock/item/{id_item} - retorna solicitudes por item")
    void listarPorItem_retornaLista() {
        when(restockService.listarPorItem(10L)).thenReturn(Arrays.asList(solicitudMock));

        ResponseEntity<List<RestockRequest>> response = restockController.listarPorItem(10L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("GET /api/restock/bodega - retorna solicitudes por bodega")
    void listarPorBodega_retornaLista() {
        when(restockService.listarPorBodega("Bodega Central")).thenReturn(Arrays.asList(solicitudMock));

        ResponseEntity<List<RestockRequest>> response = restockController.listarPorBodega("Bodega Central");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("GET /api/restock/bodega/pendientes - retorna pendientes por bodega")
    void pendientesPorBodega_retornaLista() {
        when(restockService.pendientesPorBodega("Bodega Central")).thenReturn(Arrays.asList(solicitudMock));

        ResponseEntity<List<RestockRequest>> response = restockController.pendientesPorBodega("Bodega Central");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("GET /api/restock/resumen - retorna resumen por estado")
    void resumenPorEstado_retornaMapa() {
        Map<String, Long> resumen = new HashMap<>();
        resumen.put("PENDIENTE", 3L);
        when(restockService.resumenPorEstado()).thenReturn(resumen);

        ResponseEntity<Map<String, Long>> response = restockController.resumenPorEstado();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(3L, response.getBody().get("PENDIENTE"));
    }

    @Test
    @DisplayName("POST /api/restock - crea solicitud correctamente")
    void crearSolicitud_retorna200() {
        CreateRestockRequest dto = new CreateRestockRequest();
        dto.setIdItem(10L);
        dto.setNombreItem("Caja A");
        dto.setBodega("Bodega Central");
        dto.setCantidadSolicitada(50);

        when(restockService.crearSolicitud(any(RestockRequest.class))).thenReturn(solicitudMock);

        ResponseEntity<RestockRequest> response = restockController.crearSolicitud(dto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("PENDIENTE", response.getBody().getEstado());
    }

    @Test
    @DisplayName("PUT /api/restock/{id}/estado - actualiza estado correctamente")
    void actualizarEstado_retorna200() {
        UpdateEstadoRequest dto = new UpdateEstadoRequest();
        dto.setEstado("APROBADA");

        solicitudMock.setEstado("APROBADA");
        when(restockService.actualizarEstado(1L, "APROBADA")).thenReturn(solicitudMock);

        ResponseEntity<RestockRequest> response = restockController.actualizarEstado(1L, dto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("APROBADA", response.getBody().getEstado());
    }

    @Test
    @DisplayName("DELETE /api/restock/{id} - elimina solicitud correctamente")
    void eliminarSolicitud_retorna204() {
        doNothing().when(restockService).eliminarSolicitud(1L);

        ResponseEntity<Void> response = restockController.eliminarSolicitud(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(restockService, times(1)).eliminarSolicitud(1L);
    }
}
