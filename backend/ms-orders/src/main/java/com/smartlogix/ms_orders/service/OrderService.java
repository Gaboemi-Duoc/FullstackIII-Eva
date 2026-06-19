package com.smartlogix.ms_orders.service;

import com.smartlogix.ms_orders.dto.CreateOrderRequest;
import com.smartlogix.ms_orders.model.Order;
import com.smartlogix.ms_orders.model.OrderStatus;
import com.smartlogix.ms_orders.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> listarOrdenes() {
        return orderRepository.findAll();
    }

    public Order obtenerPorId(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
    }

    public Order crearOrden(CreateOrderRequest request) {

        Order order = new Order();

        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setTotal(request.getTotal());

        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    public Order actualizarEstado(Long id, OrderStatus status) {

        Order order = obtenerPorId(id);

        order.setStatus(status);

        return orderRepository.save(order);
    }

    public List<Order> obtenerPorEstado(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public void eliminarOrden(Long id) {
        orderRepository.deleteById(id);
    }
}