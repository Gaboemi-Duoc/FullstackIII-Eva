package com.smartlogix.ms_orders.service;

import com.smartlogix.ms_orders.dto.CreateOrderRequest;
import com.smartlogix.ms_orders.model.Order;
import com.smartlogix.ms_orders.model.OrderStatus;
import com.smartlogix.ms_orders.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio que contiene la lógica de negocio para la gestión de órdenes de pedido.
 * <p>
 * Encapsula las operaciones de consulta, creación, actualización de estado y
 * eliminación de órdenes, delegando la persistencia en {@link OrderRepository}.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    /**
     * Crea una nueva instancia del servicio de órdenes.
     *
     * @param orderRepository repositorio utilizado para acceder y persistir las órdenes
     */
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Obtiene el listado completo de órdenes registradas.
     *
     * @return lista con todas las órdenes existentes
     */
    public List<Order> listarOrdenes() {
        return orderRepository.findAll();
    }

    /**
     * Busca una orden por su identificador único.
     *
     * @param id identificador de la orden a buscar
     * @return la orden encontrada
     * @throws RuntimeException si no existe una orden con el id indicado
     */
    public Order obtenerPorId(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
    }

    /**
     * Crea una nueva orden a partir de los datos recibidos, asignándole el estado
     * inicial {@link OrderStatus#CREATED} y la fecha de creación actual.
     *
     * @param request datos necesarios para crear la orden
     * @return la orden creada, ya persistida con su identificador asignado
     */
    public Order crearOrden(CreateOrderRequest request) {

        Order order = new Order();

        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setTotal(request.getTotal());

        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        order.setIdItem(request.getIdItem());
        order.setCantidadSolicitada(request.getCantidadSolicitada());

        return orderRepository.save(order);
    }

    /**
     * Actualiza el estado de una orden existente.
     *
     * @param id identificador de la orden a actualizar
     * @param status nuevo estado a asignar
     * @return la orden actualizada
     * @throws RuntimeException si no existe una orden con el id indicado
     */
    public Order actualizarEstado(Long id, OrderStatus status) {

        Order order = obtenerPorId(id);

        order.setStatus(status);

        return orderRepository.save(order);
    }

    /**
     * Obtiene las órdenes que se encuentran en un estado determinado.
     *
     * @param status estado por el cual filtrar
     * @return lista de órdenes en el estado indicado
     */
    public List<Order> obtenerPorEstado(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Elimina una orden a partir de su identificador.
     *
     * @param id identificador de la orden a eliminar
     */
    public void eliminarOrden(Long id) {
        orderRepository.deleteById(id);
    }
}