package com.smartlogix.ms_orders.controller;

import java.time.LocalDateTime;
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

import com.smartlogix.ms_orders.dto.CreateOrderRequest;
import com.smartlogix.ms_orders.dto.UpdateOrderStatusRequest;
import com.smartlogix.ms_orders.model.Order;
import com.smartlogix.ms_orders.model.OrderStatus;
import com.smartlogix.ms_orders.service.OrderService;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

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
    @DisplayName("GET /api/orders - retorna lista de ordenes")
    void listarOrdenes_retornaLista() {
        when(orderService.listarOrdenes()).thenReturn(Arrays.asList(ordenMock));

        ResponseEntity<List<Order>> response = orderController.listarOrdenes();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("GET /api/orders/{id} - retorna orden existente")
    void obtenerOrden_existente_retorna200() {
        when(orderService.obtenerPorId(1L)).thenReturn(ordenMock);

        ResponseEntity<Order> response = orderController.obtenerOrden(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Juan Pérez", response.getBody().getCustomerName());
    }

    @Test
    @DisplayName("POST /api/orders - crea orden correctamente")
    void crearOrden_retorna200() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("Juan Pérez");
        request.setCustomerEmail("juan@smartlogix.com");
        request.setDeliveryAddress("Av. Principal 123");
        request.setTotal(15000.0);
        request.setIdItem(5L);
        request.setCantidadSolicitada(2);

        when(orderService.crearOrden(any(CreateOrderRequest.class))).thenReturn(ordenMock);

        ResponseEntity<Order> response = orderController.crearOrden(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(OrderStatus.CREATED, response.getBody().getStatus());
    }

    @Test
    @DisplayName("PUT /api/orders/{id}/status - actualiza estado correctamente")
    void actualizarEstado_retorna200() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus(OrderStatus.CONFIRMED);

        ordenMock.setStatus(OrderStatus.CONFIRMED);
        when(orderService.actualizarEstado(1L, OrderStatus.CONFIRMED)).thenReturn(ordenMock);

        ResponseEntity<Order> response = orderController.actualizarEstado(1L, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(OrderStatus.CONFIRMED, response.getBody().getStatus());
    }

    @Test
    @DisplayName("GET /api/orders/status/{status} - retorna ordenes por estado")
    void obtenerPorEstado_retornaLista() {
        when(orderService.obtenerPorEstado(OrderStatus.CREATED))
                .thenReturn(Arrays.asList(ordenMock));

        ResponseEntity<List<Order>> response = orderController.obtenerPorEstado(OrderStatus.CREATED);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("DELETE /api/orders/{id} - elimina orden correctamente")
    void eliminarOrden_retorna204() {
        doNothing().when(orderService).eliminarOrden(1L);

        ResponseEntity<Void> response = orderController.eliminarOrden(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(orderService, times(1)).eliminarOrden(1L);
    }
}