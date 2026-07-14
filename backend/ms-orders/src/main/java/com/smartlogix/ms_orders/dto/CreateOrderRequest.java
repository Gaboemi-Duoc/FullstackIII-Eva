package com.smartlogix.ms_orders.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO utilizado para recibir los datos necesarios al crear una nueva orden de pedido.
 */
@Data
public class CreateOrderRequest {

    /** Nombre del cliente que realiza el pedido. */
    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String customerName;

    /** Correo electrónico de contacto del cliente. */
    @Email(message = "Correo inválido")
    @NotBlank(message = "El correo es obligatorio")
    private String customerEmail;

    /** Dirección de entrega del pedido. */
    @NotBlank(message = "La dirección es obligatoria")
    private String deliveryAddress;

    /** Monto total del pedido, debe ser mayor a 0. */
    @NotNull(message = "El total es obligatorio")
    @Positive(message = "El total debe ser mayor a 0")
    private Double total;

    /** Identificador del item de inventario solicitado. */
    @NotNull(message = "El id del item es obligatorio")
    private Long idItem;

    /** Cantidad solicitada del item, debe ser mayor a 0. */
    @NotNull(message = "La cantidad solicitada es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidadSolicitada;

}
