package com.smartlogix.bff.controller;

import com.smartlogix.bff.client.InventoryServiceClient;
import com.smartlogix.bff.client.OrdersServiceClient;
import com.smartlogix.bff.dto.CreateOrderRequest;
import com.smartlogix.bff.dto.UpdateOrderStatusRequest;
import com.smartlogix.bff.model.Item;
import com.smartlogix.bff.model.Order;
import com.smartlogix.bff.model.OrderStatus;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bff/orders")
@CrossOrigin(origins = "*")
public class OrdersBffController {

    private final OrdersServiceClient ordersServiceClient;
    private final InventoryServiceClient inventoryServiceClient;

    public OrdersBffController(
            OrdersServiceClient ordersServiceClient,
            InventoryServiceClient inventoryServiceClient) {
        this.ordersServiceClient = ordersServiceClient;
        this.inventoryServiceClient = inventoryServiceClient;
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

        System.out.println("====== ENTRANDO A CREAR ORDEN BFF ======");
        System.out.println("idItem: " + request.getIdItem());
        System.out.println("cantidadSolicitada: " + request.getCantidadSolicitada());

        Item item = inventoryServiceClient.getItemById(request.getIdItem());

        System.out.println("====== ITEM OBTENIDO ======");
        System.out.println("Stock actual: " + item.getCantidad());

        if (item.getCantidad() < request.getCantidadSolicitada()) {
            throw new RuntimeException("Stock insuficiente para crear la orden");
        }

        Order order = ordersServiceClient.createOrder(request);

        Map<String, Integer> datos = Map.of(
                "cantidad",
                item.getCantidad() - request.getCantidadSolicitada()
        );

        System.out.println("====== DESCONTANDO STOCK ======");
        System.out.println("Nueva cantidad: " + (item.getCantidad() - request.getCantidadSolicitada()));

        inventoryServiceClient.actualizarCantidad(request.getIdItem(), datos);

        System.out.println("====== STOCK DESCONTADO OK ======");

        return ResponseEntity.ok(order);
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