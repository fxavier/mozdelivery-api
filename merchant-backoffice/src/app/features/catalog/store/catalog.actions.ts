import { createActionGroup, emptyProps, props } from '@ngrx/store';
import {
  Catalog,
  Category,
  Product,
  CreateCatalogRequest,
  UpdateCatalogRequest,
  CreateCategoryRequest,
  UpdateCategoryRequest,
  CreateProductRequest,
  UpdateProductRequest,
  BulkProductOperation
} from '../models/catalog.model';

export const CatalogActions = createActionGroup({
  source: 'Catalog',
  events: {
    // Catalog actions
    'Load Catalogs': emptyProps(),
    'Load Catalogs Success': props<{ catalogs: Catalog[] }>(),
    'Load Catalogs Failure': props<{ error: string }>(),

    'Load Catalog': props<{ catalogId: string }>(),
    'Load Catalog Success': props<{ catalog: Catalog }>(),
    'Load Catalog Failure': props<{ error: string }>(),

    'Create Catalog': props<{ request: CreateCatalogRequest }>(),
    'Create Catalog Success': props<{ catalog: Catalog }>(),
    'Create Catalog Failure': props<{ error: string }>(),

    'Update Catalog': props<{ catalogId: string; request: UpdateCatalogRequest }>(),
    'Update Catalog Success': props<{ catalog: Catalog }>(),
    'Update Catalog Failure': props<{ error: string }>(),

    'Delete Catalog': props<{ catalogId: string }>(),
    'Delete Catalog Success': props<{ catalogId: string }>(),
    'Delete Catalog Failure': props<{ error: string }>(),

    // Category actions
    'Load Categories': props<{ catalogId: string }>(),
    'Load Categories Success': props<{ catalogId: string; categories: Category[] }>(),
    'Load Categories Failure': props<{ error: string }>(),

    'Create Category': props<{ request: CreateCategoryRequest }>(),
    'Create Category Success': props<{ category: Category }>(),
    'Create Category Failure': props<{ error: string }>(),

    'Update Category': props<{ categoryId: string; request: UpdateCategoryRequest }>(),
    'Update Category Success': props<{ category: Category }>(),
    'Update Category Failure': props<{ error: string }>(),

    'Delete Category': props<{ categoryId: string }>(),
    'Delete Category Success': props<{ categoryId: string }>(),
    'Delete Category Failure': props<{ error: string }>(),

    'Reorder Categories': props<{ catalogId: string; categoryIds: string[] }>(),
    'Reorder Categories Success': props<{ catalogId: string; categoryIds: string[] }>(),
    'Reorder Categories Failure': props<{ error: string }>(),

    // Product actions
    'Load Products': props<{ categoryId: string; page?: number; size?: number }>(),
    'Load Products Success': props<{ categoryId: string; products: Product[]; totalElements: number }>(),
    'Load Products Failure': props<{ error: string }>(),

    'Load Product': props<{ productId: string }>(),
    'Load Product Success': props<{ product: Product }>(),
    'Load Product Failure': props<{ error: string }>(),

    'Create Product': props<{ request: CreateProductRequest }>(),
    'Create Product Success': props<{ product: Product }>(),
    'Create Product Failure': props<{ error: string }>(),

    'Update Product': props<{ productId: string; request: UpdateProductRequest }>(),
    'Update Product Success': props<{ product: Product }>(),
    'Update Product Failure': props<{ error: string }>(),

    'Delete Product': props<{ productId: string }>(),
    'Delete Product Success': props<{ productId: string }>(),
    'Delete Product Failure': props<{ error: string }>(),

    'Update Product Availability': props<{ productId: string; availability: string }>(),
    'Update Product Availability Success': props<{ product: Product }>(),
    'Update Product Availability Failure': props<{ error: string }>(),

    // Bulk operations
    'Bulk Update Products': props<{ operation: BulkProductOperation }>(),
    'Bulk Update Products Success': props<{ operation: BulkProductOperation }>(),
    'Bulk Update Products Failure': props<{ error: string }>(),

    // Import/Export
    'Export Products': props<{ catalogId: string }>(),
    'Export Products Success': props<{ blob: Blob }>(),
    'Export Products Failure': props<{ error: string }>(),

    'Import Products': props<{ catalogId: string; file: File }>(),
    'Import Products Success': props<{ result: { success: number; errors: string[] } }>(),
    'Import Products Failure': props<{ error: string }>(),

    // Image operations
    'Upload Product Image': props<{ file: File }>(),
    'Upload Product Image Success': props<{ url: string }>(),
    'Upload Product Image Failure': props<{ error: string }>(),

    'Delete Product Image': props<{ imageUrl: string }>(),
    'Delete Product Image Success': props<{ imageUrl: string }>(),
    'Delete Product Image Failure': props<{ error: string }>(),

    // Search
    'Search Products': props<{ query: string; catalogId?: string }>(),
    'Search Products Success': props<{ products: Product[] }>(),
    'Search Products Failure': props<{ error: string }>(),

    // UI actions
    'Set Selected Catalog': props<{ catalogId: string | null }>(),
    'Set Selected Category': props<{ categoryId: string | null }>(),
    'Set Selected Product': props<{ productId: string | null }>(),
    'Clear Selection': emptyProps(),
    'Set Loading': props<{ loading: boolean }>(),
    'Clear Error': emptyProps()
  }
});