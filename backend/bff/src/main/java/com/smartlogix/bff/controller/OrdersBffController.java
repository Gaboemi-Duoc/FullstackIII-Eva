package com.smartlogix.bff.controller;

import com.smartlogix.bff.dto.CreateOrderRequest;
import com.smartlogix.bff.dto.DtoApiResponse;
import com.smartlogix.bff.dto.UpdateOrderStatusRequest;
import com.smartlogix.bff.model.Order;
import com.smartlogix.bff.model.OrderStatus;
import com.smartlogix.bff.service.OrdersBffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bff/orders")
@Tag(name = "Order Management", description = "Endpoints for managing orders in the SmartLogix logistics platform")
public class OrdersBffController {

    private final OrdersBffService ordersBffService;

    public OrdersBffController(OrdersBffService ordersBffService) {
        this.ordersBffService = ordersBffService;
    }

    @GetMapping
    @Operation(
        summary = "List all orders",
        description = "Retrieves a complete list of all orders in the system. Results can be filtered by status or date range (coming soon).",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Orders retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DtoApiResponse<List<Order>>> listarOrdenes() {
        DtoApiResponse<List<Order>> response = ordersBffService.getAllOrders();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get order by ID",
        description = "Retrieves detailed information about a specific order including customer details, total amount, and current status.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order found and retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Order not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DtoApiResponse<Order>> obtenerOrden(
            @Parameter(description = "Unique identifier of the order", required = true, example = "1001")
            @PathVariable Long id) {
        DtoApiResponse<Order> response = ordersBffService.getOrderById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping
    @Operation(
        summary = "Create a new order",
        description = """
            Creates a new order in the system. The process includes:
            1. Validating stock availability
            2. Creating the order
            3. Automatically deducting the ordered quantity from inventory
            
            The order will be created with status 'CREATED'.
            """,
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validation error or insufficient stock",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DtoApiResponse<Order>> crearOrden(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Order creation request with customer and item details",
                required = true,
                content = @Content(schema = @Schema(implementation = CreateOrderRequest.class))
            )
            @Valid @RequestBody CreateOrderRequest request) {
        DtoApiResponse<Order> response = ordersBffService.createOrder(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{id}/status")
    @Operation(
        summary = "Update order status",
        description = """
            Updates the status of an existing order. Valid status transitions:
            - CREATED → CONFIRMED
            - CONFIRMED → IN_PREPARATION
            - IN_PREPARATION → DISPATCHED
            - DISPATCHED → DELIVERED
            - Any status → CANCELLED
            
            Note: CANCELLED orders cannot be changed to any other status.
            """,
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order status updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Order not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid status transition",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DtoApiResponse<Order>> actualizarEstado(
            @Parameter(description = "ID of the order to update", required = true, example = "1001")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Request containing the new status",
                required = true,
                content = @Content(schema = @Schema(implementation = UpdateOrderStatusRequest.class))
            )
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        DtoApiResponse<Order> response = ordersBffService.updateOrderStatus(id, request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/status/{status}")
    @Operation(
        summary = "Get orders by status",
        description = "Retrieves all orders that match the specified status. Useful for order filtering and dashboard views.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Orders retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid status value",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DtoApiResponse<List<Order>>> obtenerPorEstado(
            @Parameter(
                description = "Order status to filter by. Valid values: CREATED, CONFIRMED, IN_PREPARATION, DISPATCHED, DELIVERED, CANCELLED",
                required = true,
                example = "CONFIRMED"
            )
            @PathVariable OrderStatus status) {
        DtoApiResponse<List<Order>> response = ordersBffService.getOrdersByStatus(status);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete order",
        description = "Permanently removes an order from the system. This action cannot be undone. Only orders with status 'CREATED' or 'CANCELLED' can be deleted.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Order deleted successfully",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Order not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Order cannot be deleted in its current status",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Insufficient permissions",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DtoApiResponse<Void>> eliminarOrden(
            @Parameter(description = "ID of the order to delete", required = true, example = "1001")
            @PathVariable Long id) {
        DtoApiResponse<Void> response = ordersBffService.deleteOrder(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}