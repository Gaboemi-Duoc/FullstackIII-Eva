CREATE TABLE IF NOT EXISTS restock_request (
    id_restock   BIGSERIAL PRIMARY KEY,
    id_item      BIGINT        NOT NULL,
    nombre_item  VARCHAR(255)  NOT NULL,
    bodega       VARCHAR(255)  NOT NULL,
    cantidad_solicitada INTEGER NOT NULL,
    estado       VARCHAR(50)   NOT NULL DEFAULT 'PENDIENTE',
    fecha_solicitud TIMESTAMP  NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_restock_estado    ON restock_request(estado);
CREATE INDEX IF NOT EXISTS idx_restock_id_item   ON restock_request(id_item);
CREATE INDEX IF NOT EXISTS idx_restock_bodega    ON restock_request(bodega);

ALTER TABLE restock_request
    ADD CONSTRAINT check_cantidad_positiva
    CHECK (cantidad_solicitada > 0);

ALTER TABLE restock_request
    ADD CONSTRAINT check_estado_valido
    CHECK (estado IN ('PENDIENTE', 'APROBADA', 'RECHAZADA', 'COMPLETADA'));

COMMENT ON TABLE  restock_request                          IS 'Solicitudes de reposición de stock por ítem y bodega';
COMMENT ON COLUMN restock_request.id_restock               IS 'PK autoincremental';
COMMENT ON COLUMN restock_request.id_item                  IS 'FK lógica al ítem en ms-inventory';
COMMENT ON COLUMN restock_request.nombre_item              IS 'Nombre del ítem (desnormalizado para consultas rápidas)';
COMMENT ON COLUMN restock_request.bodega                   IS 'Bodega que solicita el restock';
COMMENT ON COLUMN restock_request.cantidad_solicitada      IS 'Cantidad pedida (debe ser > 0)';
COMMENT ON COLUMN restock_request.estado                   IS 'Estado del ciclo de vida: PENDIENTE | APROBADA | RECHAZADA | COMPLETADA';
COMMENT ON COLUMN restock_request.fecha_solicitud          IS 'Timestamp de creación automático';
COMMENT ON COLUMN restock_request.fecha_actualizacion      IS 'Timestamp de última actualización de estado';