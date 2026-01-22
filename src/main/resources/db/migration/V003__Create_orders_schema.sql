-- Create orders table
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    customer_id UUID NOT NULL,
    items JSONB NOT NULL,
    delivery_address JSONB NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN (
        'PENDING', 'PAYMENT_PROCESSING', 'PAYMENT_CONFIRMED', 'PREPARING', 
        'READY_FOR_PICKUP', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED', 'REFUNDED'
    )),
    payment_info JSONB NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    currency VARCHAR(3) NOT NULL CHECK (currency IN ('USD', 'MZN')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create indexes for performance
CREATE INDEX idx_orders_tenant_id ON orders(tenant_id);
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_orders_tenant_status ON orders(tenant_id, status);
CREATE INDEX idx_orders_customer_tenant ON orders(customer_id, tenant_id);

-- Enable Row Level Security on orders table
ALTER TABLE orders ENABLE ROW LEVEL SECURITY;

-- Create RLS policy for orders - users can only see orders for their tenant
CREATE POLICY orders_tenant_isolation_policy ON orders
    USING (tenant_id = COALESCE(current_setting('app.current_tenant_id', true)::UUID, tenant_id));

-- Create trigger for updated_at
CREATE TRIGGER update_orders_updated_at BEFORE UPDATE ON orders
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Add constraints for JSON structure validation
ALTER TABLE orders ADD CONSTRAINT orders_items_not_empty 
    CHECK (jsonb_array_length(items) > 0);

ALTER TABLE orders ADD CONSTRAINT orders_delivery_address_has_required_fields
    CHECK (
        delivery_address ? 'street' AND 
        delivery_address ? 'city' AND 
        delivery_address ? 'country' AND
        delivery_address ? 'latitude' AND 
        delivery_address ? 'longitude'
    );

ALTER TABLE orders ADD CONSTRAINT orders_payment_info_has_required_fields
    CHECK (
        payment_info ? 'method' AND 
        payment_info ? 'amount' AND 
        payment_info ? 'currency' AND
        payment_info ? 'status'
    );