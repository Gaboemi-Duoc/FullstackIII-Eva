package com.smartlogix.ms_inventory.controller;

import com.smartlogix.ms_inventory.model.Item;
import com.smartlogix.ms_inventory.service.InventoryService;
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

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @Test
    @DisplayName("GET /api/inventory/{id} - Retorna 200 cuando el item existe")
    void obtenerItem_retorna200() throws Exception {
        Item item = new Item();
        item.setId(1L);
        item.setNombre("Producto Prueba");

        when(inventoryService.obtenerPorId(1L)).thenReturn(item);

        mockMvc.perform(get("/api/inventory/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
