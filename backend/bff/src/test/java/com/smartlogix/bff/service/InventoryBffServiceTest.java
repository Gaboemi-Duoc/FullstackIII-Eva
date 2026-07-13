package com.smartlogix.bff.service;

import com.smartlogix.bff.client.InventoryServiceClient;
import com.smartlogix.bff.dto.DtoApiResponse;
import com.smartlogix.bff.model.Item;
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
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryBffServiceTest {

    @Mock
    private InventoryServiceClient inventoryServiceClient;

    @InjectMocks
    private InventoryBffService inventoryBffService;

    private Item itemMock;

    @BeforeEach
    void setUp() {
        itemMock = new Item();
        itemMock.setId_item(1L);
        itemMock.setNombre("Caja A");
        itemMock.setDescripcion("Caja de prueba");
        itemMock.setCantidad(100);
        itemMock.setPrecio(1500.0);
        itemMock.setBodega("Bodega Central");
    }

    @Test
    @DisplayName("getAllItems: retorna lista exitosa")
    void getAllItems_retornaListaExitosa() {
        when(inventoryServiceClient.getAllItems()).thenReturn(Arrays.asList(itemMock));

        DtoApiResponse<List<Item>> response = inventoryBffService.getAllItems();

        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatusCode());
        assertEquals(1, response.getData().size());
    }

    @Test
    @DisplayName("getAllItems: maneja error y retorna 500")
    void getAllItems_errorRetorna500() {
        when(inventoryServiceClient.getAllItems()).thenThrow(new RuntimeException("conexión fallida"));

        DtoApiResponse<List<Item>> response = inventoryBffService.getAllItems();

        assertFalse(response.isSuccess());
        assertEquals(500, response.getStatusCode());
    }

    @Test
    @DisplayName("getItemById: retorna item existente")
    void getItemById_existente_retorna200() {
        when(inventoryServiceClient.getItemById(1L)).thenReturn(itemMock);

        DtoApiResponse<Item> response = inventoryBffService.getItemById(1L);

        assertTrue(response.isSuccess());
        assertEquals("Caja A", response.getData().getNombre());
    }

    @Test
    @DisplayName("getItemById: retorna 404 si no existe")
    void getItemById_noExistente_retorna404() {
        when(inventoryServiceClient.getItemById(99L)).thenThrow(new RuntimeException("no encontrado"));

        DtoApiResponse<Item> response = inventoryBffService.getItemById(99L);

        assertFalse(response.isSuccess());
        assertEquals(404, response.getStatusCode());
    }

    @Test
    @DisplayName("getLowStockItems: retorna items con stock bajo")
    void getLowStockItems_retornaListaFiltrada() {
        when(inventoryServiceClient.getStockBajo(10)).thenReturn(Arrays.asList(itemMock));

        DtoApiResponse<List<Item>> response = inventoryBffService.getLowStockItems(10);

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
    }

    @Test
    @DisplayName("createItem: crea item correctamente")
    void createItem_creaCorrectamente() {
        when(inventoryServiceClient.crearItem(any(Item.class))).thenReturn(itemMock);

        DtoApiResponse<Item> response = inventoryBffService.createItem(itemMock);

        assertTrue(response.isSuccess());
        assertEquals("Caja A", response.getData().getNombre());
    }

    @Test
    @DisplayName("createItem: retorna 409 si el item ya existe")
    void createItem_duplicado_retorna409() {
        when(inventoryServiceClient.crearItem(any(Item.class)))
            .thenThrow(new RuntimeException("Item already exists"));

        DtoApiResponse<Item> response = inventoryBffService.createItem(itemMock);

        assertFalse(response.isSuccess());
        assertEquals(409, response.getStatusCode());
    }

    @Test
    @DisplayName("updateItemQuantity: actualiza cantidad correctamente")
    void updateItemQuantity_actualizaCorrectamente() {
        Map<String, Integer> datos = new HashMap<>();
        datos.put("cantidad", 200);

        Item actualizado = new Item();
        actualizado.setId_item(1L);
        actualizado.setCantidad(200);

        when(inventoryServiceClient.actualizarCantidad(eq(1L), anyMap())).thenReturn(actualizado);

        DtoApiResponse<Item> response = inventoryBffService.updateItemQuantity(1L, datos);

        assertTrue(response.isSuccess());
        assertEquals(200, response.getData().getCantidad());
    }

    @Test
    @DisplayName("updateItemPrice: actualiza precio correctamente")
    void updateItemPrice_actualizaCorrectamente() {
        Map<String, Double> datos = new HashMap<>();
        datos.put("precio", 2000.0);

        Item actualizado = new Item();
        actualizado.setId_item(1L);
        actualizado.setPrecio(2000.0);

        when(inventoryServiceClient.actualizarPrecio(eq(1L), anyMap())).thenReturn(actualizado);

        DtoApiResponse<Item> response = inventoryBffService.updateItemPrice(1L, datos);

        assertTrue(response.isSuccess());
        assertEquals(2000.0, response.getData().getPrecio());
    }

    @Test
    @DisplayName("deleteItem: elimina correctamente")
    void deleteItem_eliminaCorrectamente() {
        doNothing().when(inventoryServiceClient).eliminarItem(1L);

        DtoApiResponse<Void> response = inventoryBffService.deleteItem(1L);

        assertTrue(response.isSuccess());
        assertEquals(204, response.getStatusCode());
        verify(inventoryServiceClient, times(1)).eliminarItem(1L);
    }
}