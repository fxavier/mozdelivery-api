import 'package:equatable/equatable.dart';

import '../../data/models/merchant_model.dart';
import '../../data/models/catalog_model.dart';

abstract class MerchantDetailState extends Equatable {
  const MerchantDetailState();

  @override
  List<Object?> get props => [];
}

class MerchantDetailInitial extends MerchantDetailState {
  const MerchantDetailInitial();
}

class MerchantDetailLoading extends MerchantDetailState {
  const MerchantDetailLoading();
}

class MerchantDetailLoaded extends MerchantDetailState {
  final MerchantModel merchant;
  final List<CatalogModel> catalogs;
  final List<CategoryModel> categories;
  final List<ProductModel> products;
  final List<ProductModel> filteredProducts;
  final String? selectedCatalogId;
  final String? selectedCategoryId;
  final String? productSearchQuery;
  final bool isSearchingProducts;

  const MerchantDetailLoaded({
    required this.merchant,
    this.catalogs = const [],
    this.categories = const [],
    this.products = const [],
    this.filteredProducts = const [],
    this.selectedCatalogId,
    this.selectedCategoryId,
    this.productSearchQuery,
    this.isSearchingProducts = false,
  });

  @override
  List<Object?> get props => [
        merchant,
        catalogs,
        categories,
        products,
        filteredProducts,
        selectedCatalogId,
        selectedCategoryId,
        productSearchQuery,
        isSearchingProducts,
      ];

  MerchantDetailLoaded copyWith({
    MerchantModel? merchant,
    List<CatalogModel>? catalogs,
    List<CategoryModel>? categories,
    List<ProductModel>? products,
    List<ProductModel>? filteredProducts,
    String? selectedCatalogId,
    String? selectedCategoryId,
    String? productSearchQuery,
    bool? isSearchingProducts,
  }) {
    return MerchantDetailLoaded(
      merchant: merchant ?? this.merchant,
      catalogs: catalogs ?? this.catalogs,
      categories: categories ?? this.categories,
      products: products ?? this.products,
      filteredProducts: filteredProducts ?? this.filteredProducts,
      selectedCatalogId: selectedCatalogId ?? this.selectedCatalogId,
      selectedCategoryId: selectedCategoryId ?? this.selectedCategoryId,
      productSearchQuery: productSearchQuery ?? this.productSearchQuery,
      isSearchingProducts: isSearchingProducts ?? this.isSearchingProducts,
    );
  }
}

class MerchantDetailError extends MerchantDetailState {
  final String message;

  const MerchantDetailError({required this.message});

  @override
  List<Object?> get props => [message];
}