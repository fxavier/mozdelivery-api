import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { CatalogActions } from '../../store/catalog.actions';
import {
  selectCatalogs,
  selectSelectedCatalogId,
  selectSelectedCatalog,
  selectLoading,
  selectError,
  selectCatalogStats
} from '../../store/catalog.selectors';
import { Catalog } from '../../models/catalog.model';
import { CatalogFormModalComponent } from '../catalog-form-modal/catalog-form-modal.component';
import { ImportProductsModalComponent } from '../import-products-modal/import-products-modal.component';
import { CategoryManagementComponent } from '../category-management/category-management.component';

@Component({
  selector: 'app-catalog-management',
  standalone: true,
  imports: [
    CommonModule, 
    RouterModule, 
    CatalogFormModalComponent, 
    ImportProductsModalComponent,
    CategoryManagementComponent
  ],
  templateUrl: './catalog-management.component.html',
  styleUrl: './catalog-management.component.css'
})
export class CatalogManagementComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  catalogs$: Observable<Catalog[]>;
  selectedCatalogId$: Observable<string | null>;
  selectedCatalog$: Observable<Catalog | null>;
  loading$: Observable<boolean>;
  error$: Observable<string | null>;
  catalogStats$: Observable<{ totalCatalogs: number; totalCategories: number; totalProducts: number }>;

  showCreateCatalogModal = false;
  showImportModal = false;
  selectedCatalogForActions: Catalog | null = null;

  constructor(private store: Store) {
    this.catalogs$ = this.store.select(selectCatalogs);
    this.selectedCatalogId$ = this.store.select(selectSelectedCatalogId);
    this.selectedCatalog$ = this.store.select(selectSelectedCatalog);
    this.loading$ = this.store.select(selectLoading);
    this.error$ = this.store.select(selectError);
    this.catalogStats$ = this.store.select(selectCatalogStats);
  }

  ngOnInit(): void {
    this.loadCatalogs();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadCatalogs(): void {
    this.store.dispatch(CatalogActions.loadCatalogs());
  }

  onSelectCatalog(catalogId: string): void {
    this.store.dispatch(CatalogActions.setSelectedCatalog({ catalogId }));
    this.store.dispatch(CatalogActions.loadCategories({ catalogId }));
  }

  onCreateCatalog(): void {
    this.showCreateCatalogModal = true;
  }

  onEditCatalog(catalog: Catalog): void {
    this.selectedCatalogForActions = catalog;
    this.showCreateCatalogModal = true;
  }

  onDeleteCatalog(catalog: Catalog): void {
    if (confirm(`Are you sure you want to delete the catalog "${catalog.name}"? This action cannot be undone.`)) {
      this.store.dispatch(CatalogActions.deleteCatalog({ catalogId: catalog.id }));
    }
  }

  onExportCatalog(catalog: Catalog): void {
    this.store.dispatch(CatalogActions.exportProducts({ catalogId: catalog.id }));
  }

  onImportProducts(): void {
    this.showImportModal = true;
  }

  onCloseCreateModal(): void {
    this.showCreateCatalogModal = false;
    this.selectedCatalogForActions = null;
  }

  onCloseImportModal(): void {
    this.showImportModal = false;
  }

  onClearError(): void {
    this.store.dispatch(CatalogActions.clearError());
  }

  trackByCatalogId(index: number, catalog: Catalog): string {
    return catalog.id;
  }

  getTotalProducts(catalog: Catalog): number {
    if (!catalog.categories) return 0;
    return catalog.categories.reduce((total, category) => 
      total + (category.products?.length || 0), 0
    );
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'ACTIVE':
        return 'bg-green-100 text-green-800';
      case 'INACTIVE':
        return 'bg-gray-100 text-gray-800';
      case 'DRAFT':
        return 'bg-yellow-100 text-yellow-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  getRelativeTime(date: Date): string {
    const now = new Date();
    const diffInMs = now.getTime() - new Date(date).getTime();
    const diffInDays = Math.floor(diffInMs / (1000 * 60 * 60 * 24));
    
    if (diffInDays === 0) return 'today';
    if (diffInDays === 1) return 'yesterday';
    if (diffInDays < 7) return `${diffInDays} days ago`;
    if (diffInDays < 30) return `${Math.floor(diffInDays / 7)} weeks ago`;
    return `${Math.floor(diffInDays / 30)} months ago`;
  }
}