import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Store } from '@ngrx/store';
import { Observable, Subject, of } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { CatalogActions } from '../../store/catalog.actions';
import {
  selectCategoriesByCatalogId,
  selectLoading,
  selectError
} from '../../store/catalog.selectors';
import { Category, CreateCategoryRequest, UpdateCategoryRequest } from '../../models/catalog.model';
import { CategoryFormModalComponent } from '../category-form-modal/category-form-modal.component';
import { ProductManagementComponent } from '../product-management/product-management.component';

@Component({
  selector: 'app-category-management',
  standalone: true,
  imports: [CommonModule, CategoryFormModalComponent, ProductManagementComponent],
  templateUrl: './category-management.component.html',
  styleUrl: './category-management.component.css'
})
export class CategoryManagementComponent implements OnInit, OnDestroy {
  @Input() catalogId!: string;

  private destroy$ = new Subject<void>();

  categories$!: Observable<Category[]>;
  loading$: Observable<boolean>;
  error$: Observable<string | null>;

  showCreateModal = false;
  selectedCategoryForEdit: Category | null = null;
  selectedCategoryForProducts: Category | null = null;
  draggedCategory: Category | null = null;

  constructor(private store: Store) {
    this.loading$ = this.store.select(selectLoading);
    this.error$ = this.store.select(selectError);
  }

  ngOnInit(): void {
    if (this.catalogId) {
      this.categories$ = this.store.select(selectCategoriesByCatalogId(this.catalogId));
      this.loadCategories();
    } else {
      // Provide empty observable if catalogId is not available
      this.categories$ = of([]);
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadCategories(): void {
    this.store.dispatch(CatalogActions.loadCategories({ catalogId: this.catalogId }));
  }

  onCreateCategory(): void {
    this.selectedCategoryForEdit = null;
    this.showCreateModal = true;
  }

  onEditCategory(category: Category): void {
    this.selectedCategoryForEdit = category;
    this.showCreateModal = true;
  }

  onDeleteCategory(category: Category): void {
    if (confirm(`Are you sure you want to delete the category "${category.name}"? This will also delete all products in this category.`)) {
      this.store.dispatch(CatalogActions.deleteCategory({ categoryId: category.id }));
    }
  }

  onViewProducts(category: Category): void {
    this.selectedCategoryForProducts = category;
    this.store.dispatch(CatalogActions.loadProducts({ categoryId: category.id }));
  }

  onCloseProductView(): void {
    this.selectedCategoryForProducts = null;
  }

  onCloseModal(): void {
    this.showCreateModal = false;
    this.selectedCategoryForEdit = null;
  }

  onCategorySubmit(categoryData: CreateCategoryRequest | UpdateCategoryRequest): void {
    if (this.selectedCategoryForEdit) {
      // Update existing category
      this.store.dispatch(CatalogActions.updateCategory({
        categoryId: this.selectedCategoryForEdit.id,
        request: categoryData as UpdateCategoryRequest
      }));
    } else {
      // Create new category
      this.store.dispatch(CatalogActions.createCategory({
        request: {
          ...categoryData as CreateCategoryRequest,
          catalogId: this.catalogId
        }
      }));
    }
    this.onCloseModal();
  }

  // Drag and drop functionality for reordering
  onDragStart(event: DragEvent, category: Category): void {
    this.draggedCategory = category;
    if (event.dataTransfer) {
      event.dataTransfer.effectAllowed = 'move';
      event.dataTransfer.setData('text/html', category.id);
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    if (event.dataTransfer) {
      event.dataTransfer.dropEffect = 'move';
    }
  }

  onDrop(event: DragEvent, targetCategory: Category): void {
    event.preventDefault();
    
    if (this.draggedCategory && this.draggedCategory.id !== targetCategory.id) {
      // Get current categories and reorder them
      this.categories$.pipe(takeUntil(this.destroy$)).subscribe(categories => {
        const reorderedCategories = [...categories];
        const draggedIndex = reorderedCategories.findIndex(c => c.id === this.draggedCategory!.id);
        const targetIndex = reorderedCategories.findIndex(c => c.id === targetCategory.id);
        
        // Remove dragged category and insert at target position
        const [draggedItem] = reorderedCategories.splice(draggedIndex, 1);
        reorderedCategories.splice(targetIndex, 0, draggedItem);
        
        // Dispatch reorder action
        const categoryIds = reorderedCategories.map(c => c.id);
        this.store.dispatch(CatalogActions.reorderCategories({
          catalogId: this.catalogId,
          categoryIds
        }));
      });
    }
    
    this.draggedCategory = null;
  }

  onDragEnd(): void {
    this.draggedCategory = null;
  }

  trackByCategoryId(index: number, category: Category): string {
    return category.id;
  }

  getProductCount(category: Category): number {
    return category.products?.length || 0;
  }
}