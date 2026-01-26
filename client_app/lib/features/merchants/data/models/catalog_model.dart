import 'package:equatable/equatable.dart';
import 'package:json_annotation/json_annotation.dart';

part 'catalog_model.g.dart';

@JsonSerializable()
class CatalogModel extends Equatable {
  final String id;
  final String merchantId;
  final String name;
  final String? description;
  final String status;
  final List<CategoryModel>? categories;
  final DateTime createdAt;
  final DateTime updatedAt;

  const CatalogModel({
    required this.id,
    required this.merchantId,
    required this.name,
    this.description,
    required this.status,
    this.categories,
    required this.createdAt,
    required this.updatedAt,
  });

  factory CatalogModel.fromJson(Map<String, dynamic> json) =>
      _$CatalogModelFromJson(json);

  Map<String, dynamic> toJson() => _$CatalogModelToJson(this);

  @override
  List<Object?> get props => [
        id,
        merchantId,
        name,
        description,
        status,
        categories,
        createdAt,
        updatedAt,
      ];
}

@JsonSerializable()
class CategoryModel extends Equatable {
  final String id;
  final String catalogId;
  final String name;
  final String? description;
  final String? imageUrl;
  final bool visible;
  final int sortOrder;
  final List<ProductModel>? products;

  const CategoryModel({
    required this.id,
    required this.catalogId,
    required this.name,
    this.description,
    this.imageUrl,
    required this.visible,
    required this.sortOrder,
    this.products,
  });

  factory CategoryModel.fromJson(Map<String, dynamic> json) =>
      _$CategoryModelFromJson(json);

  Map<String, dynamic> toJson() => _$CategoryModelToJson(this);

  @override
  List<Object?> get props => [
        id,
        catalogId,
        name,
        description,
        imageUrl,
        visible,
        sortOrder,
        products,
      ];
}

@JsonSerializable()
class ProductModel extends Equatable {
  final String id;
  final String merchantId;
  final String categoryId;
  final String name;
  final String? description;
  final List<String>? imageUrls;
  final double price;
  final String currency;
  final String availability;
  final StockInfoModel? stockInfo;
  final List<ProductModifierModel>? modifiers;
  final DateTime createdAt;
  final DateTime updatedAt;

  const ProductModel({
    required this.id,
    required this.merchantId,
    required this.categoryId,
    required this.name,
    this.description,
    this.imageUrls,
    required this.price,
    required this.currency,
    required this.availability,
    this.stockInfo,
    this.modifiers,
    required this.createdAt,
    required this.updatedAt,
  });

  factory ProductModel.fromJson(Map<String, dynamic> json) =>
      _$ProductModelFromJson(json);

  Map<String, dynamic> toJson() => _$ProductModelToJson(this);

  @override
  List<Object?> get props => [
        id,
        merchantId,
        categoryId,
        name,
        description,
        imageUrls,
        price,
        currency,
        availability,
        stockInfo,
        modifiers,
        createdAt,
        updatedAt,
      ];
}

@JsonSerializable()
class StockInfoModel extends Equatable {
  final int? quantity;
  final bool trackStock;
  final int? lowStockThreshold;

  const StockInfoModel({
    this.quantity,
    required this.trackStock,
    this.lowStockThreshold,
  });

  factory StockInfoModel.fromJson(Map<String, dynamic> json) =>
      _$StockInfoModelFromJson(json);

  Map<String, dynamic> toJson() => _$StockInfoModelToJson(this);

  @override
  List<Object?> get props => [quantity, trackStock, lowStockThreshold];
}

@JsonSerializable()
class ProductModifierModel extends Equatable {
  final String id;
  final String name;
  final String type;
  final bool required;
  final List<ModifierOptionModel> options;

  const ProductModifierModel({
    required this.id,
    required this.name,
    required this.type,
    required this.required,
    required this.options,
  });

  factory ProductModifierModel.fromJson(Map<String, dynamic> json) =>
      _$ProductModifierModelFromJson(json);

  Map<String, dynamic> toJson() => _$ProductModifierModelToJson(this);

  @override
  List<Object?> get props => [id, name, type, required, options];
}

@JsonSerializable()
class ModifierOptionModel extends Equatable {
  final String id;
  final String name;
  final double? priceAdjustment;

  const ModifierOptionModel({
    required this.id,
    required this.name,
    this.priceAdjustment,
  });

  factory ModifierOptionModel.fromJson(Map<String, dynamic> json) =>
      _$ModifierOptionModelFromJson(json);

  Map<String, dynamic> toJson() => _$ModifierOptionModelToJson(this);

  @override
  List<Object?> get props => [id, name, priceAdjustment];
}