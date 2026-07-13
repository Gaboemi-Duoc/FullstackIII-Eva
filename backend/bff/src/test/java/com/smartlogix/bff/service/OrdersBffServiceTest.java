package com.smartlogix.bff.service;

import com.smartlogix.bff.client.InventoryServiceClient;
import com.smartlogix.bff.client.OrdersServiceClient;
import com.smartlogix.bff.dto.CreateOrderRequest;
import com.smartlogix.bff.dto.DtoApiResponse;
import com.smartlogix.bff.dto.UpdateOrderStatusRequest;
import com.smartlogix.bff.model.Item;
import com.smartlogix.bff.model.Order;
import com.smartlogix.bff.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdersBffServiceTest {

    @Mock
    private OrdersServiceClient ordersServiceClient;

    @Mock
    private InventoryServiceClient inventoryServiceClient;

    @InjectMocks
    private OrdersBffService ordersBffService;

    private Order ordenMock;
    private Item itemMock;
    private CreateOrderRequest requestMock;

    @BeforeEach
    void setUp() {
        ordenMock = new Order();
        ordenMock.setId_order(1L);
        ordenMock.setCustomerName("Juan Pérez");
        ordenMock.setCustomerEmail("juan@smartlogix.com");
        ordenMock.setDeliveryAddress("Av. Principal 123");
        ordenMock.setTotal(15000.0);
        ordenMock.setStatus(OrderStatus.CREATED);

        itemMock = new Item();
        itemMock.setId_item(5L);
        itemMock.setNombre("Caja A");
        itemMock.setCantidad(50);

        requestMock = new CreateOrderRequest();
        requestMock.setCustomerName("Juan Pérez");
        requestMock.setCustomerEmail("juan@smartlogix.com");
        requestMock.setDeliveryAddress("Av. Principal 123");
        requestMock.setTotal(15000.0);
        requestMock.setIdItem(5L);
        requestMock.setCantidadSolicitada(10);
    }

    @Test
    @DisplayName("getAllOrders: retorna lista exitosa")
    void getAllOrders_retornaListaExitosa() {
        when(ordersServiceClient.getAllOrders()).thenReturn(Arrays.asList(ordenMock));

        DtoApiResponse<List<Order>> response = ordersBffService.getAllOrders();

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
    }

    @Test
    @DisplayName("getOrderById: retorna orden existente")
    void getOrderById_existente_retorna200() {
        when(ordersServiceClient.getOrderById(1L)).thenReturn(ordenMock);

        DtoApiResponse<Order> response = ordersBffService.getOrderById(1L);

        assertTrue(response.isSuccess());
        assertEquals("Juan Pérez", response.getData().getCustomerName());
    }

    @Test
    @DisplayName("getOrderById: retorna 404 si no existe")
    void getOrderById_noExistente_retorna404() {
        when(ordersServiceClient.getOrderById(99L)).thenThrow(new RuntimeException("no encontrado"));

        DtoApiResponse<Order> response = ordersBffService.getOrderById(99L);

        assertFalse(response.isSuccess());
        assertEquals(404, response.getStatusCode());
    }

    @Test
    @DisplayName("createOrder: crea orden y descuenta stock cuando hay disponible")
    void createOrder_stockDisponible_creaYDescuenta() {
        when(inventoryServiceClient.getItemById(5L)).thenReturn(itemMock);
        when(ordersServiceClient.createOrder(any(CreateOrderRequest.class))).thenReturn(ordenMock);
        when(inventoryServiceClient.actualizarCantidad(eq(5L), anyMap())).thenReturn(itemMock);

        DtoApiResponse<Order> response = ordersBffService.createOrder(requestMock);

        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatusCode());
        verify(inventoryServiceClient, times(1)).actualizarCantidad(eq(5L), anyMap());
    }

    @Test
    @DisplayName("createOrder: rechaza si no hay stock suficiente")
    void createOrder_stockInsuficiente_retorna400() {
        requestMock.setCantidadSolicitada(100);

        when(inventoryServiceClient.getItemById(5L)).thenReturn(itemMock);

        DtoApiResponse<Order> response = ordersBffService.createOrder(requestMock);

        assertFalse(response.isSuccess());
        assertEquals(400, response.getStatusCode());
        assertEquals("Insufficient stock to create order", response.getMessage());
        verify(ordersServiceClient, never()).createOrder(any());
    }

    @Test
    @DisplayName("updateOrderStatus: actualiza estado correctamente")
    void updateOrderStatus_actualizaCorrectamente() {
        UpdateOrderStatusRequest statusRequest = new UpdateOrderStatusRequest();
        statusRequest.setStatus(OrderStatus.CONFIRMED);

        Order actualizada = new Order();
        actualizada.setId_order(1L);
        actualizada.setStatus(OrderStatus.CONFIRMED);

        when(ordersServiceClient.updateStatus(eq(1L), any(UpdateOrderStatusRequest.class)))
            .thenReturn(actualizada);

        DtoApiResponse<Order> response = ordersBffService.updateOrderStatus(1L, statusRequest);

        assertTrue(response.isSuccess());
        assertEquals(OrderStatus.CONFIRMED, response.getData().getStatus());
    }

    @Test
    @DisplayName("getOrdersByStatus: retorna ordenes filtradas")
    void getOrdersByStatus_retornaListaFiltrada() {
        when(ordersServiceClient.getOrdersByStatus(OrderStatus.CREATED))
            .thenReturn(Arrays.asList(ordenMock));

        DtoApiResponse<List<Order>> response = ordersBffService.getOrdersByStatus(OrderStatus.CREATED);

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
    }

    @Test
    @DisplayName("deleteOrder: elimina correctamente")
    void deleteOrder_eliminaCorrectamente() {
        doNothing().when(ordersServiceClient).deleteOrder(1L);

        DtoApiResponse<Void> response = ordersBffService.deleteOrder(1L);

        assertTrue(response.isSuccess());
        assertEquals(204, response.getStatusCode());
        verify(ordersServiceClient, times(1)).deleteOrder(1L);
    }
}