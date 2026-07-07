package com.smartlogix.bff.service;

import com.smartlogix.bff.client.InventoryServiceClient;
import com.smartlogix.bff.client.RestockServiceClient;
import com.smartlogix.bff.dto.DtoApiResponse;
import com.smartlogix.bff.model.Item;
import com.smartlogix.bff.model.RestockRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestockBffServiceTest {

    @Mock
    private RestockServiceClient restockServiceClient;

    @Mock
    private InventoryServiceClient inventoryServiceClient;

    @InjectMocks
    private RestockBffService restockBffService;

    private RestockRequest solicitudMock;
    private Item itemMock;

    @BeforeEach
    void setUp() {
        solicitudMock = new RestockRequest();
        solicitudMock.setIdRestock(1L);
        solicitudMock.setIdItem(5L);
        solicitudMock.setNombreItem("Caja A");
        solicitudMock.setBodega("Bodega Central");
        solicitudMock.setCantidadSolicitada(20);
        solicitudMock.setEstado("PENDIENTE");

        itemMock = new Item();
        itemMock.setId_item(5L);
        itemMock.setNombre("Caja A");
        itemMock.setCantidad(50);
        itemMock.setPrecio(1500.0);
        itemMock.setBodega("Bodega Central");
    }

    @Test
    @DisplayName("getAllRestockRequests: retorna lista exitosa")
    void getAllRestockRequests_retornaListaExitosa() {
        when(restockServiceClient.listarSolicitudes()).thenReturn(Arrays.asList(solicitudMock));

        DtoApiResponse<List<RestockRequest>> response = restockBffService.getAllRestockRequests();

        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatusCode());
        assertEquals(1, response.getData().size());
    }

    @Test
    @DisplayName("getAllRestockRequests: maneja error y retorna 500")
    void getAllRestockRequests_errorRetorna500() {
        when(restockServiceClient.listarSolicitudes()).thenThrow(new RuntimeException("conexión fallida"));

        DtoApiResponse<List<RestockRequest>> response = restockBffService.getAllRestockRequests();

        assertFalse(response.isSuccess());
        assertEquals(500, response.getStatusCode());
    }

    @Test
    @DisplayName("getRestockRequestById: retorna solicitud existente")
    void getRestockRequestById_existente_retorna200() {
        when(restockServiceClient.obtenerPorId(1L)).thenReturn(solicitudMock);

        DtoApiResponse<RestockRequest> response = restockBffService.getRestockRequestById(1L);

        assertTrue(response.isSuccess());
        assertEquals("Caja A", response.getData().getNombreItem());
    }

    @Test
    @DisplayName("getRestockRequestById: retorna 404 si no existe")
    void getRestockRequestById_noExistente_retorna404() {
        when(restockServiceClient.obtenerPorId(99L)).thenThrow(new RuntimeException("no encontrado"));

        DtoApiResponse<RestockRequest> response = restockBffService.getRestockRequestById(99L);

        assertFalse(response.isSuccess());
        assertEquals(404, response.getStatusCode());
    }

    @Test
    @DisplayName("createRestockRequest: crea solicitud correctamente")
    void createRestockRequest_creaCorrectamente() {
        Map<String, Object> datos = new HashMap<>();
        datos.put("idItem", 5L);
        datos.put("nombreItem", "Caja A");
        datos.put("bodega", "Bodega Central");
        datos.put("cantidadSolicitada", 20);

        when(restockServiceClient.crearSolicitud(anyMap())).thenReturn(solicitudMock);

        DtoApiResponse<RestockRequest> response = restockBffService.createRestockRequest(datos);

        assertTrue(response.isSuccess());
        assertEquals("PENDIENTE", response.getData().getEstado());
    }

    @Test
    @DisplayName("updateRestockStatus: al COMPLETAR actualiza el stock en inventory")
    void updateRestockStatus_completada_actualizaInventory() {
        Map<String, String> statusData = new HashMap<>();
        statusData.put("estado", "COMPLETADA");

        RestockRequest actualizada = new RestockRequest();
        actualizada.setIdRestock(1L);
        actualizada.setIdItem(5L);
        actualizada.setEstado("COMPLETADA");
        actualizada.setCantidadSolicitada(20);

        when(restockServiceClient.obtenerPorId(1L)).thenReturn(solicitudMock);
        when(restockServiceClient.actualizarEstado(eq(1L), anyMap())).thenReturn(actualizada);
        when(inventoryServiceClient.getItemById(5L)).thenReturn(itemMock);
        when(inventoryServiceClient.actualizarCantidad(eq(5L), anyMap())).thenReturn(itemMock);

        DtoApiResponse<RestockRequest> response = restockBffService.updateRestockStatus(1L, statusData);

        assertTrue(response.isSuccess());
        assertEquals("COMPLETADA", response.getData().getEstado());
        // verifica que SÍ se llamó a actualizar inventory
        verify(inventoryServiceClient, times(1)).actualizarCantidad(eq(5L), anyMap());
    }

    @Test
    @DisplayName("updateRestockStatus: si NO es COMPLETADA no toca inventory")
    void updateRestockStatus_pendiente_noTocaInventory() {
        Map<String, String> statusData = new HashMap<>();
        statusData.put("estado", "APROBADA");

        RestockRequest actualizada = new RestockRequest();
        actualizada.setIdRestock(1L);
        actualizada.setEstado("APROBADA");

        when(restockServiceClient.obtenerPorId(1L)).thenReturn(solicitudMock);
        when(restockServiceClient.actualizarEstado(eq(1L), anyMap())).thenReturn(actualizada);

        DtoApiResponse<RestockRequest> response = restockBffService.updateRestockStatus(1L, statusData);

        assertTrue(response.isSuccess());
        // verifica que NO se llamó a inventory porque el estado no es COMPLETADA
        verify(inventoryServiceClient, never()).actualizarCantidad(anyLong(), anyMap());
    }

    @Test
    @DisplayName("deleteRestockRequest: elimina correctamente")
    void deleteRestockRequest_eliminaCorrectamente() {
        doNothing().when(restockServiceClient).eliminarSolicitud(1L);

        DtoApiResponse<Void> response = restockBffService.deleteRestockRequest(1L);

        assertTrue(response.isSuccess());
        assertEquals(204, response.getStatusCode());
        verify(restockServiceClient, times(1)).eliminarSolicitud(1L);
    }

    @Test
    @DisplayName("getRestockSummary: retorna resumen por estado")
    void getRestockSummary_retornaResumen() {
        Map<String, Long> resumen = new HashMap<>();
        resumen.put("PENDIENTE", 3L);
        when(restockServiceClient.resumenPorEstado()).thenReturn(resumen);

        DtoApiResponse<Map<String, Long>> response = restockBffService.getRestockSummary();

        assertTrue(response.isSuccess());
        assertEquals(3L, response.getData().get("PENDIENTE"));
    }
}