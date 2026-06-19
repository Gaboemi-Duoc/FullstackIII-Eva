package com.smartlogix.ms_orders.controller;

import com.smartlogix.ms_orders.dto.CreateOrderRequest;
import com.smartlogix.ms_orders.dto.UpdateOrderStatusRequest;
import com.smartlogix.ms_orders.model.Order;
import com.smartlogix.ms_orders.model.OrderStatus;
import com.smartlogix.ms_orders.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> listarOrdenes() {
        return ResponseEntity.ok(orderService.listarOrdenes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> obtenerOrden(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Order> crearOrden(
            @Valid @RequestBody CreateOrderRequest request) {

        return ResponseEntity.ok(orderService.crearOrden(request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        return ResponseEntity.ok(
                orderService.actualizarEstado(id, request.getStatus())
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> obtenerPorEstado(
            @PathVariable OrderStatus status) {

        return ResponseEntity.ok(
                orderService.obtenerPorEstado(status)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOrden(@PathVariable Long id) {

        orderService.eliminarOrden(id);

        return ResponseEntity.noContent().build();
    }
}