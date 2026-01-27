import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

import { Category, CreateCategoryRequest, UpdateCategoryRequest } from '../../models/catalog.model';

@Component({
  selector: 'app-category-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './category-form-modal.component.html',
  styleUrl: './category-form-modal.component.css'
})
export class CategoryFormModalComponent implements OnInit {
  @Input() category: Category | null = null;
  @Input() catalogId!: string;
  @Output() close = new EventEmitter<void>();
  @Output() submit = new EventEmitter<CreateCategoryRequest | UpdateCategoryRequest>();

  categoryForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.categoryForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(500)]],
      displayOrder: [0, [Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    if (this.category) {
      this.categoryForm.patchValue({
        name: this.category.name,
        description: this.category.description,
        displayOrder: this.category.displayOrder
      });
    }
  }

  get isEditing(): boolean {
    return !!this.category;
  }

  get modalTitle(): string {
    return this.isEditing ? 'Edit Category' : 'Create New Category';
  }

  onSubmit(): void {
    if (this.categoryForm.valid) {
      const formValue = this.categoryForm.value;
      
      if (this.isEditing) {
        this.submit.emit(formValue as UpdateCategoryRequest);
      } else {
        this.submit.emit({
          catalogId: this.catalogId,
          name: formValue.name,
          description: formValue.description,
          displayOrder: formValue.displayOrder
        } as CreateCategoryRequest);
      }
    } else {
      this.markFormGroupTouched();
    }
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
    Object.keys(this.categoryForm.controls).forEach(key => {
      const control = this.categoryForm.get(key);
      control?.markAsTouched();
    });
  }

  getFieldError(fieldName: string): string | null {
    const field = this.categoryForm.get(fieldName);
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
      name: 'Category name',
      description: 'Description',
      displayOrder: 'Display order'
    };
    return labels[fieldName] || fieldName;
  }
}