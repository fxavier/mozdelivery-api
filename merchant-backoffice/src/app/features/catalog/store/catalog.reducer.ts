import { createReducer, on } from '@ngrx/store';
import { Catalog, Category, Product } from '../models/catalog.model';
import { CatalogActions } from './catalog.actions';

export interface CatalogState {
  catalogs: Catalog[];
  categories: { [catalogId: string]: Category[] };
  products: { [categoryId: string]: { items: Product[]; totalElements: number } };
  selectedCatalogId: string | null;
  selectedCategoryId: string | null;
  selectedProductId: string | null;
  selectedCatalog: Catalog | null;
  selectedCategory: Category | null;
  selectedProduct: Product | null;
  searchResults: Product[];
  loading: boolean;
  error: string | null;
  uploadedImageUrls: string[];
  importResult: { success: number; errors: string[] } | null;
}

export const initialState: CatalogState = {
  catalogs: [],
  categories: {},
  products: {},
  selectedCatalogId: null,
  selectedCategoryId: null,
  selectedProductId: null,
  selectedCatalog: null,
  selectedCategory: null,
  selectedProduct: null,
  searchResults: [],
  loading: false,
  error: null,
  uploadedImageUrls: [],
  importResult: null
};

export const catalogReducer = createReducer(
  initialState,

  // Loading and error handling
  on(CatalogActions.setLoading, (state, { loading }) => ({
    ...state,
    loading
  })),

  on(CatalogActions.clearError, (state) => ({
    ...state,
    error: null
  })),

  // Catalog reducers
  on(CatalogActions.loadCatalogs, (state) => ({
    ...state,
    loading: true,
    error: null
  })),

  on(CatalogActions.loadCatalogsSuccess, (state, { catalogs }) => ({
    ...state,
    catalogs,
    loading: false,
    error: null
  })),

  on(CatalogActions.loadCatalogsFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),

  on(CatalogActions.loadCatalogSuccess, (state, { catalog }) => ({
    ...state,
    selectedCatalog: catalog,
    loading: false,
    error: null
  })),

  on(CatalogActions.createCatalogSuccess, (state, { catalog }) => ({
    ...state,
    catalogs: [...state.catalogs, catalog],
    loading: false,
    error: null
  })),

  on(CatalogActions.updateCatalogSuccess, (state, { catalog }) => ({
    ...state,
    catalogs: state.catalogs.map(c => c.id === catalog.id ? catalog : c),
    selectedCatalog: state.selectedCatalog?.id === catalog.id ? catalog : state.selectedCatalog,
    loading: false,
    error: null
  })),

  on(CatalogActions.deleteCatalogSuccess, (state, { catalogId }) => ({
    ...state,
    catalogs: state.catalogs.filter(c => c.id !== catalogId),
    selectedCatalog: state.selectedCatalog?.id === catalogId ? null : state.selectedCatalog,
    selectedCatalogId: state.selectedCatalogId === catalogId ? null : state.selectedCatalogId,
    loading: false,
    error: null
  })),

  // Category reducers
  on(CatalogActions.loadCategoriesSuccess, (state, { catalogId, categories }) => ({
    ...state,
    categories: {
      ...state.categories,
      [catalogId]: categories
    },
    loading: false,
    error: null
  })),

  on(CatalogActions.createCategorySuccess, (state, { category }) => {
    const catalogCategories = state.categories[category.catalogId] || [];
    return {
      ...state,
      categories: {
        ...state.categories,
        [category.catalogId]: [...catalogCategories, category]
      },
      loading: false,
      error: null
    };
  }),

  on(CatalogActions.updateCategorySuccess, (state, { category }) => {
    const catalogCategories = state.categories[category.catalogId] || [];
    return {
      ...state,
      categories: {
        ...state.categories,
        [category.catalogId]: catalogCategories.map(c => c.id === category.id ? category : c)
      },
      selectedCategory: state.selectedCategory?.id === category.id ? category : state.selectedCategory,
      loading: false,
      error: null
    };
  }),

  on(CatalogActions.deleteCategorySuccess, (state, { categoryId }) => {
    const updatedCategories = { ...state.categories };
    Object.keys(updatedCategories).forEach(catalogId => {
      updatedCategories[catalogId] = updatedCategories[catalogId].filter(c => c.id !== categoryId);
    });

    return {
      ...state,
      categories: updatedCategories,
      selectedCategory: state.selectedCategory?.id === categoryId ? null : state.selectedCategory,
      selectedCategoryId: state.selectedCategoryId === categoryId ? null : state.selectedCategoryId,
      loading: false,
      error: null
    };
  }),

  on(CatalogActions.reorderCategoriesSuccess, (state, { catalogId, categoryIds }) => {
    const categories = state.categories[catalogId] || [];
    const reorderedCategories = categoryIds.map(id => 
      categories.find(c => c.id === id)!
    ).filter(Boolean);

    return {
      ...state,
      categories: {
        ...state.categories,
        [catalogId]: reorderedCategories
      },
      loading: false,
      error: null
    };
  }),

  // Product reducers
  on(CatalogActions.loadProductsSuccess, (state, { categoryId, products, totalElements }) => ({
    ...state,
    products: {
      ...state.products,
      [categoryId]: { items: products, totalElements }
    },
    loading: false,
    error: null
  })),

  on(CatalogActions.loadProductSuccess, (state, { product }) => ({
    ...state,
    selectedProduct: product,
    loading: false,
    error: null
  })),

  on(CatalogActions.createProductSuccess, (state, { product }) => {
    const categoryProducts = state.products[product.categoryId];
    const updatedProducts = categoryProducts 
      ? { ...categoryProducts, items: [...categoryProducts.items, product] }
      : { items: [product], totalElements: 1 };

    return {
      ...state,
      products: {
        ...state.products,
        [product.categoryId]: updatedProducts
      },
      loading: false,
      error: null
    };
  }),

  on(CatalogActions.updateProductSuccess, (state, { product }) => {
    const categoryProducts = state.products[product.categoryId];
    if (!categoryProducts) return { ...state, loading: false, error: null };

    const updatedProducts = {
      ...categoryProducts,
      items: categoryProducts.items.map(p => p.id === product.id ? product : p)
    };

    return {
      ...state,
      products: {
        ...state.products,
        [product.categoryId]: updatedProducts
      },
      selectedProduct: state.selectedProduct?.id === product.id ? product : state.selectedProduct,
      loading: false,
      error: null
    };
  }),

  on(CatalogActions.deleteProductSuccess, (state, { productId }) => {
    const updatedProducts = { ...state.products };
    Object.keys(updatedProducts).forEach(categoryId => {
      const categoryProducts = updatedProducts[categoryId];
      updatedProducts[categoryId] = {
        ...categoryProducts,
        items: categoryProducts.items.filter(p => p.id !== productId)
      };
    });

    return {
      ...state,
      products: updatedProducts,
      selectedProduct: state.selectedProduct?.id === productId ? null : state.selectedProduct,
      selectedProductId: state.selectedProductId === productId ? null : state.selectedProductId,
      loading: false,
      error: null
    };
  }),

  on(CatalogActions.updateProductAvailabilitySuccess, (state, { product }) => {
    const categoryProducts = state.products[product.categoryId];
    if (!categoryProducts) return { ...state, loading: false, error: null };

    const updatedProducts = {
      ...categoryProducts,
      items: categoryProducts.items.map(p => p.id === product.id ? product : p)
    };

    return {
      ...state,
      products: {
        ...state.products,
        [product.categoryId]: updatedProducts
      },
      selectedProduct: state.selectedProduct?.id === product.id ? product : state.selectedProduct,
      loading: false,
      error: null
    };
  }),

  // Search reducers
  on(CatalogActions.searchProductsSuccess, (state, { products }) => ({
    ...state,
    searchResults: products,
    loading: false,
    error: null
  })),

  // Image upload reducers
  on(CatalogActions.uploadProductImageSuccess, (state, { url }) => ({
    ...state,
    uploadedImageUrls: [...state.uploadedImageUrls, url],
    loading: false,
    error: null
  })),

  on(CatalogActions.deleteProductImageSuccess, (state, { imageUrl }) => ({
    ...state,
    uploadedImageUrls: state.uploadedImageUrls.filter(url => url !== imageUrl),
    loading: false,
    error: null
  })),

  // Import/Export reducers
  on(CatalogActions.importProductsSuccess, (state, { result }) => ({
    ...state,
    importResult: result,
    loading: false,
    error: null
  })),

  // Selection reducers
  on(CatalogActions.setSelectedCatalog, (state, { catalogId }) => ({
    ...state,
    selectedCatalogId: catalogId,
    selectedCatalog: catalogId ? state.catalogs.find(c => c.id === catalogId) || null : null
  })),

  on(CatalogActions.setSelectedCategory, (state, { categoryId }) => {
    let selectedCategory: Category | null = null;
    if (categoryId) {
      Object.values(state.categories).forEach(categories => {
        const found = categories.find(c => c.id === categoryId);
        if (found) selectedCategory = found;
      });
    }

    return {
      ...state,
      selectedCategoryId: categoryId,
      selectedCategory
    };
  }),

  on(CatalogActions.setSelectedProduct, (state, { productId }) => {
    let selectedProduct: Product | null = null;
    if (productId) {
      Object.values(state.products).forEach(categoryProducts => {
        const found = categoryProducts.items.find(p => p.id === productId);
        if (found) selectedProduct = found;
      });
    }

    return {
      ...state,
      selectedProductId: productId,
      selectedProduct
    };
  }),

  on(CatalogActions.clearSelection, (state) => ({
    ...state,
    selectedCatalogId: null,
    selectedCategoryId: null,
    selectedProductId: null,
    selectedCatalog: null,
    selectedCategory: null,
    selectedProduct: null
  })),

  // Error handling for all failure actions
  on(
    CatalogActions.loadCatalogFailure,
    CatalogActions.createCatalogFailure,
    CatalogActions.updateCatalogFailure,
    CatalogActions.deleteCatalogFailure,
    CatalogActions.loadCategoriesFailure,
    CatalogActions.createCategoryFailure,
    CatalogActions.updateCategoryFailure,
    CatalogActions.deleteCategoryFailure,
    CatalogActions.reorderCategoriesFailure,
    CatalogActions.loadProductsFailure,
    CatalogActions.loadProductFailure,
    CatalogActions.createProductFailure,
    CatalogActions.updateProductFailure,
    CatalogActions.deleteProductFailure,
    CatalogActions.updateProductAvailabilityFailure,
    CatalogActions.bulkUpdateProductsFailure,
    CatalogActions.exportProductsFailure,
    CatalogActions.importProductsFailure,
    CatalogActions.uploadProductImageFailure,
    CatalogActions.deleteProductImageFailure,
    CatalogActions.searchProductsFailure,
    (state, { error }) => ({
      ...state,
      loading: false,
      error
    })
  )
);