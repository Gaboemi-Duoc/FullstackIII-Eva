package com.smartlogix.ms_orders.model;

/**
 * Representa los posibles estados por los que puede pasar una orden de pedido
 * a lo largo de su ciclo de vida.
 */
public enum OrderStatus {
    /** La orden fue creada y está pendiente de confirmación. */
    CREATED,
    /** La orden fue confirmada. */
    CONFIRMED,
    /** La orden se encuentra en preparación. */
    IN_PREPARATION,
    /** La orden fue despachada. */
    DISPATCHED,
    /** La orden fue entregada al cliente. */
    DELIVERED,
    /** La orden fue cancelada. */
    CANCELLED
}