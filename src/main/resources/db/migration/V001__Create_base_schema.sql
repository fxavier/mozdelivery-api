-- Enable PostGIS extension
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create tenants table
CREATE TABLE tenants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    vertical VARCHAR(50) NOT NULL CHECK (vertical IN ('RESTAURANT', 'GROCERY', 'PHARMACY', 'CONVENIENCE', 'ELECTRONICS', 'FLORIST', 'BEVERAGES', 'FUEL_STATION')),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    configuration JSONB DEFAULT '{}',
    compliance_settings JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create index on tenant name for lookups
CREATE INDEX idx_tenants_name ON tenants(name);
CREATE INDEX idx_tenants_vertical ON tenants(vertical);
CREATE INDEX idx_tenants_status ON tenants(status);

-- Create service areas table with PostGIS geometry
CREATE TABLE service_areas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    city VARCHAR(100) NOT NULL,
    boundary GEOMETRY(POLYGON, 4326) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create spatial index for service areas
CREATE INDEX idx_service_areas_boundary ON service_areas USING GIST (boundary);
CREATE INDEX idx_service_areas_tenant ON service_areas(tenant_id);
CREATE INDEX idx_service_areas_city ON service_areas(city);

-- Enable Row Level Security on tenants table
ALTER TABLE tenants ENABLE ROW LEVEL SECURITY;

-- Create RLS policy for tenants - users can only see their own tenant
CREATE POLICY tenant_isolation_policy ON tenants
    USING (id = COALESCE(current_setting('app.current_tenant_id', true)::UUID, id));

-- Enable RLS on service_areas
ALTER TABLE service_areas ENABLE ROW LEVEL SECURITY;

-- Create RLS policy for service_areas
CREATE POLICY service_areas_tenant_policy ON service_areas
    USING (tenant_id = COALESCE(current_setting('app.current_tenant_id', true)::UUID, tenant_id));

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at
CREATE TRIGGER update_tenants_updated_at BEFORE UPDATE ON tenants
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_service_areas_updated_at BEFORE UPDATE ON service_areas
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();