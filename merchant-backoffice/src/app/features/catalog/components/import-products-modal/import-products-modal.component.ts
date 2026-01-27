import { Component, Output, EventEmitter, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { CatalogActions } from '../../store/catalog.actions';
import { 
  selectCatalogs, 
  selectLoading, 
  selectImportResult 
} from '../../store/catalog.selectors';
import { Catalog } from '../../models/catalog.model';

@Component({
  selector: 'app-import-products-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './import-products-modal.component.html',
  styleUrl: './import-products-modal.component.css'
})
export class ImportProductsModalComponent implements OnInit, OnDestroy {
  @Output() close = new EventEmitter<void>();

  private destroy$ = new Subject<void>();

  importForm: FormGroup;
  catalogs$: Observable<Catalog[]>;
  loading$: Observable<boolean>;
  importResult$: Observable<{ success: number; errors: string[] } | null>;

  selectedFile: File | null = null;
  dragOver = false;
  importStep: 'select' | 'upload' | 'result' = 'select';

  constructor(
    private fb: FormBuilder,
    private store: Store
  ) {
    this.catalogs$ = this.store.select(selectCatalogs);
    this.loading$ = this.store.select(selectLoading);
    this.importResult$ = this.store.select(selectImportResult);

    this.importForm = this.fb.group({
      catalogId: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.store.dispatch(CatalogActions.loadCatalogs());
    
    // Watch for import results
    this.importResult$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(result => {
      if (result) {
        this.importStep = 'result';
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.validateFile();
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.dragOver = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    this.dragOver = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.dragOver = false;
    
    if (event.dataTransfer?.files && event.dataTransfer.files.length > 0) {
      this.selectedFile = event.dataTransfer.files[0];
      this.validateFile();
    }
  }

  private validateFile(): void {
    if (!this.selectedFile) return;

    const allowedTypes = ['text/csv', 'application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'];
    const maxSize = 5 * 1024 * 1024; // 5MB

    if (!allowedTypes.includes(this.selectedFile.type)) {
      alert('Please select a valid CSV or Excel file.');
      this.selectedFile = null;
      return;
    }

    if (this.selectedFile.size > maxSize) {
      alert('File size must be less than 5MB.');
      this.selectedFile = null;
      return;
    }
  }

  onImport(): void {
    if (this.importForm.valid && this.selectedFile) {
      const catalogId = this.importForm.get('catalogId')?.value;
      this.store.dispatch(CatalogActions.importProducts({
        catalogId,
        file: this.selectedFile
      }));
      this.importStep = 'upload';
    }
  }

  onDownloadTemplate(): void {
    // Create a sample CSV template
    const csvContent = [
      'name,description,categoryName,price,currency,availability,stockQuantity',
      'Sample Product,This is a sample product,Sample Category,10.99,USD,AVAILABLE,100',
      'Another Product,Another sample product,Sample Category,15.50,USD,AVAILABLE,50'
    ].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'product-import-template.csv';
    link.click();
    window.URL.revokeObjectURL(url);
  }

  onClose(): void {
    this.close.emit();
  }

  onBackdropClick(event: Event): void {
    if (event.target === event.currentTarget) {
      this.onClose();
    }
  }

  onStartOver(): void {
    this.importStep = 'select';
    this.selectedFile = null;
    this.importForm.reset();
  }

  getFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }
}