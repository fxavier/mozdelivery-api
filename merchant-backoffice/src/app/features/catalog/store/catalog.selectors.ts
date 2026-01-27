import { createFeatureSelector, createSelector } from '@ngrx/store';
import { CatalogState } from './catalog.reducer';

export const selectCatalogState = createFeatureSelector<CatalogState>('catalog');

// Catalog selectors
export const selectCatalogs = createSelector(
  selectCatalogState,
  (state) => state.catalogs
);

export const selectSelectedCatalog = createSelector(
  selectCatalogState,
  (state) => state.selectedCatalog
);

export const selectSelectedCatalogId = createSelector(
  selectCatalogState,
  (state) => state.selectedCatalogId
);

// Category selectors
export const selectAllCategories = createSelector(
  selectCatalogState,
  (state) => state.categories
);

export const selectCategoriesByCatalogId = (catalogId: string) => createSelector(
  selectCatalogState,
  (state) => state.categories[catalogId] || []
);

export const selectSelectedCategory = createSelector(
  selectCatalogState,
  (state) => state.selectedCategory
);

export const selectSelectedCategoryId = createSelector(
  selectCatalogState,
  (state) => state.selectedCategoryId
);

// Product selectors
export const selectAllProducts = createSelector(
  selectCatalogState,
  (state) => state.products
);

export const selectProductsByCategoryId = (categoryId: string) => createSelector(
  selectCatalogState,
  (state) => state.products[categoryId]?.items || []
);

export const selectProductsTotalByCategoryId = (categoryId: string) => createSelector(
  selectCatalogState,
  (state) => state.products[categoryId]?.totalElements || 0
);

export const selectSelectedProduct = createSelector(
  selectCatalogState,
  (state) => state.selectedProduct
);

export const selectSelectedProductId = createSelector(
  selectCatalogState,
  (state) => state.selectedProductId
);

// Search selectors
export const selectSearchResults = createSelector(
  selectCatalogState,
  (state) => state.searchResults
);

// UI state selectors
export const selectLoading = createSelector(
  selectCatalogState,
  (state) => state.loading
);

export const selectError = createSelector(
  selectCatalogState,
  (state) => state.error
);

// Image upload selectors
export const selectUploadedImageUrls = createSelector(
  selectCatalogState,
  (state) => state.uploadedImageUrls
);

// Import result selectors
export const selectImportResult = createSelector(
  selectCatalogState,
  (state) => state.importResult
);

// Computed selectors
export const selectCatalogWithCategories = createSelector(
  selectSelectedCatalog,
  selectAllCategories,
  (catalog, allCategories) => {
    if (!catalog) return null;
    return {
      ...catalog,
      categories: allCategories[catalog.id] || []
    };
  }
);

export const selectCategoryWithProducts = createSelector(
  selectSelectedCategory,
  selectAllProducts,
  (category, allProducts) => {
    if (!category) return null;
    return {
      ...category,
      products: allProducts[category.id]?.items || []
    };
  }
);

export const selectCatalogStats = createSelector(
  selectCatalogs,
  selectAllCategories,
  selectAllProducts,
  (catalogs, allCategories, allProducts) => {
    const totalCatalogs = catalogs.length;
    const totalCategories = Object.values(allCategories).reduce((sum, categories) => sum + categories.length, 0);
    const totalProducts = Object.values(allProducts).reduce((sum, categoryProducts) => sum + categoryProducts.items.length, 0);
    
    return {
      totalCatalogs,
      totalCategories,
      totalProducts
    };
  }
);

export const selectProductsByAvailability = createSelector(
  selectAllProducts,
  (allProducts) => {
    const stats = {
      available: 0,
      outOfStock: 0,
      discontinued: 0
    };

    Object.values(allProducts).forEach(categoryProducts => {
      categoryProducts.items.forEach(product => {
        switch (product.availability) {
          case 'AVAILABLE':
            stats.available++;
            break;
          case 'OUT_OF_STOCK':
            stats.outOfStock++;
            break;
          case 'DISCONTINUED':
            stats.discontinued++;
            break;
        }
      });
    });

    return stats;
  }
);