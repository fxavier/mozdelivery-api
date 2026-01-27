import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';

import { CatalogActions } from '../../store/catalog.actions';
import { selectLoading } from '../../store/catalog.selectors';
import { Product, ProductAvailability } from '../../models/catalog.model';

@Component({
  selector: 'app-product-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './product-form-modal.component.html',
  styleUrl: './product-form-modal.component.css'
})
export class ProductFormModalComponent implements OnInit {
  @Input() product: Product | null = null;
  @Input() categoryId!: string;
  @Output() close = new EventEmitter<void>();

  productForm: FormGroup;
  loading$: Observable<boolean>;
  availabilityOptions = Object.values(ProductAvailability);
  selectedImages: string[] = [];

  constructor(
    private fb: FormBuilder,
    private store: Store
  ) {
    this.loading$ = this.store.select(selectLoading);
    
    this.productForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(200)]],
      description: ['', [Validators.maxLength(1000)]],
      price: [0, [Validators.required, Validators.min(0)]],
      currency: ['USD', Validators.required],
      availability: [ProductAvailability.AVAILABLE, Validators.required],
      trackStock: [false],
      stockQuantity: [0, [Validators.min(0)]],
      lowStockThreshold: [10, [Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    if (this.product) {
      this.productForm.patchValue({
        name: this.product.name,
        description: this.product.description,
        price: this.product.price,
        currency: this.product.currency,
        availability: this.product.availability,
        trackStock: this.product.stockInfo?.trackStock || false,
        stockQuantity: this.product.stockInfo?.quantity || 0,
        lowStockThreshold: this.product.stockInfo?.lowStockThreshold || 10
      });
      this.selectedImages = [...this.product.imageUrls];
    }
  }

  get isEditing(): boolean {
    return !!this.product;
  }

  get modalTitle(): string {
    return this.isEditing ? 'Edit Product' : 'Create New Product';
  }

  get trackStock(): boolean {
    return this.productForm.get('trackStock')?.value || false;
  }

  onSubmit(): void {
    if (this.productForm.valid) {
      const formValue = this.productForm.value;
      
      const productData = {
        name: formValue.name,
        description: formValue.description,
        price: formValue.price,
        currency: formValue.currency,
        availability: formValue.availability,
        imageUrls: this.selectedImages,
        stockInfo: formValue.trackStock ? {
          trackStock: true,
          quantity: formValue.stockQuantity,
          lowStockThreshold: formValue.lowStockThreshold
        } : undefined
      };

      if (this.isEditing) {
        this.store.dispatch(CatalogActions.updateProduct({
          productId: this.product!.id,
          request: productData
        }));
      } else {
        this.store.dispatch(CatalogActions.createProduct({
          request: {
            ...productData,
            categoryId: this.categoryId
          }
        }));
      }
      
      this.onClose();
    } else {
      this.markFormGroupTouched();
    }
  }

  onImageUpload(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      this.store.dispatch(CatalogActions.uploadProductImage({ file }));
    }
  }

  onRemoveImage(imageUrl: string): void {
    this.selectedImages = this.selectedImages.filter(url => url !== imageUrl);
    this.store.dispatch(CatalogActions.deleteProductImage({ imageUrl }));
  }

  onClose(): void {
    this.close.emit();
  }

  onBackdropClick(event: Event): void {
    if (event.target === event.currentTarget) {
      this.onClose();
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.productForm.controls).forEach(key => {
      const control = this.productForm.get(key);
      control?.markAsTouched();
    });
  }

  getFieldError(fieldName: string): string | null {
    const field = this.productForm.get(fieldName);
    if (field?.errors && field.touched) {
      if (field.errors['required']) {
        return `${this.getFieldLabel(fieldName)} is required`;
      }
      if (field.errors['minlength']) {
        return `${this.getFieldLabel(fieldName)} must be at least ${field.errors['minlength'].requiredLength} characters`;
      }
      if (field.errors['maxlength']) {
        return `${this.getFieldLabel(fieldName)} must not exceed ${field.errors['maxlength'].requiredLength} characters`;
      }
      if (field.errors['min']) {
        return `${this.getFieldLabel(fieldName)} must be at least ${field.errors['min'].min}`;
      }
    }
    return null;
  }

  private getFieldLabel(fieldName: string): string {
    const labels: { [key: string]: string } = {
      name: 'Product name',
      description: 'Description',
      price: 'Price',
      currency: 'Currency',
      availability: 'Availability',
      stockQuantity: 'Stock quantity',
      lowStockThreshold: 'Low stock threshold'
    };
    return labels[fieldName] || fieldName;
  }
}