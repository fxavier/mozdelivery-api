-- Create merchants table (new multi-merchant architecture)
CREATE TABLE merchants (
    id UUID PRIMARY KEY,
    business_name VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    business_registration_number VARCHAR(100),
    tax_id VARCHAR(100),
    contact_email VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(50) NOT NULL,
    business_address TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    vertical VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    configuration JSONB,
    compliance_settings JSONB,
    approval_status JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create indexes for merchants
CREATE INDEX idx_merchants_status ON merchants(status);
CREATE INDEX idx_merchants_vertical ON merchants(vertical);
CREATE INDEX idx_merchants_city ON merchants(city);
CREATE INDEX idx_merchants_created_at ON merchants(created_at);
CREATE UNIQUE INDEX idx_merchants_business_name ON merchants(business_name);
CREATE UNIQUE INDEX idx_merchants_contact_email ON merchants(contact_email);

-- Add merchant_id column to orders table for multi-merchant support
ALTER TABLE orders ADD COLUMN merchant_id UUID;

-- Create index for merchant_id in orders
CREATE INDEX idx_orders_merchant_id ON orders(merchant_id);

-- Add foreign key constraint (will be populated during data migration)
-- ALTER TABLE orders ADD CONSTRAINT fk_orders_merchant_id FOREIGN KEY (merchant_id) REFERENCES merchants(id);

-- Row Level Security for multi-merchant isolation
ALTER TABLE merchants ENABLE ROW LEVEL SECURITY;
CREATE POLICY merchant_isolation_policy ON merchants
    USING (id = current_setting('app.current_merchant_id', true)::UUID);

-- Update orders RLS policy to support both tenant_id and merchant_id
DROP POLICY IF EXISTS tenant_isolation_policy ON orders;
CREATE POLICY merchant_order_isolation_policy ON orders
    USING (
        tenant_id = current_setting('app.current_tenant_id', true)::UUID OR
        merchant_id = current_setting('app.current_merchant_id', true)::UUID
    );

-- Create audit table for merchant changes
CREATE TABLE merchant_audit (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id UUID NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_data JSONB NOT NULL,
    created_by VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_merchant_audit_merchant_id ON merchant_audit(merchant_id);
CREATE INDEX idx_merchant_audit_event_type ON merchant_audit(event_type);
CREATE INDEX idx_merchant_audit_created_at ON merchant_audit(created_at);