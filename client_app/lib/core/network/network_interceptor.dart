import 'package:dio/dio.dart';
import 'package:injectable/injectable.dart';
import 'package:logger/logger.dart';

import '../constants/app_constants.dart';
import '../storage/secure_storage_service.dart';

@singleton
class NetworkInterceptor extends Interceptor {
  final SecureStorageService _secureStorage;
  final Logger _logger = Logger();

  NetworkInterceptor(this._secureStorage);

  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) async {
    // Add authentication token if available and not a public endpoint
    if (!options.path.contains('/public/')) {
      final token = await _secureStorage.getAccessToken();
      if (token != null) {
        options.headers['Authorization'] = 'Bearer $token';
      }
    }

    // Add guest token for guest checkout if available
    if (options.path.contains('/public/orders/guest')) {
      final guestToken = await _secureStorage.getGuestToken();
      if (guestToken != null) {
        options.headers['X-Guest-Token'] = guestToken;
      }
    }

    _logger.d('Request: ${options.method} ${options.path}');
    _logger.d('Headers: ${options.headers}');
    
    super.onRequest(options, handler);
  }

  @override
  void onResponse(Response response, ResponseInterceptorHandler handler) {
    _logger.d('Response: ${response.statusCode} ${response.requestOptions.path}');
    super.onResponse(response, handler);
  }

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) async {
    _logger.e('Error: ${err.response?.statusCode} ${err.requestOptions.path}');
    _logger.e('Error message: ${err.message}');

    // Handle token refresh for 401 errors
    if (err.response?.statusCode == 401 && !err.requestOptions.path.contains('/auth/')) {
      final refreshed = await _refreshToken();
      if (refreshed) {
        // Retry the original request
        final options = err.requestOptions;
        final token = await _secureStorage.getAccessToken();
        if (token != null) {
          options.headers['Authorization'] = 'Bearer $token';
        }
        
        try {
          final dio = Dio();
          final response = await dio.fetch(options);
          handler.resolve(response);
          return;
        } catch (e) {
          _logger.e('Retry failed: $e');
        }
      }
    }

    super.onError(err, handler);
  }

  Future<bool> _refreshToken() async {
    try {
      final refreshToken = await _secureStorage.getRefreshToken();
      if (refreshToken == null) return false;

      final dio = Dio();
      final response = await dio.post(
        '${AppConstants.baseUrl}/v1/auth/refresh',
        data: {'refreshToken': refreshToken},
      );

      if (response.statusCode == 200) {
        final data = response.data;
        await _secureStorage.saveAccessToken(data['accessToken']);
        await _secureStorage.saveRefreshToken(data['refreshToken']);
        return true;
      }
    } catch (e) {
      _logger.e('Token refresh failed: $e');
      await _secureStorage.clearAuthTokens();
    }
    
    return false;
  }
}