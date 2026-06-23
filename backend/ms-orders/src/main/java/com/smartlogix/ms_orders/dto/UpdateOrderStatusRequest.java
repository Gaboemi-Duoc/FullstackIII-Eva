package com.smartlogix.ms_orders.dto;

import com.smartlogix.ms_orders.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {

    @NotNull(message = "El estado es obligatorio")
    private OrderStatus status;
}