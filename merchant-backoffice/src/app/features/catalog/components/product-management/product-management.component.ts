import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Store } from '@ngrx/store';
import { Observable, Subject, of } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { CatalogActions } from '../../store/catalog.actions';
import {
  selectProductsByCategoryId,
  selectProductsTotalByCategoryId,
  selectLoading,
  selectError
} from '../../store/catalog.selectors';
import { Product, ProductAvailability } from '../../models/catalog.model';
import { ProductFormModalComponent } from '../product-form-modal/product-form-modal.component';

@Component({
  selector: 'app-product-management',
  standalone: true,
  imports: [CommonModule, ProductFormModalComponent],
  templateUrl: './product-management.component.html',
  styleUrl: './product-management.component.css'
})
export class ProductManagementComponent implements OnInit, OnDestroy {
  @Input() categoryId!: string;
  @Input() categoryName!: string;

  private destroy$ = new Subject<void>();

  products$!: Observable<Product[]>;
  totalProducts$!: Observable<number>;
  loading$: Observable<boolean>;
  error$: Observable<string | null>;

  showCreateModal = false;
  selectedProductForEdit: Product | null = null;
  selectedProducts: Set<string> = new Set();
  currentPage = 0;
  pageSize = 20;

  ProductAvailability = ProductAvailability;

  constructor(private store: Store) {
    this.loading$ = this.store.select(selectLoading);
    this.error$ = this.store.select(selectError);
  }

  ngOnInit(): void {
    if (this.categoryId) {
      this.products$ = this.store.select(selectProductsByCategoryId(this.categoryId));
      this.totalProducts$ = this.store.select(selectProductsTotalByCategoryId(this.categoryId));
      this.loadProducts();
    } else {
      // Provide empty observables if categoryId is not available
      this.products$ = of([]);
      this.totalProducts$ = of(0);
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadProducts(): void {
    this.store.dispatch(CatalogActions.loadProducts({
      categoryId: this.categoryId,
      page: this.currentPage,
      size: this.pageSize
    }));
  }

  onCreateProduct(): void {
    this.selectedProductForEdit = null;
    this.showCreateModal = true;
  }

  onEditProduct(product: Product): void {
    this.selectedProductForEdit = product;
    this.showCreateModal = true;
  }

  onDeleteProduct(product: Product): void {
    if (confirm(`Are you sure you want to delete "${product.name}"?`)) {
      this.store.dispatch(CatalogActions.deleteProduct({ productId: product.id }));
    }
  }

  onToggleAvailability(product: Product): void {
    const newAvailability = product.availability === ProductAvailability.AVAILABLE 
      ? ProductAvailability.OUT_OF_STOCK 
      : ProductAvailability.AVAILABLE;
    
    this.store.dispatch(CatalogActions.updateProductAvailability({
      productId: product.id,
      availability: newAvailability
    }));
  }

  onSelectProduct(productId: string, selected: boolean): void {
    if (selected) {
      this.selectedProducts.add(productId);
    } else {
      this.selectedProducts.delete(productId);
    }
  }

  onSelectAllProducts(selected: boolean): void {
    if (selected) {
      this.products$.pipe(takeUntil(this.destroy$)).subscribe(products => {
        products.forEach(product => this.selectedProducts.add(product.id));
      });
    } else {
      this.selectedProducts.clear();
    }
  }

  onBulkUpdateAvailability(availability: ProductAvailability): void {
    if (this.selectedProducts.size > 0) {
      this.store.dispatch(CatalogActions.bulkUpdateProducts({
        operation: {
          operation: 'UPDATE_AVAILABILITY',
          productIds: Array.from(this.selectedProducts),
          data: { availability }
        }
      }));
      this.selectedProducts.clear();
    }
  }

  onBulkDelete(): void {
    if (this.selectedProducts.size > 0 && 
        confirm(`Are you sure you want to delete ${this.selectedProducts.size} products?`)) {
      this.store.dispatch(CatalogActions.bulkUpdateProducts({
        operation: {
          operation: 'DELETE',
          productIds: Array.from(this.selectedProducts),
          data: {}
        }
      }));
      this.selectedProducts.clear();
    }
  }

  onCloseModal(): void {
    this.showCreateModal = false;
    this.selectedProductForEdit = null;
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadProducts();
  }

  trackByProductId(index: number, product: Product): string {
    return product.id;
  }

  getAvailabilityBadgeClass(availability: ProductAvailability): string {
    switch (availability) {
      case ProductAvailability.AVAILABLE:
        return 'bg-green-100 text-green-800';
      case ProductAvailability.OUT_OF_STOCK:
        return 'bg-red-100 text-red-800';
      case ProductAvailability.DISCONTINUED:
        return 'bg-gray-100 text-gray-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  formatPrice(price: number, currency: string): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency
    }).format(price);
  }

  isProductSelected(productId: string): boolean {
    return this.selectedProducts.has(productId);
  }

  get hasSelectedProducts(): boolean {
    return this.selectedProducts.size > 0;
  }

  get selectedProductsCount(): number {
    return this.selectedProducts.size;
  }
}