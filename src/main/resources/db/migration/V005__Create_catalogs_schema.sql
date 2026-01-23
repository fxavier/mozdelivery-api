-- Create catalogs table
CREATE TABLE catalogs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'ACTIVE', 'INACTIVE', 'ARCHIVED')),
    display_order INTEGER,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create indexes for catalogs
CREATE INDEX idx_catalogs_merchant_id ON catalogs(merchant_id);
CREATE INDEX idx_catalogs_status ON catalogs(status);
CREATE INDEX idx_catalogs_display_order ON catalogs(display_order);
CREATE INDEX idx_catalogs_merchant_status ON catalogs(merchant_id, status);
CREATE INDEX idx_catalogs_merchant_display_order ON catalogs(merchant_id, display_order);

-- Add foreign key constraint to merchants table
ALTER TABLE catalogs ADD CONSTRAINT fk_catalogs_merchant_id 
    FOREIGN KEY (merchant_id) REFERENCES merchants(id) ON DELETE CASCADE;

-- Add unique constraint for catalog name per merchant
ALTER TABLE catalogs ADD CONSTRAINT uk_catalogs_merchant_name 
    UNIQUE (merchant_id, name);

-- Enable Row Level Security for multi-merchant isolation
ALTER TABLE catalogs ENABLE ROW LEVEL SECURITY;

-- Create RLS policy for catalogs - users can only see catalogs for their merchant
CREATE POLICY catalog_merchant_isolation_policy ON catalogs
    USING (merchant_id = current_setting('app.current_merchant_id', true)::UUID);

-- Create trigger for updated_at
CREATE TRIGGER update_catalogs_updated_at BEFORE UPDATE ON catalogs
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Create categories table
CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    catalog_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    is_visible BOOLEAN DEFAULT true,
    display_order INTEGER,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create indexes for categories
CREATE INDEX idx_categories_catalog_id ON categories(catalog_id);
CREATE INDEX idx_categories_is_visible ON categories(is_visible);
CREATE INDEX idx_categories_display_order ON categories(display_order);
CREATE INDEX idx_categories_catalog_visible ON categories(catalog_id, is_visible);
CREATE INDEX idx_categories_catalog_display_order ON categories(catalog_id, display_order);

-- Add foreign key constraint to catalogs table
ALTER TABLE categories ADD CONSTRAINT fk_categories_catalog_id 
    FOREIGN KEY (catalog_id) REFERENCES catalogs(id) ON DELETE CASCADE;

-- Add unique constraint for category name per catalog
ALTER TABLE categories ADD CONSTRAINT uk_categories_catalog_name 
    UNIQUE (catalog_id, name);

-- Enable Row Level Security for categories
ALTER TABLE categories ENABLE ROW LEVEL SECURITY;

-- Create RLS policy for categories - inherit from catalog's merchant
CREATE POLICY category_merchant_isolation_policy ON categories
    USING (
        catalog_id IN (
            SELECT id FROM catalogs 
            WHERE merchant_id = current_setting('app.current_merchant_id', true)::UUID
        )
    );

-- Create trigger for updated_at
CREATE TRIGGER update_categories_updated_at BEFORE UPDATE ON categories
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Create products table
CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    image_urls JSONB DEFAULT '[]',
    base_price DECIMAL(10,2) NOT NULL CHECK (base_price >= 0),
    currency VARCHAR(3) NOT NULL DEFAULT 'MZN' CHECK (currency IN ('USD', 'MZN')),
    availability VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' CHECK (availability IN ('AVAILABLE', 'OUT_OF_STOCK', 'DISCONTINUED', 'HIDDEN')),
    is_visible BOOLEAN DEFAULT true,
    stock_info JSONB,
    modifiers JSONB DEFAULT '[]',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create indexes for products
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_availability ON products(availability);
CREATE INDEX idx_products_is_visible ON products(is_visible);
CREATE INDEX idx_products_base_price ON products(base_price);
CREATE INDEX idx_products_category_visible ON products(category_id, is_visible);
CREATE INDEX idx_products_category_availability ON products(category_id, availability);

-- Add foreign key constraint to categories table
ALTER TABLE products ADD CONSTRAINT fk_products_category_id 
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE;

-- Add unique constraint for product name per category
ALTER TABLE products ADD CONSTRAINT uk_products_category_name 
    UNIQUE (category_id, name);

-- Enable Row Level Security for products
ALTER TABLE products ENABLE ROW LEVEL SECURITY;

-- Create RLS policy for products - inherit from category's catalog's merchant
CREATE POLICY product_merchant_isolation_policy ON products
    USING (
        category_id IN (
            SELECT c.id FROM categories c
            JOIN catalogs cat ON c.catalog_id = cat.id
            WHERE cat.merchant_id = current_setting('app.current_merchant_id', true)::UUID
        )
    );

-- Create trigger for updated_at
CREATE TRIGGER update_products_updated_at BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Add JSON validation constraints for products
ALTER TABLE products ADD CONSTRAINT products_image_urls_is_array
    CHECK (jsonb_typeof(image_urls) = 'array');

ALTER TABLE products ADD CONSTRAINT products_modifiers_is_array
    CHECK (jsonb_typeof(modifiers) = 'array');

-- Add stock_info validation (if present, must have required fields)
ALTER TABLE products ADD CONSTRAINT products_stock_info_valid
    CHECK (
        stock_info IS NULL OR (
            stock_info ? 'currentStock' AND 
            stock_info ? 'lowStockThreshold' AND 
            stock_info ? 'maxStock' AND
            (stock_info->>'currentStock')::integer >= 0 AND
            (stock_info->>'lowStockThreshold')::integer >= 0 AND
            (stock_info->>'maxStock')::integer >= 0
        )
    );