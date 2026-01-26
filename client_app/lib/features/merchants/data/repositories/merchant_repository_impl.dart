import 'package:injectable/injectable.dart';
import 'package:dio/dio.dart';

import '../../../../core/network/api_client.dart';
import '../../../../core/di/injection.dart';
import '../../domain/repositories/merchant_repository.dart';
import '../models/merchant_model.dart';
import '../models/catalog_model.dart';

@LazySingleton(as: MerchantRepository)
class MerchantRepositoryImpl implements MerchantRepository {
  final ApiService _apiService;

  MerchantRepositoryImpl(this._apiService);

  @override
  Future<List<MerchantModel>> getMerchants({
    String? city,
    String? vertical,
  }) async {
    final response = await _apiService.getMerchants(city: city, vertical: vertical);
    return response
        .map((json) => MerchantModel.fromJson(json))
        .toList();
  }

  @override
  Future<MerchantModel> getMerchant(String merchantId) async {
    final response = await _apiService.getMerchant(merchantId);
    return MerchantModel.fromJson(response as Map<String, dynamic>);
  }

  @override
  Future<List<CatalogModel>> getMerchantCatalogs(String merchantId) async {
    final response = await _apiService.handleApiCall(
      () async {
        final dio = getIt<Dio>();
        final result = await dio.get('/api/public/merchants/$merchantId/catalogs');
        return result.data;
      },
    );
    return (response as List)
        .map((json) => CatalogModel.fromJson(json as Map<String, dynamic>))
        .toList();
  }

  @override
  Future<CatalogModel> getCatalog(String catalogId) async {
    final response = await _apiService.handleApiCall(
      () async {
        final dio = getIt<Dio>();
        final result = await dio.get('/api/public/catalogs/$catalogId');
        return result.data;
      },
    );
    return CatalogModel.fromJson(response as Map<String, dynamic>);
  }

  @override
  Future<List<CategoryModel>> getCatalogCategories(String catalogId) async {
    final response = await _apiService.handleApiCall(
      () async {
        final dio = getIt<Dio>();
        final result = await dio.get('/api/public/catalogs/$catalogId/categories');
        return result.data;
      },
    );
    return (response as List)
        .map((json) => CategoryModel.fromJson(json as Map<String, dynamic>))
        .toList();
  }

  @override
  Future<CategoryModel> getCategory(String categoryId) async {
    final response = await _apiService.handleApiCall(
      () async {
        final dio = getIt<Dio>();
        final result = await dio.get('/api/public/categories/$categoryId');
        return result.data;
      },
    );
    return CategoryModel.fromJson(response as Map<String, dynamic>);
  }

  @override
  Future<List<ProductModel>> getCategoryProducts(String categoryId) async {
    final response = await _apiService.handleApiCall(
      () async {
        final dio = getIt<Dio>();
        final result = await dio.get('/api/public/categories/$categoryId/products');
        return result.data;
      },
    );
    return (response as List)
        .map((json) => ProductModel.fromJson(json as Map<String, dynamic>))
        .toList();
  }

  @override
  Future<ProductModel> getProduct(String productId) async {
    final response = await _apiService.handleApiCall(
      () async {
        final dio = getIt<Dio>();
        final result = await dio.get('/api/public/products/$productId');
        return result.data;
      },
    );
    return ProductModel.fromJson(response as Map<String, dynamic>);
  }

  @override
  Future<List<MerchantModel>> searchMerchants({
    required String query,
    String? city,
    String? vertical,
  }) async {
    // Get all merchants with filters and then filter by query locally
    // In a real implementation, this would be done server-side
    final merchants = await getMerchants(city: city, vertical: vertical);
    
    if (query.isEmpty) return merchants;
    
    final lowercaseQuery = query.toLowerCase();
    return merchants.where((merchant) {
      return merchant.businessName.toLowerCase().contains(lowercaseQuery) ||
             merchant.displayName.toLowerCase().contains(lowercaseQuery) ||
             (merchant.description?.toLowerCase().contains(lowercaseQuery) ?? false);
    }).toList();
  }

  @override
  Future<List<ProductModel>> searchProducts({
    required String query,
    String? merchantId,
    String? categoryId,
  }) async {
    // This is a simplified implementation
    // In a real app, this would be a dedicated search API
    if (categoryId != null) {
      final products = await getCategoryProducts(categoryId);
      if (query.isEmpty) return products;
      
      final lowercaseQuery = query.toLowerCase();
      return products.where((product) {
        return product.name.toLowerCase().contains(lowercaseQuery) ||
               (product.description?.toLowerCase().contains(lowercaseQuery) ?? false);
      }).toList();
    }
    
    // For now, return empty list if no category specified
    // In a real implementation, this would search across all products
    return [];
  }
}