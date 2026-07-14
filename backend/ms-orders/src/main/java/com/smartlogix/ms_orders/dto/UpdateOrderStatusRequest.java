package com.smartlogix.ms_orders.dto;

import com.smartlogix.ms_orders.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO utilizado para actualizar el estado de una orden existente.
 */
@Data
public class UpdateOrderStatusRequest {

    /** Nuevo estado a asignar a la orden. */
    @NotNull(message = "El estado es obligatorio")
    private OrderStatus status;
}