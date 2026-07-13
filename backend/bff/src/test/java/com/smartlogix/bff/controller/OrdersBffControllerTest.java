package com.smartlogix.bff.controller;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.smartlogix.bff.dto.CreateOrderRequest;
import com.smartlogix.bff.dto.DtoApiResponse;
import com.smartlogix.bff.dto.UpdateOrderStatusRequest;
import com.smartlogix.bff.model.Order;
import com.smartlogix.bff.model.OrderStatus;
import com.smartlogix.bff.service.OrdersBffService;

@ExtendWith(MockitoExtension.class)
class OrdersBffControllerTest {

    @Mock
    private OrdersBffService ordersBffService;

    @InjectMocks
    private OrdersBffController ordersBffController;

    private Order ordenMock;

    @BeforeEach
    void setUp() {
        ordenMock = new Order();
        ordenMock.setId_order(1L);
        ordenMock.setCustomerName("Juan Pérez");
        ordenMock.setStatus(OrderStatus.CREATED);
    }

    @Test
    @DisplayName("listarOrdenes: retorna 200 con lista")
    void listarOrdenes_retorna200() {
        DtoApiResponse<List<Order>> mockResponse = new DtoApiResponse<>(true, "ok", Arrays.asList(ordenMock), 200);
        when(ordersBffService.getAllOrders()).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<List<Order>>> response = ordersBffController.listarOrdenes();

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("obtenerOrden: retorna 200 con orden existente")
    void obtenerOrden_retorna200() {
        DtoApiResponse<Order> mockResponse = new DtoApiResponse<>(true, "ok", ordenMock, 200);
        when(ordersBffService.getOrderById(1L)).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<Order>> response = ordersBffController.obtenerOrden(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Juan Pérez", response.getBody().getData().getCustomerName());
    }

    @Test
    @DisplayName("crearOrden: retorna 200 al crear correctamente")
    void crearOrden_retorna200() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("Juan Pérez");
        request.setCustomerEmail("juan@smartlogix.com");
        request.setDeliveryAddress("Av. Principal 123");
        request.setTotal(15000.0);
        request.setIdItem(5L);
        request.setCantidadSolicitada(10);

        DtoApiResponse<Order> mockResponse = new DtoApiResponse<>(true, "ok", ordenMock, 200);
        when(ordersBffService.createOrder(any(CreateOrderRequest.class))).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<Order>> response = ordersBffController.crearOrden(request);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("actualizarEstado: retorna 200 al actualizar")
    void actualizarEstado_retorna200() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus(OrderStatus.CONFIRMED);

        DtoApiResponse<Order> mockResponse = new DtoApiResponse<>(true, "ok", ordenMock, 200);
        when(ordersBffService.updateOrderStatus(eq(1L), any(UpdateOrderStatusRequest.class))).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<Order>> response = ordersBffController.actualizarEstado(1L, request);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("obtenerPorEstado: retorna 200 con lista filtrada")
    void obtenerPorEstado_retorna200() {
        DtoApiResponse<List<Order>> mockResponse = new DtoApiResponse<>(true, "ok", Arrays.asList(ordenMock), 200);
        when(ordersBffService.getOrdersByStatus(OrderStatus.CREATED)).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<List<Order>>> response = ordersBffController.obtenerPorEstado(OrderStatus.CREATED);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("eliminarOrden: retorna 204 al eliminar")
    void eliminarOrden_retorna204() {
        DtoApiResponse<Void> mockResponse = new DtoApiResponse<>(true, "ok", null, 204);
        when(ordersBffService.deleteOrder(1L)).thenReturn(mockResponse);

        ResponseEntity<DtoApiResponse<Void>> response = ordersBffController.eliminarOrden(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(ordersBffService, times(1)).deleteOrder(1L);
    }
}