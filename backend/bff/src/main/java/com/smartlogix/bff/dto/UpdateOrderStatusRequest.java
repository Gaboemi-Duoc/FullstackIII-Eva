package com.smartlogix.bff.dto;

import com.smartlogix.bff.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    @NotNull(message = "El estado es obligatorio")
    private OrderStatus status;
}