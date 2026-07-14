package com.smartlogix.ms_orders.repository;

import com.smartlogix.ms_orders.model.Order;
import com.smartlogix.ms_orders.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA para el acceso y persistencia de la entidad {@link Order}.
 * <p>
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar y
 * define consultas derivadas adicionales.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Busca las órdenes que se encuentran en un estado determinado.
     *
     * @param status estado por el cual filtrar
     * @return lista de órdenes en el estado indicado
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Busca las órdenes asociadas a un correo de cliente específico.
     *
     * @param customerEmail correo electrónico del cliente
     * @return lista de órdenes registradas para el correo indicado
     */
    List<Order> findByCustomerEmail(String customerEmail);
}