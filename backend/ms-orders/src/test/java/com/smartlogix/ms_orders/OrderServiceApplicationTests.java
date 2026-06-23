package com.smartlogix.ms_orders;

import java.time.LocalDateTime;
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

import com.smartlogix.ms_orders.dto.CreateOrderRequest;
import com.smartlogix.ms_orders.model.Order;
import com.smartlogix.ms_orders.model.OrderStatus;
import com.smartlogix.ms_orders.repository.OrderRepository;
import com.smartlogix.ms_orders.service.OrderService;

@ExtendWith(MockitoExtension.class)
class OrderServiceApplicationTests {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order ordenMock;

    @BeforeEach
    void setUp() {
        ordenMock = new Order();
        ordenMock.setId_order(1L);
        ordenMock.setCustomerName("Juan Pérez");
        ordenMock.setCustomerEmail("juan@smartlogix.com");
        ordenMock.setDeliveryAddress("Av. Principal 123");
        ordenMock.setTotal(15000.0);
        ordenMock.setStatus(OrderStatus.CREATED);
        ordenMock.setCreatedAt(LocalDateTime.now());
        ordenMock.setIdItem(5L);
        ordenMock.setCantidadSolicitada(2);
    }

    @Test
    @DisplayName("listarOrdenes: retorna lista de ordenes")
    void listarOrdenes_retornaLista() {
        when(orderRepository.findAll()).thenReturn(Arrays.asList(ordenMock));

        List<Order> resultado = orderService.listarOrdenes();

        assertEquals(1, resultado.size());
        assertEquals("Juan Pérez", resultado.get(0).getCustomerName());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerPorId: retorna orden cuando existe")
    void obtenerPorId_existente_retornaOrden() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(ordenMock));

        Order resultado = orderService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId_order());
    }

    @Test
    @DisplayName("obtenerPorId: lanza excepcion cuando no existe")
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.obtenerPorId(99L));

        assertTrue(ex.getMessage().contains("Orden no encontrada"));
    }

    @Test
    @DisplayName("crearOrden: fuerza estado CREATED y persiste")
    void crearOrden_fuerzaEstadoCreated() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("Juan Pérez");
        request.setCustomerEmail("juan@smartlogix.com");
        request.setDeliveryAddress("Av. Principal 123");
        request.setTotal(15000.0);
        request.setIdItem(5L);
        request.setCantidadSolicitada(2);

        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order resultado = orderService.crearOrden(request);

        assertEquals(OrderStatus.CREATED, resultado.getStatus());
        assertNotNull(resultado.getCreatedAt());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("actualizarEstado: actualiza estado correctamente")
    void actualizarEstado_actualizaCorrectamente() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(ordenMock));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order resultado = orderService.actualizarEstado(1L, OrderStatus.CONFIRMED);

        assertEquals(OrderStatus.CONFIRMED, resultado.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("obtenerPorEstado: retorna ordenes filtradas por estado")
    void obtenerPorEstado_retornaOrdenesFiltradas() {
        when(orderRepository.findByStatus(OrderStatus.CREATED))
                .thenReturn(Arrays.asList(ordenMock));

        List<Order> resultado = orderService.obtenerPorEstado(OrderStatus.CREATED);

        assertEquals(1, resultado.size());
        assertEquals(OrderStatus.CREATED, resultado.get(0).getStatus());
    }

    @Test
    @DisplayName("eliminarOrden: llama deleteById correctamente")
    void eliminarOrden_eliminaCorrectamente() {
        doNothing().when(orderRepository).deleteById(1L);

        assertDoesNotThrow(() -> orderService.eliminarOrden(1L));
        verify(orderRepository, times(1)).deleteById(1L);
    }
}
