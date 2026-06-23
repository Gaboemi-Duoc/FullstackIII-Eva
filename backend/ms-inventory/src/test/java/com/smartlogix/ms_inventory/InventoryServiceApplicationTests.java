package com.smartlogix.ms_inventory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.smartlogix.ms_inventory.model.Item;
import com.smartlogix.ms_inventory.repository.InventoryRepository;
import com.smartlogix.ms_inventory.service.InventoryService;

@ExtendWith(MockitoExtension.class)
class InventoryServiceApplicationTests {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

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
    @DisplayName("listarItems: retorna lista de items")
    void listarItems_retornaLista() {
        when(inventoryRepository.findAll()).thenReturn(Arrays.asList(itemMock));

        List<Item> resultado = inventoryService.listarItems();

        assertEquals(1, resultado.size());
        assertEquals("Caja A", resultado.get(0).getNombre());
        verify(inventoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerPorId: retorna item cuando existe")
    void obtenerPorId_existente_retornaItem() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(itemMock));

        Item resultado = inventoryService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId_item());
    }

    @Test
    @DisplayName("obtenerPorId: lanza excepcion cuando no existe")
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(inventoryRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> inventoryService.obtenerPorId(99L));

        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    @DisplayName("crearItem: guarda y retorna el item")
    void crearItem_guardaCorrectamente() {
        when(inventoryRepository.save(any(Item.class))).thenReturn(itemMock);

        Item resultado = inventoryService.crearItem(itemMock);

        assertNotNull(resultado);
        assertEquals("Caja A", resultado.getNombre());
        verify(inventoryRepository, times(1)).save(any(Item.class));
    }

    @Test
    @DisplayName("actualizarCantidad: actualiza cantidad correctamente")
    void actualizarCantidad_actualizaCorrectamente() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(itemMock));
        when(inventoryRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        Item resultado = inventoryService.actualizarCantidad(1L, 200);

        assertEquals(200, resultado.getCantidad());
        verify(inventoryRepository, times(1)).save(any(Item.class));
    }

    @Test
    @DisplayName("actualizarPrecio: actualiza precio correctamente")
    void actualizarPrecio_actualizaCorrectamente() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(itemMock));
        when(inventoryRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        Item resultado = inventoryService.actualizarPrecio(1L, 2000.0);

        assertEquals(2000.0, resultado.getPrecio());
    }

    @Test
    @DisplayName("itemsConStockBajo: retorna items con cantidad menor al umbral")
    void itemsConStockBajo_retornaItemsFiltrados() {
        Item itemBajoStock = new Item();
        itemBajoStock.setId_item(2L);
        itemBajoStock.setNombre("Caja B");
        itemBajoStock.setCantidad(5);
        itemBajoStock.setPrecio(500.0);
        itemBajoStock.setBodega("Bodega Norte");
        itemBajoStock.setDescripcion("Stock bajo");

        when(inventoryRepository.findByCantidadLessThan(10))
                .thenReturn(Arrays.asList(itemBajoStock));

        List<Item> resultado = inventoryService.itemsConStockBajo(10);

        assertEquals(1, resultado.size());
        assertEquals(5, resultado.get(0).getCantidad());
    }

    @Test
    @DisplayName("eliminarItem: llama deleteById correctamente")
    void eliminarItem_eliminaCorrectamente() {
        doNothing().when(inventoryRepository).deleteById(1L);

        assertDoesNotThrow(() -> inventoryService.eliminarItem(1L));
        verify(inventoryRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("obtenerItemsPorBodega: retorna items de la bodega indicada")
    void obtenerItemsPorBodega_retornaItemsDeBodega() {
        when(inventoryRepository.findByBodega("Bodega Central"))
                .thenReturn(Arrays.asList(itemMock));

        List<Item> resultado = inventoryService.obtenerItemsPorBodega("Bodega Central");

        assertEquals(1, resultado.size());
        assertEquals("Bodega Central", resultado.get(0).getBodega());
    }
}
