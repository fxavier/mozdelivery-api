import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
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
  BulkProductOperation,
  ImportProductData
} from '../models/catalog.model';

@Injectable({
  providedIn: 'root'
})
export class CatalogService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1`;

  constructor(private http: HttpClient) {}

  // Catalog operations
  getCatalogs(): Observable<Catalog[]> {
    return this.http.get<Catalog[]>(`${this.apiUrl}/catalogs`);
  }

  getCatalog(catalogId: string): Observable<Catalog> {
    return this.http.get<Catalog>(`${this.apiUrl}/catalogs/${catalogId}`);
  }

  createCatalog(request: CreateCatalogRequest): Observable<Catalog> {
    return this.http.post<Catalog>(`${this.apiUrl}/catalogs`, request);
  }

  updateCatalog(catalogId: string, request: UpdateCatalogRequest): Observable<Catalog> {
    return this.http.put<Catalog>(`${this.apiUrl}/catalogs/${catalogId}`, request);
  }

  deleteCatalog(catalogId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/catalogs/${catalogId}`);
  }

  // Category operations
  getCategories(catalogId: string): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.apiUrl}/catalogs/${catalogId}/categories`);
  }

  getCategory(categoryId: string): Observable<Category> {
    return this.http.get<Category>(`${this.apiUrl}/categories/${categoryId}`);
  }

  createCategory(request: CreateCategoryRequest): Observable<Category> {
    return this.http.post<Category>(`${this.apiUrl}/categories`, request);
  }

  updateCategory(categoryId: string, request: UpdateCategoryRequest): Observable<Category> {
    return this.http.put<Category>(`${this.apiUrl}/categories/${categoryId}`, request);
  }

  deleteCategory(categoryId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/categories/${categoryId}`);
  }

  reorderCategories(catalogId: string, categoryIds: string[]): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/catalogs/${catalogId}/categories/reorder`, {
      categoryIds
    });
  }

  // Product operations
  getProducts(categoryId: string, page = 0, size = 20): Observable<{ content: Product[], totalElements: number }> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<{ content: Product[], totalElements: number }>(
      `${this.apiUrl}/categories/${categoryId}/products`,
      { params }
    );
  }

  getProduct(productId: string): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/products/${productId}`);
  }

  createProduct(request: CreateProductRequest): Observable<Product> {
    return this.http.post<Product>(`${this.apiUrl}/products`, request);
  }

  updateProduct(productId: string, request: UpdateProductRequest): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/products/${productId}`, request);
  }

  deleteProduct(productId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/products/${productId}`);
  }

  updateProductAvailability(productId: string, availability: string): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/products/${productId}/availability`, {
      availability
    });
  }

  // Bulk operations
  bulkUpdateProducts(operation: BulkProductOperation): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/products/bulk`, operation);
  }

  // Import/Export operations
  exportProducts(catalogId: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/catalogs/${catalogId}/export`, {
      responseType: 'blob'
    });
  }

  importProducts(catalogId: string, file: File): Observable<{ success: number, errors: string[] }> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post<{ success: number, errors: string[] }>(
      `${this.apiUrl}/catalogs/${catalogId}/import`,
      formData
    );
  }

  // Image upload
  uploadProductImage(file: File): Observable<{ url: string }> {
    const formData = new FormData();
    formData.append('image', file);
    
    return this.http.post<{ url: string }>(
      `${this.apiUrl}/products/images/upload`,
      formData
    );
  }

  deleteProductImage(imageUrl: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/products/images`, {
      body: { imageUrl }
    });
  }

  // Search and filtering
  searchProducts(query: string, catalogId?: string): Observable<Product[]> {
    let params = new HttpParams().set('q', query);
    if (catalogId) {
      params = params.set('catalogId', catalogId);
    }
    
    return this.http.get<Product[]>(`${this.apiUrl}/products/search`, { params });
  }
}