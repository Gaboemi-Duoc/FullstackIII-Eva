package com.smartlogix.ms_inventory.controller;

import java.util.Arrays;
import java.util.List;

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

import com.smartlogix.ms_inventory.dto.CreateItemRequest;
import com.smartlogix.ms_inventory.dto.UpdateCantidadRequest;
import com.smartlogix.ms_inventory.dto.UpdatePrecioRequest;
import com.smartlogix.ms_inventory.model.Item;
import com.smartlogix.ms_inventory.service.InventoryService;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    private Item itemMock;

    @BeforeEach
    void setUp() {
        itemMock = new Item();
        itemMock.setId_item(1L);
        itemMock.setNombre("Caja A");
        itemMock.setCantidad(100);
        itemMock.setPrecio(1500.0);
        itemMock.setBodega("Bodega Central");
        itemMock.setDescripcion("Descripcion de prueba");
    }

    @Test
    @DisplayName("GET /api/inventory/{id} - retorna item existente")
    void obtenerItem_existente_retorna200() {
        when(inventoryService.obtenerPorId(1L)).thenReturn(itemMock);

        ResponseEntity<Item> response = inventoryController.obtenerItem(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Caja A", response.getBody().getNombre());
    }

    @Test
    @DisplayName("GET /api/inventory/buscar - retorna item por nombre")
    void buscarPorNombre_retornaItem() {
        when(inventoryService.buscarPorNombre("Caja A")).thenReturn(itemMock);

        ResponseEntity<Item> response = inventoryController.buscarPorNombre("Caja A");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Caja A", response.getBody().getNombre());
    }

    @Test
    @DisplayName("GET /api/inventory/stock-bajo - retorna items con stock bajo")
    void stockBajo_retornaLista() {
        when(inventoryService.itemsConStockBajo(10)).thenReturn(Arrays.asList(itemMock));

        ResponseEntity<List<Item>> response = inventoryController.stockBajo(10);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("POST /api/inventory - crea item con DTO correctamente")
    void crearItem_conDto_retorna200() {
        CreateItemRequest request = new CreateItemRequest();
        request.setNombre("Caja A");
        request.setDescripcion("Descripcion de prueba");
        request.setCantidad(100);
        request.setPrecio(1500.0);
        request.setBodega("Bodega Central");

        when(inventoryService.crearItem(any(Item.class))).thenReturn(itemMock);

        ResponseEntity<Item> response = inventoryController.crearItem(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Caja A", response.getBody().getNombre());
    }

    @Test
    @DisplayName("PUT /api/inventory/{id}/cantidad - actualiza cantidad")
    void actualizarCantidad_retorna200() {
        UpdateCantidadRequest request = new UpdateCantidadRequest();
        request.setCantidad(200);

        when(inventoryService.actualizarCantidad(1L, 200)).thenReturn(itemMock);

        ResponseEntity<Item> response = inventoryController.actualizarCantidad(1L, request);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("PUT /api/inventory/{id}/precio - actualiza precio")
    void actualizarPrecio_retorna200() {
        UpdatePrecioRequest request = new UpdatePrecioRequest();
        request.setPrecio(2000.0);

        when(inventoryService.actualizarPrecio(1L, 2000.0)).thenReturn(itemMock);

        ResponseEntity<Item> response = inventoryController.actualizarPrecio(1L, request);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("GET /api/inventory/bodega - retorna items por bodega")
    void itemsPorBodega_retornaLista() {
        when(inventoryService.obtenerItemsPorBodega("Bodega Central"))
                .thenReturn(Arrays.asList(itemMock));

        ResponseEntity<List<Item>> response = inventoryController.itemsPorBodega("Bodega Central");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("DELETE /api/inventory/{id} - elimina item correctamente")
    void eliminarItem_retorna204() {
        doNothing().when(inventoryService).eliminarItem(1L);

        ResponseEntity<Void> response = inventoryController.eliminarItem(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(inventoryService, times(1)).eliminarItem(1L);
    }
}