class AppConstants {
  // API Configuration
  static const String baseUrl = 'http://localhost:8080/api';
  static const String publicApiPath = '/public';
  static const String v1ApiPath = '/v1';
  
  // Storage Keys
  static const String accessTokenKey = 'access_token';
  static const String refreshTokenKey = 'refresh_token';
  static const String userDataKey = 'user_data';
  static const String guestTokenKey = 'guest_token';
  static const String isGuestModeKey = 'is_guest_mode';
  
  // App Configuration
  static const String appName = 'MozDelivery';
  static const String appVersion = '1.0.0';
  
  // Timeouts
  static const Duration connectionTimeout = Duration(seconds: 30);
  static const Duration receiveTimeout = Duration(seconds: 30);
  
  // Pagination
  static const int defaultPageSize = 20;
  static const int maxPageSize = 100;
  
  // Location
  static const double defaultLocationAccuracy = 100.0;
  static const Duration locationUpdateInterval = Duration(seconds: 30);
  
  // Cache
  static const Duration cacheExpiration = Duration(hours: 1);
  static const int maxCacheSize = 100;
}