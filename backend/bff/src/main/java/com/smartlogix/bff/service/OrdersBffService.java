package com.smartlogix.bff.service;

import com.smartlogix.bff.client.InventoryServiceClient;
import com.smartlogix.bff.client.OrdersServiceClient;
import com.smartlogix.bff.dto.CreateOrderRequest;
import com.smartlogix.bff.dto.DtoApiResponse;
import com.smartlogix.bff.dto.UpdateOrderStatusRequest;
import com.smartlogix.bff.model.Item;
import com.smartlogix.bff.model.Order;
import com.smartlogix.bff.model.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OrdersBffService {

    private final OrdersServiceClient ordersServiceClient;
    private final InventoryServiceClient inventoryServiceClient;

    public OrdersBffService(OrdersServiceClient ordersServiceClient,
                            InventoryServiceClient inventoryServiceClient) {
        this.ordersServiceClient = ordersServiceClient;
        this.inventoryServiceClient = inventoryServiceClient;
    }

    public DtoApiResponse<List<Order>> getAllOrders() {
        try {
            log.info("Fetching all orders");
            List<Order> orders = ordersServiceClient.getAllOrders();
            return new DtoApiResponse<>(true, "Orders retrieved successfully", orders, 200);
        } catch (Exception e) {
            log.error("Failed to fetch orders", e);
            return new DtoApiResponse<>(false, "Failed to retrieve orders", null, 500);
        }
    }

    public DtoApiResponse<Order> getOrderById(Long id) {
        try {
            log.info("Fetching order with ID: {}", id);
            Order order = ordersServiceClient.getOrderById(id);
            return new DtoApiResponse<>(true, "Order found", order, 200);
        } catch (Exception e) {
            log.error("Order not found with ID: {}", id, e);
            return new DtoApiResponse<>(false, "Order not found", null, 404);
        }
    }

    public DtoApiResponse<Order> createOrder(CreateOrderRequest request) {
        try {
            log.info("Creating order for customer: {}", request.getCustomerName());
            log.debug("Item ID: {}, Quantity: {}", request.getIdItem(), request.getCantidadSolicitada());

            // Validate stock availability
            Item item = inventoryServiceClient.getItemById(request.getIdItem());
            log.debug("Current stock for item {}: {}", request.getIdItem(), item.getCantidad());

            if (item.getCantidad() < request.getCantidadSolicitada()) {
                log.warn("Insufficient stock for item ID: {}. Available: {}, Requested: {}",
                        request.getIdItem(), item.getCantidad(), request.getCantidadSolicitada());
                return new DtoApiResponse<>(false, "Insufficient stock to create order", null, 400);
            }

            // Create the order
            Order order = ordersServiceClient.createOrder(request);
            log.info("Order created with ID: {}", order.getId_order());

            // Update inventory stock
            Map<String, Integer> stockUpdate = Map.of(
                    "cantidad",
                    item.getCantidad() - request.getCantidadSolicitada()
            );
            
            inventoryServiceClient.actualizarCantidad(request.getIdItem(), stockUpdate);
            log.debug("Stock updated for item ID: {}. New quantity: {}",
                    request.getIdItem(), item.getCantidad() - request.getCantidadSolicitada());

            return new DtoApiResponse<>(true, "Order created successfully", order, 200);

        } catch (Exception e) {
            log.error("Failed to create order for customer: {}", request.getCustomerName(), e);
            return new DtoApiResponse<>(false, "Failed to create order: " + e.getMessage(), null, 500);
        }
    }

    public DtoApiResponse<Order> updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
        try {
            log.info("Updating status for order ID: {} to {}", id, request.getStatus());
            Order updatedOrder = ordersServiceClient.updateStatus(id, request);
            return new DtoApiResponse<>(true, "Order status updated successfully", updatedOrder, 200);
        } catch (Exception e) {
            log.error("Failed to update status for order ID: {}", id, e);
            return new DtoApiResponse<>(false, "Failed to update order status", null, 500);
        }
    }

    public DtoApiResponse<List<Order>> getOrdersByStatus(OrderStatus status) {
        try {
            log.info("Fetching orders with status: {}", status);
            List<Order> orders = ordersServiceClient.getOrdersByStatus(status);
            return new DtoApiResponse<>(true, "Orders retrieved by status", orders, 200);
        } catch (Exception e) {
            log.error("Failed to fetch orders with status: {}", status, e);
            return new DtoApiResponse<>(false, "Failed to retrieve orders", null, 500);
        }
    }

    public DtoApiResponse<Void> deleteOrder(Long id) {
        try {
            log.info("Deleting order with ID: {}", id);
            ordersServiceClient.deleteOrder(id);
            return new DtoApiResponse<>(true, "Order deleted successfully", null, 204);
        } catch (Exception e) {
            log.error("Failed to delete order with ID: {}", id, e);
            return new DtoApiResponse<>(false, "Failed to delete order", null, 500);
        }
    }
}