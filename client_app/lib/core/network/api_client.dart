import 'package:dio/dio.dart';
import 'package:injectable/injectable.dart';

import '../errors/exceptions.dart';

@singleton
class ApiClient {
  final Dio _dio;

  ApiClient(this._dio);

  // Public endpoints (no authentication required)
  Future<List<dynamic>> getMerchants({String? city, String? vertical}) async {
    final queryParams = <String, dynamic>{};
    if (city != null) queryParams['city'] = city;
    if (vertical != null) queryParams['vertical'] = vertical;
    
    final response = await _dio.get('/api/public/merchants', queryParameters: queryParams);
    return response.data;
  }

  Future<dynamic> getMerchant(String merchantId) async {
    final response = await _dio.get('/api/public/merchants/$merchantId');
    return response.data;
  }

  Future<List<dynamic>> getMerchantCatalogs(String merchantId) async {
    final response = await _dio.get('/api/public/merchants/$merchantId/catalogs');
    return response.data;
  }

  Future<List<dynamic>> getCatalogCategories(String catalogId) async {
    final response = await _dio.get('/api/public/catalogs/$catalogId/categories');
    return response.data;
  }

  Future<List<dynamic>> getCategoryProducts(String categoryId) async {
    final response = await _dio.get('/api/public/categories/$categoryId/products');
    return response.data;
  }

  Future<dynamic> getProduct(String productId) async {
    final response = await _dio.get('/api/public/products/$productId');
    return response.data;
  }

  // Guest checkout endpoints
  Future<dynamic> createGuestOrder(Map<String, dynamic> orderData) async {
    final response = await _dio.post('/api/public/orders/guest', data: orderData);
    return response.data;
  }

  Future<dynamic> trackGuestOrder(String trackingToken) async {
    final response = await _dio.get('/api/public/orders/guest/track', queryParameters: {'token': trackingToken});
    return response.data;
  }

  Future<void> resendGuestDeliveryCode(String trackingToken) async {
    await _dio.post('/api/public/orders/guest/resend-code', data: {'token': trackingToken});
  }

  // Authenticated endpoints
  Future<dynamic> login(Map<String, dynamic> credentials) async {
    final response = await _dio.post('/v1/auth/login', data: credentials);
    return response.data;
  }

  Future<dynamic> register(Map<String, dynamic> userData) async {
    final response = await _dio.post('/v1/auth/register', data: userData);
    return response.data;
  }

  Future<dynamic> refreshToken(Map<String, dynamic> tokenData) async {
    final response = await _dio.post('/v1/auth/refresh', data: tokenData);
    return response.data;
  }

  Future<dynamic> getOrder(String orderId) async {
    final response = await _dio.get('/v1/orders/$orderId');
    return response.data;
  }

  Future<List<dynamic>> getClientOrders(String clientId) async {
    final response = await _dio.get('/v1/clients/$clientId/orders');
    return response.data;
  }

  Future<dynamic> createOrder(Map<String, dynamic> orderData) async {
    final response = await _dio.post('/v1/orders', data: orderData);
    return response.data;
  }

  Future<void> cancelOrder(String orderId) async {
    await _dio.put('/v1/orders/$orderId/cancel');
  }
}

@singleton
class ApiService {
  final ApiClient _apiClient;

  ApiService(this._apiClient);

  Future<T> handleApiCall<T>(Future<T> Function() apiCall) async {
    try {
      return await apiCall();
    } on DioException catch (e) {
      throw _handleDioError(e);
    } catch (e) {
      throw ServerException(message: 'Unexpected error: $e');
    }
  }

  // Expose API client methods
  Future<List<dynamic>> getMerchants({String? city, String? vertical}) =>
      handleApiCall(() => _apiClient.getMerchants(city: city, vertical: vertical));

  Future<dynamic> getMerchant(String merchantId) =>
      handleApiCall(() => _apiClient.getMerchant(merchantId));

  Future<dynamic> createGuestOrder(Map<String, dynamic> orderData) =>
      handleApiCall(() => _apiClient.createGuestOrder(orderData));

  Future<dynamic> trackGuestOrder(String trackingToken) =>
      handleApiCall(() => _apiClient.trackGuestOrder(trackingToken));

  Future<void> resendGuestDeliveryCode(String trackingToken) =>
      handleApiCall(() => _apiClient.resendGuestDeliveryCode(trackingToken));

  Future<dynamic> login(Map<String, dynamic> credentials) =>
      handleApiCall(() => _apiClient.login(credentials));

  Future<dynamic> register(Map<String, dynamic> userData) =>
      handleApiCall(() => _apiClient.register(userData));

  Exception _handleDioError(DioException error) {
    switch (error.type) {
      case DioExceptionType.connectionTimeout:
      case DioExceptionType.sendTimeout:
      case DioExceptionType.receiveTimeout:
        return const NetworkException(message: 'Connection timeout');
      case DioExceptionType.connectionError:
        return const NetworkException(message: 'No internet connection');
      case DioExceptionType.badResponse:
        final statusCode = error.response?.statusCode;
        final message = error.response?.data?['message'] ?? 'Server error';
        
        if (statusCode == 401) {
          return AuthenticationException(message: message, statusCode: statusCode);
        } else if (statusCode == 400) {
          return ValidationException(
            message: message,
            errors: error.response?.data?['errors'],
          );
        } else {
          return ServerException(message: message, statusCode: statusCode);
        }
      case DioExceptionType.cancel:
        return const NetworkException(message: 'Request cancelled');
      case DioExceptionType.unknown:
        return NetworkException(message: 'Unknown error: ${error.message}');
      default:
        return ServerException(message: error.message ?? 'Unknown server error');
    }
  }
}