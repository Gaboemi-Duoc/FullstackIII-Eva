package com.smartlogix.bff.controller;

import com.smartlogix.bff.client.OrdersServiceClient;
import com.smartlogix.bff.dto.CreateOrderRequest;
import com.smartlogix.bff.dto.UpdateOrderStatusRequest;
import com.smartlogix.bff.model.Order;
import com.smartlogix.bff.model.OrderStatus;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bff/orders")
@CrossOrigin(origins = "*")
public class OrdersBffController {

    private final OrdersServiceClient ordersServiceClient;

    public OrdersBffController(OrdersServiceClient ordersServiceClient) {
        this.ordersServiceClient = ordersServiceClient;
    }

    @GetMapping
    public ResponseEntity<List<Order>> listarOrdenes() {
        return ResponseEntity.ok(ordersServiceClient.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> obtenerOrden(@PathVariable Long id) {
        return ResponseEntity.ok(ordersServiceClient.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<Order> crearOrden(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(ordersServiceClient.createOrder(request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        return ResponseEntity.ok(ordersServiceClient.updateStatus(id, request));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> obtenerPorEstado(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(ordersServiceClient.getOrdersByStatus(status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOrden(@PathVariable Long id) {
        ordersServiceClient.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}