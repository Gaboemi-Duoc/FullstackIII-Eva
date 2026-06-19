package com.smartlogix.bff.client;

import com.smartlogix.bff.dto.CreateOrderRequest;
import com.smartlogix.bff.dto.UpdateOrderStatusRequest;
import com.smartlogix.bff.model.Order;
import com.smartlogix.bff.model.OrderStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "orders-service", url = "${orders-service.url}")
public interface OrdersServiceClient {

    @GetMapping("/api/orders")
    List<Order> getAllOrders();

    @GetMapping("/api/orders/{id}")
    Order getOrderById(@PathVariable Long id);

    @PostMapping("/api/orders")
    Order createOrder(@RequestBody CreateOrderRequest request);

    @PutMapping("/api/orders/{id}/status")
    Order updateStatus(@PathVariable Long id, @RequestBody UpdateOrderStatusRequest request);

    @GetMapping("/api/orders/status/{status}")
    List<Order> getOrdersByStatus(@PathVariable OrderStatus status);

    @DeleteMapping("/api/orders/{id}")
    void deleteOrder(@PathVariable Long id);
}