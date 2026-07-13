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
import static org.mockito.ArgumentMatchers.any;
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
import com.smartlogix.bff.model.Item;
import com.smartlogix.bff.service.InventoryBffService;

@ExtendWith(MockitoExtension.class)
class InventoryBffControllerTest {

    @Mock
    private InventoryBffService inventoryBffService;

    @InjectMocks
    private InventoryBffController inventoryBffController;

    private Item itemMock;

    @BeforeEach
    void setUp() {
        itemMock = new Item();
        itemMock.setId_item(1L);
        itemMock.setNombre("Caja A");
        itemMock.setCantidad(100);
        itemMock.setPrecio(1500.0);
        itemMock.setBodega("Bodega Central");
    }

    @Test
    @DisplayName("listarItems: retorna 200 con lista")
    void listarItems_retorna200() {
        DtoApiResponse<List<Item>> mockResponse = new DtoApiResponse<>(true, "ok", Arrays.asList(itemMock), 200);
        when(inventoryBffService.getAllItems()).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<List<Item>>> response = inventoryBffController.listarItems();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getData().size());
    }

    @Test
    @DisplayName("obtenerItem: retorna 200 con item existente")
    void obtenerItem_retorna200() {
        DtoApiResponse<Item> mockResponse = new DtoApiResponse<>(true, "ok", itemMock, 200);
        when(inventoryBffService.getItemById(1L)).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<Item>> response = inventoryBffController.obtenerItem(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Caja A", response.getBody().getData().getNombre());
    }

    @Test
    @DisplayName("stockBajo: retorna 200 con items filtrados")
    void stockBajo_retorna200() {
        DtoApiResponse<List<Item>> mockResponse = new DtoApiResponse<>(true, "ok", Arrays.asList(itemMock), 200);
        when(inventoryBffService.getLowStockItems(10)).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<List<Item>>> response = inventoryBffController.stockBajo(10);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("crearItem: retorna 200 al crear correctamente")
    void crearItem_retorna200() {
        DtoApiResponse<Item> mockResponse = new DtoApiResponse<>(true, "ok", itemMock, 200);
        when(inventoryBffService.createItem(any(Item.class))).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<Item>> response = inventoryBffController.crearItem(itemMock);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("actualizarCantidad: retorna 200 al actualizar")
    void actualizarCantidad_retorna200() {
        Map<String, Integer> datos = new HashMap<>();
        datos.put("cantidad", 200);
        DtoApiResponse<Item> mockResponse = new DtoApiResponse<>(true, "ok", itemMock, 200);
        when(inventoryBffService.updateItemQuantity(eq(1L), anyMap())).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<Item>> response = inventoryBffController.actualizarCantidad(1L, datos);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("actualizarPrecio: retorna 200 al actualizar")
    void actualizarPrecio_retorna200() {
        Map<String, Double> datos = new HashMap<>();
        datos.put("precio", 2000.0);
        DtoApiResponse<Item> mockResponse = new DtoApiResponse<>(true, "ok", itemMock, 200);
        when(inventoryBffService.updateItemPrice(eq(1L), anyMap())).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<Item>> response = inventoryBffController.actualizarPrecio(1L, datos);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("eliminarItem: retorna 204 al eliminar")
    void eliminarItem_retorna204() {
        DtoApiResponse<Void> mockResponse = new DtoApiResponse<>(true, "ok", null, 204);
        when(inventoryBffService.deleteItem(1L)).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<Void>> response = inventoryBffController.eliminarItem(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(inventoryBffService, times(1)).deleteItem(1L);
    }
}