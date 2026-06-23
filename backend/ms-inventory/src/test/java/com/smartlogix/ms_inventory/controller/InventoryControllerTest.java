package com.smartlogix.ms_inventory.controller;

import com.smartlogix.ms_inventory.model.Item;
import com.smartlogix.ms_inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    private Item itemMock;

    @BeforeEach
    void setUp() {
        itemMock = new Item();
        itemMock.setId_item(1L);
        itemMock.setNombre("Caja A");
        itemMock.setCantidad(100);
    }

    @Test
    @DisplayName("GET /api/inventory/{id} - Retorna 200 cuando el item existe")
    void obtenerItem_retorna200() throws Exception {
        when(inventoryService.obtenerPorId(1L)).thenReturn(itemMock);

        mockMvc.perform(get("/api/inventory/1")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.nombre").value("Caja A")); // Verifica que el JSON devuelto es correcto
    }
}
