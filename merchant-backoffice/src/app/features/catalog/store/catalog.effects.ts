import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { map, catchError, switchMap, tap } from 'rxjs/operators';
import { CatalogService } from '../services/catalog.service';
import { CatalogActions } from './catalog.actions';

@Injectable()
export class CatalogEffects {

  constructor(
    private actions$: Actions,
    private catalogService: CatalogService
  ) {}

  // Catalog effects
  loadCatalogs$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.loadCatalogs),
      switchMap(() =>
        this.catalogService.getCatalogs().pipe(
          map(catalogs => CatalogActions.loadCatalogsSuccess({ catalogs })),
          catchError(error => of(CatalogActions.loadCatalogsFailure({ error: error.message })))
        )
      )
    )
  );

  loadCatalog$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.loadCatalog),
      switchMap(({ catalogId }) =>
        this.catalogService.getCatalog(catalogId).pipe(
          map(catalog => CatalogActions.loadCatalogSuccess({ catalog })),
          catchError(error => of(CatalogActions.loadCatalogFailure({ error: error.message })))
        )
      )
    )
  );

  createCatalog$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.createCatalog),
      switchMap(({ request }) =>
        this.catalogService.createCatalog(request).pipe(
          map(catalog => CatalogActions.createCatalogSuccess({ catalog })),
          catchError(error => of(CatalogActions.createCatalogFailure({ error: error.message })))
        )
      )
    )
  );

  updateCatalog$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.updateCatalog),
      switchMap(({ catalogId, request }) =>
        this.catalogService.updateCatalog(catalogId, request).pipe(
          map(catalog => CatalogActions.updateCatalogSuccess({ catalog })),
          catchError(error => of(CatalogActions.updateCatalogFailure({ error: error.message })))
        )
      )
    )
  );

  deleteCatalog$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.deleteCatalog),
      switchMap(({ catalogId }) =>
        this.catalogService.deleteCatalog(catalogId).pipe(
          map(() => CatalogActions.deleteCatalogSuccess({ catalogId })),
          catchError(error => of(CatalogActions.deleteCatalogFailure({ error: error.message })))
        )
      )
    )
  );

  // Category effects
  loadCategories$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.loadCategories),
      switchMap(({ catalogId }) =>
        this.catalogService.getCategories(catalogId).pipe(
          map(categories => CatalogActions.loadCategoriesSuccess({ catalogId, categories })),
          catchError(error => of(CatalogActions.loadCategoriesFailure({ error: error.message })))
        )
      )
    )
  );

  createCategory$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.createCategory),
      switchMap(({ request }) =>
        this.catalogService.createCategory(request).pipe(
          map(category => CatalogActions.createCategorySuccess({ category })),
          catchError(error => of(CatalogActions.createCategoryFailure({ error: error.message })))
        )
      )
    )
  );

  updateCategory$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.updateCategory),
      switchMap(({ categoryId, request }) =>
        this.catalogService.updateCategory(categoryId, request).pipe(
          map(category => CatalogActions.updateCategorySuccess({ category })),
          catchError(error => of(CatalogActions.updateCategoryFailure({ error: error.message })))
        )
      )
    )
  );

  deleteCategory$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.deleteCategory),
      switchMap(({ categoryId }) =>
        this.catalogService.deleteCategory(categoryId).pipe(
          map(() => CatalogActions.deleteCategorySuccess({ categoryId })),
          catchError(error => of(CatalogActions.deleteCategoryFailure({ error: error.message })))
        )
      )
    )
  );

  reorderCategories$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.reorderCategories),
      switchMap(({ catalogId, categoryIds }) =>
        this.catalogService.reorderCategories(catalogId, categoryIds).pipe(
          map(() => CatalogActions.reorderCategoriesSuccess({ catalogId, categoryIds })),
          catchError(error => of(CatalogActions.reorderCategoriesFailure({ error: error.message })))
        )
      )
    )
  );

  // Product effects
  loadProducts$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.loadProducts),
      switchMap(({ categoryId, page, size }) =>
        this.catalogService.getProducts(categoryId, page, size).pipe(
          map(response => CatalogActions.loadProductsSuccess({ 
            categoryId, 
            products: response.content, 
            totalElements: response.totalElements 
          })),
          catchError(error => of(CatalogActions.loadProductsFailure({ error: error.message })))
        )
      )
    )
  );

  loadProduct$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.loadProduct),
      switchMap(({ productId }) =>
        this.catalogService.getProduct(productId).pipe(
          map(product => CatalogActions.loadProductSuccess({ product })),
          catchError(error => of(CatalogActions.loadProductFailure({ error: error.message })))
        )
      )
    )
  );

  createProduct$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.createProduct),
      switchMap(({ request }) =>
        this.catalogService.createProduct(request).pipe(
          map(product => CatalogActions.createProductSuccess({ product })),
          catchError(error => of(CatalogActions.createProductFailure({ error: error.message })))
        )
      )
    )
  );

  updateProduct$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.updateProduct),
      switchMap(({ productId, request }) =>
        this.catalogService.updateProduct(productId, request).pipe(
          map(product => CatalogActions.updateProductSuccess({ product })),
          catchError(error => of(CatalogActions.updateProductFailure({ error: error.message })))
        )
      )
    )
  );

  deleteProduct$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.deleteProduct),
      switchMap(({ productId }) =>
        this.catalogService.deleteProduct(productId).pipe(
          map(() => CatalogActions.deleteProductSuccess({ productId })),
          catchError(error => of(CatalogActions.deleteProductFailure({ error: error.message })))
        )
      )
    )
  );

  updateProductAvailability$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.updateProductAvailability),
      switchMap(({ productId, availability }) =>
        this.catalogService.updateProductAvailability(productId, availability).pipe(
          map(product => CatalogActions.updateProductAvailabilitySuccess({ product })),
          catchError(error => of(CatalogActions.updateProductAvailabilityFailure({ error: error.message })))
        )
      )
    )
  );

  // Bulk operations
  bulkUpdateProducts$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.bulkUpdateProducts),
      switchMap(({ operation }) =>
        this.catalogService.bulkUpdateProducts(operation).pipe(
          map(() => CatalogActions.bulkUpdateProductsSuccess({ operation })),
          catchError(error => of(CatalogActions.bulkUpdateProductsFailure({ error: error.message })))
        )
      )
    )
  );

  // Import/Export effects
  exportProducts$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.exportProducts),
      switchMap(({ catalogId }) =>
        this.catalogService.exportProducts(catalogId).pipe(
          map(blob => CatalogActions.exportProductsSuccess({ blob })),
          catchError(error => of(CatalogActions.exportProductsFailure({ error: error.message })))
        )
      )
    )
  );

  exportProductsSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.exportProductsSuccess),
      tap(({ blob }) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `products-export-${new Date().toISOString().split('T')[0]}.csv`;
        link.click();
        window.URL.revokeObjectURL(url);
      })
    ),
    { dispatch: false }
  );

  importProducts$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.importProducts),
      switchMap(({ catalogId, file }) =>
        this.catalogService.importProducts(catalogId, file).pipe(
          map(result => CatalogActions.importProductsSuccess({ result })),
          catchError(error => of(CatalogActions.importProductsFailure({ error: error.message })))
        )
      )
    )
  );

  // Image upload effects
  uploadProductImage$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.uploadProductImage),
      switchMap(({ file }) =>
        this.catalogService.uploadProductImage(file).pipe(
          map(response => CatalogActions.uploadProductImageSuccess({ url: response.url })),
          catchError(error => of(CatalogActions.uploadProductImageFailure({ error: error.message })))
        )
      )
    )
  );

  deleteProductImage$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.deleteProductImage),
      switchMap(({ imageUrl }) =>
        this.catalogService.deleteProductImage(imageUrl).pipe(
          map(() => CatalogActions.deleteProductImageSuccess({ imageUrl })),
          catchError(error => of(CatalogActions.deleteProductImageFailure({ error: error.message })))
        )
      )
    )
  );

  // Search effects
  searchProducts$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.searchProducts),
      switchMap(({ query, catalogId }) =>
        this.catalogService.searchProducts(query, catalogId).pipe(
          map(products => CatalogActions.searchProductsSuccess({ products })),
          catchError(error => of(CatalogActions.searchProductsFailure({ error: error.message })))
        )
      )
    )
  );
}