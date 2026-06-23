
package com.smartlogix.bff.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long id_order;
    private String customerName;
    private String customerEmail;
    private String deliveryAddress;
    private Double total;
    private OrderStatus status;
    private LocalDateTime createdAt;
}