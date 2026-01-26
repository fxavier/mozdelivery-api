import 'package:equatable/equatable.dart';

abstract class MerchantDetailEvent extends Equatable {
  const MerchantDetailEvent();

  @override
  List<Object?> get props => [];
}

class LoadMerchantDetail extends MerchantDetailEvent {
  final String merchantId;

  const LoadMerchantDetail({required this.merchantId});

  @override
  List<Object?> get props => [merchantId];
}

class LoadMerchantCatalogs extends MerchantDetailEvent {
  final String merchantId;

  const LoadMerchantCatalogs({required this.merchantId});

  @override
  List<Object?> get props => [merchantId];
}

class LoadCatalogCategories extends MerchantDetailEvent {
  final String catalogId;

  const LoadCatalogCategories({required this.catalogId});

  @override
  List<Object?> get props => [catalogId];
}

class LoadCategoryProducts extends MerchantDetailEvent {
  final String categoryId;

  const LoadCategoryProducts({required this.categoryId});

  @override
  List<Object?> get props => [categoryId];
}

class SearchProducts extends MerchantDetailEvent {
  final String query;
  final String? categoryId;

  const SearchProducts({
    required this.query,
    this.categoryId,
  });

  @override
  List<Object?> get props => [query, categoryId];
}

class ClearProductSearch extends MerchantDetailEvent {
  const ClearProductSearch();
}

class RefreshMerchantDetail extends MerchantDetailEvent {
  const RefreshMerchantDetail();
}