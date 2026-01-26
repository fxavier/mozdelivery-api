// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'catalog_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

CatalogModel _$CatalogModelFromJson(Map<String, dynamic> json) => CatalogModel(
      id: json['id'] as String,
      merchantId: json['merchantId'] as String,
      name: json['name'] as String,
      description: json['description'] as String?,
      status: json['status'] as String,
      categories: (json['categories'] as List<dynamic>?)
          ?.map((e) => CategoryModel.fromJson(e as Map<String, dynamic>))
          .toList(),
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: DateTime.parse(json['updatedAt'] as String),
    );

Map<String, dynamic> _$CatalogModelToJson(CatalogModel instance) =>
    <String, dynamic>{
      'id': instance.id,
      'merchantId': instance.merchantId,
      'name': instance.name,
      'description': instance.description,
      'status': instance.status,
      'categories': instance.categories,
      'createdAt': instance.createdAt.toIso8601String(),
      'updatedAt': instance.updatedAt.toIso8601String(),
    };

CategoryModel _$CategoryModelFromJson(Map<String, dynamic> json) =>
    CategoryModel(
      id: json['id'] as String,
      catalogId: json['catalogId'] as String,
      name: json['name'] as String,
      description: json['description'] as String?,
      imageUrl: json['imageUrl'] as String?,
      visible: json['visible'] as bool,
      sortOrder: (json['sortOrder'] as num).toInt(),
      products: (json['products'] as List<dynamic>?)
          ?.map((e) => ProductModel.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$CategoryModelToJson(CategoryModel instance) =>
    <String, dynamic>{
      'id': instance.id,
      'catalogId': instance.catalogId,
      'name': instance.name,
      'description': instance.description,
      'imageUrl': instance.imageUrl,
      'visible': instance.visible,
      'sortOrder': instance.sortOrder,
      'products': instance.products,
    };

ProductModel _$ProductModelFromJson(Map<String, dynamic> json) => ProductModel(
      id: json['id'] as String,
      merchantId: json['merchantId'] as String,
      categoryId: json['categoryId'] as String,
      name: json['name'] as String,
      description: json['description'] as String?,
      imageUrls: (json['imageUrls'] as List<dynamic>?)
          ?.map((e) => e as String)
          .toList(),
      price: (json['price'] as num).toDouble(),
      currency: json['currency'] as String,
      availability: json['availability'] as String,
      stockInfo: json['stockInfo'] == null
          ? null
          : StockInfoModel.fromJson(json['stockInfo'] as Map<String, dynamic>),
      modifiers: (json['modifiers'] as List<dynamic>?)
          ?.map((e) => ProductModifierModel.fromJson(e as Map<String, dynamic>))
          .toList(),
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: DateTime.parse(json['updatedAt'] as String),
    );

Map<String, dynamic> _$ProductModelToJson(ProductModel instance) =>
    <String, dynamic>{
      'id': instance.id,
      'merchantId': instance.merchantId,
      'categoryId': instance.categoryId,
      'name': instance.name,
      'description': instance.description,
      'imageUrls': instance.imageUrls,
      'price': instance.price,
      'currency': instance.currency,
      'availability': instance.availability,
      'stockInfo': instance.stockInfo,
      'modifiers': instance.modifiers,
      'createdAt': instance.createdAt.toIso8601String(),
      'updatedAt': instance.updatedAt.toIso8601String(),
    };

StockInfoModel _$StockInfoModelFromJson(Map<String, dynamic> json) =>
    StockInfoModel(
      quantity: (json['quantity'] as num?)?.toInt(),
      trackStock: json['trackStock'] as bool,
      lowStockThreshold: (json['lowStockThreshold'] as num?)?.toInt(),
    );

Map<String, dynamic> _$StockInfoModelToJson(StockInfoModel instance) =>
    <String, dynamic>{
      'quantity': instance.quantity,
      'trackStock': instance.trackStock,
      'lowStockThreshold': instance.lowStockThreshold,
    };

ProductModifierModel _$ProductModifierModelFromJson(
        Map<String, dynamic> json) =>
    ProductModifierModel(
      id: json['id'] as String,
      name: json['name'] as String,
      type: json['type'] as String,
      required: json['required'] as bool,
      options: (json['options'] as List<dynamic>)
          .map((e) => ModifierOptionModel.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$ProductModifierModelToJson(
        ProductModifierModel instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'type': instance.type,
      'required': instance.required,
      'options': instance.options,
    };

ModifierOptionModel _$ModifierOptionModelFromJson(Map<String, dynamic> json) =>
    ModifierOptionModel(
      id: json['id'] as String,
      name: json['name'] as String,
      priceAdjustment: (json['priceAdjustment'] as num?)?.toDouble(),
    );

Map<String, dynamic> _$ModifierOptionModelToJson(
        ModifierOptionModel instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'priceAdjustment': instance.priceAdjustment,
    };
