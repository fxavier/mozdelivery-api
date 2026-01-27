export interface Catalog {
  id: string;
  merchantId: string;
  name: string;
  description: string;
  status: CatalogStatus;
  categories: Category[];
  createdAt: Date;
  updatedAt: Date;
}

export interface Category {
  id: string;
  catalogId: string;
  name: string;
  description: string;
  displayOrder: number;
  products: Product[];
  createdAt: Date;
  updatedAt: Date;
}

export interface Product {
  id: string;
  categoryId: string;
  merchantId: string;
  name: string;
  description: string;
  imageUrls: string[];
  price: number;
  currency: string;
  availability: ProductAvailability;
  stockInfo?: StockInfo;
  modifiers: ProductModifier[];
  createdAt: Date;
  updatedAt: Date;
}

export interface ProductModifier {
  id: string;
  name: string;
  type: ModifierType;
  required: boolean;
  options: ModifierOption[];
}

export interface ModifierOption {
  id: string;
  name: string;
  priceAdjustment: number;
  available: boolean;
}

export interface StockInfo {
  quantity: number;
  lowStockThreshold: number;
  trackStock: boolean;
}

export enum CatalogStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  DRAFT = 'DRAFT'
}

export enum ProductAvailability {
  AVAILABLE = 'AVAILABLE',
  OUT_OF_STOCK = 'OUT_OF_STOCK',
  DISCONTINUED = 'DISCONTINUED'
}

export enum ModifierType {
  SINGLE_SELECT = 'SINGLE_SELECT',
  MULTI_SELECT = 'MULTI_SELECT',
  TEXT_INPUT = 'TEXT_INPUT'
}

// Request/Response DTOs
export interface CreateCatalogRequest {
  name: string;
  description: string;
}

export interface UpdateCatalogRequest {
  name?: string;
  description?: string;
  status?: CatalogStatus;
}

export interface CreateCategoryRequest {
  catalogId: string;
  name: string;
  description: string;
  displayOrder?: number;
}

export interface UpdateCategoryRequest {
  name?: string;
  description?: string;
  displayOrder?: number;
}

export interface CreateProductRequest {
  categoryId: string;
  name: string;
  description: string;
  price: number;
  currency: string;
  imageUrls?: string[];
  stockInfo?: StockInfo;
  modifiers?: ProductModifier[];
}

export interface UpdateProductRequest {
  name?: string;
  description?: string;
  price?: number;
  imageUrls?: string[];
  availability?: ProductAvailability;
  stockInfo?: StockInfo;
  modifiers?: ProductModifier[];
}

export interface BulkProductOperation {
  operation: 'UPDATE_AVAILABILITY' | 'UPDATE_PRICE' | 'DELETE';
  productIds: string[];
  data?: any;
}

export interface ImportProductData {
  name: string;
  description: string;
  categoryName: string;
  price: number;
  currency: string;
  availability: string;
  stockQuantity?: number;
}