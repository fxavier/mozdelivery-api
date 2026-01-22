-- Update service_areas table to match the new domain model
ALTER TABLE service_areas 
ADD COLUMN IF NOT EXISTS country_code VARCHAR(2) NOT NULL DEFAULT 'MZ',
ADD COLUMN IF NOT EXISTS city_center_lat DECIMAL(10,8) NOT NULL DEFAULT -25.9692,
ADD COLUMN IF NOT EXISTS city_center_lon DECIMAL(11,8) NOT NULL DEFAULT 32.5732;

-- Update existing records with default values for Maputo
UPDATE service_areas 
SET country_code = 'MZ',
    city_center_lat = CASE 
        WHEN city = 'Maputo' THEN -25.9692
        WHEN city = 'Beira' THEN -19.8436
        WHEN city = 'Nampula' THEN -15.1165
        ELSE -25.9692
    END,
    city_center_lon = CASE 
        WHEN city = 'Maputo' THEN 32.5732
        WHEN city = 'Beira' THEN 34.8389
        WHEN city = 'Nampula' THEN 39.2666
        ELSE 32.5732
    END
WHERE country_code IS NULL OR city_center_lat IS NULL OR city_center_lon IS NULL;

-- Create additional indexes for performance
CREATE INDEX IF NOT EXISTS idx_service_areas_city_active ON service_areas(city, is_active);
CREATE INDEX IF NOT EXISTS idx_service_areas_tenant_active ON service_areas(tenant_id, is_active);
CREATE INDEX IF NOT EXISTS idx_service_areas_country ON service_areas(country_code);

-- Create a composite index for common queries
CREATE INDEX IF NOT EXISTS idx_service_areas_tenant_city_active ON service_areas(tenant_id, city, is_active);

-- Add constraint to ensure country_code is valid
ALTER TABLE service_areas 
ADD CONSTRAINT chk_country_code_format 
CHECK (country_code ~ '^[A-Z]{2}$');

-- Add constraint to ensure coordinates are within valid ranges
ALTER TABLE service_areas 
ADD CONSTRAINT chk_city_center_lat_range 
CHECK (city_center_lat >= -90 AND city_center_lat <= 90);

ALTER TABLE service_areas 
ADD CONSTRAINT chk_city_center_lon_range 
CHECK (city_center_lon >= -180 AND city_center_lon <= 180);

-- Create a function to validate service area boundaries don't overlap for the same tenant
CREATE OR REPLACE FUNCTION validate_service_area_overlap()
RETURNS TRIGGER AS $$
BEGIN
    -- Check if the new/updated service area overlaps with existing ones for the same tenant
    IF EXISTS (
        SELECT 1 
        FROM service_areas sa 
        WHERE sa.tenant_id = NEW.tenant_id 
        AND sa.city = NEW.city 
        AND sa.id != NEW.id 
        AND sa.is_active = true 
        AND NEW.is_active = true
        AND ST_Intersects(sa.boundary, NEW.boundary)
    ) THEN
        RAISE EXCEPTION 'Service area boundary overlaps with existing service area for the same tenant in the same city';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to validate overlaps
DROP TRIGGER IF EXISTS trg_validate_service_area_overlap ON service_areas;
CREATE TRIGGER trg_validate_service_area_overlap
    BEFORE INSERT OR UPDATE ON service_areas
    FOR EACH ROW
    EXECUTE FUNCTION validate_service_area_overlap();