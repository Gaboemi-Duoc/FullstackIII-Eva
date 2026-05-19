-- Migration V1: Create inventory_item table
-- This migration creates the inventory_item table with the same structure as the original dump

CREATE TABLE IF NOT EXISTS inventory_item (
    id_item BIGSERIAL PRIMARY KEY,
    bodega VARCHAR(255) NOT NULL,
    cantidad INTEGER NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    precio DOUBLE PRECISION NOT NULL
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_inventory_item_bodega ON inventory_item(bodega);
CREATE INDEX IF NOT EXISTS idx_inventory_item_nombre ON inventory_item(nombre);

-- Add check constraint to ensure cantidad is non-negative
ALTER TABLE inventory_item 
    ADD CONSTRAINT check_cantidad_non_negative 
    CHECK (cantidad >= 0);

-- Add check constraint to ensure precio is non-negative
ALTER TABLE inventory_item 
    ADD CONSTRAINT check_precio_non_negative 
    CHECK (precio >= 0);

-- Add comment to table
COMMENT ON TABLE inventory_item IS 'Table to store inventory items';
COMMENT ON COLUMN inventory_item.id_item IS 'Primary key - item identifier';
COMMENT ON COLUMN inventory_item.bodega IS 'Warehouse name';
COMMENT ON COLUMN inventory_item.cantidad IS 'Available quantity (non-negative)';
COMMENT ON COLUMN inventory_item.descripcion IS 'Item description';
COMMENT ON COLUMN inventory_item.nombre IS 'Item name';
COMMENT ON COLUMN inventory_item.precio IS 'Item price (non-negative)';