import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';

import { CatalogActions } from '../../store/catalog.actions';
import { selectLoading } from '../../store/catalog.selectors';
import { Catalog, CatalogStatus } from '../../models/catalog.model';

@Component({
  selector: 'app-catalog-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './catalog-form-modal.component.html',
  styleUrl: './catalog-form-modal.component.css'
})
export class CatalogFormModalComponent implements OnInit {
  @Input() catalog: Catalog | null = null;
  @Output() close = new EventEmitter<void>();

  catalogForm: FormGroup;
  loading$: Observable<boolean>;
  catalogStatuses = Object.values(CatalogStatus);

  constructor(
    private fb: FormBuilder,
    private store: Store
  ) {
    this.loading$ = this.store.select(selectLoading);
    
    this.catalogForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(500)]],
      status: [CatalogStatus.DRAFT, Validators.required]
    });
  }

  ngOnInit(): void {
    if (this.catalog) {
      this.catalogForm.patchValue({
        name: this.catalog.name,
        description: this.catalog.description,
        status: this.catalog.status
      });
    }
  }

  get isEditing(): boolean {
    return !!this.catalog;
  }

  get modalTitle(): string {
    return this.isEditing ? 'Edit Catalog' : 'Create New Catalog';
  }

  onSubmit(): void {
    if (this.catalogForm.valid) {
      const formValue = this.catalogForm.value;
      
      if (this.isEditing) {
        this.store.dispatch(CatalogActions.updateCatalog({
          catalogId: this.catalog!.id,
          request: formValue
        }));
      } else {
        this.store.dispatch(CatalogActions.createCatalog({
          request: {
            name: formValue.name,
            description: formValue.description
          }
        }));
      }
      
      this.onClose();
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
    Object.keys(this.catalogForm.controls).forEach(key => {
      const control = this.catalogForm.get(key);
      control?.markAsTouched();
    });
  }

  getFieldError(fieldName: string): string | null {
    const field = this.catalogForm.get(fieldName);
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
    }
    return null;
  }

  private getFieldLabel(fieldName: string): string {
    const labels: { [key: string]: string } = {
      name: 'Catalog name',
      description: 'Description',
      status: 'Status'
    };
    return labels[fieldName] || fieldName;
  }
}