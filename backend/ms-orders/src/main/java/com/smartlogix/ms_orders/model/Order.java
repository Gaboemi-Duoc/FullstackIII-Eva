package com.smartlogix.ms_orders.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una orden de pedido dentro de la plataforma SmartLogix.
 * <p>
 * Se mapea a la tabla {@code orders} y almacena la información del cliente, la
 * dirección de entrega, el total, el estado y el item solicitado.
 */
@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    /** Identificador único de la orden, autogenerado por la base de datos. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_order;

    /** Nombre del cliente que realizó el pedido. */
    @Column(nullable = false)
    private String customerName;

    /** Correo electrónico de contacto del cliente. */
    @Column(nullable = false)
    private String customerEmail;

    /** Dirección de entrega del pedido. */
    @Column(nullable = false, length = 500)
    private String deliveryAddress;

    /** Monto total del pedido. */
    @Column(nullable = false)
    private Double total;

    /** Estado actual de la orden (ej. PENDIENTE, ENVIADA, ENTREGADA). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    /** Fecha y hora en que se creó la orden. */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** Identificador del item de inventario solicitado en la orden. */
    @Column(nullable = false)
    private Long idItem;

    /** Cantidad solicitada del item asociado a la orden. */
    @Column(nullable = false)
    private Integer cantidadSolicitada;
}