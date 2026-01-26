class AppConstants {
  // App Information
  static const String appName = 'Courier App';
  static const String appVersion = '1.0.0';
  
  // API Configuration
  static const String baseUrl = 'http://localhost:8080/api';
  static const Duration connectTimeout = Duration(seconds: 30);
  static const Duration receiveTimeout = Duration(seconds: 30);
  
  // Location Configuration
  static const double locationAccuracyThreshold = 10.0; // meters
  static const Duration locationUpdateInterval = Duration(seconds: 30);
  static const Duration locationTimeout = Duration(seconds: 10);
  
  // Map Configuration
  static const double defaultZoom = 15.0;
  static const double trackingZoom = 18.0;
  static const double routeZoom = 14.0;
  
  // Delivery Configuration
  static const Duration deliveryTimeout = Duration(hours: 2);
  static const int maxDeliveryAttempts = 3;
  static const Duration dccValidationTimeout = Duration(minutes: 5);
  
  // Notification Configuration
  static const String notificationChannelId = 'courier_notifications';
  static const String notificationChannelName = 'Courier Notifications';
  static const String notificationChannelDescription = 'Notifications for courier operations';
  
  // Storage Keys
  static const String onboardingCompletedKey = 'onboarding_completed';
  static const String biometricEnabledKey = 'biometric_enabled';
  static const String notificationsEnabledKey = 'notifications_enabled';
  
  // UI Configuration
  static const Duration animationDuration = Duration(milliseconds: 300);
  static const Duration splashDuration = Duration(seconds: 2);
  
  // Error Messages
  static const String networkErrorMessage = 'Network connection error. Please check your internet connection.';
  static const String locationErrorMessage = 'Unable to access location. Please enable location services.';
  static const String authErrorMessage = 'Authentication failed. Please login again.';
  static const String genericErrorMessage = 'An unexpected error occurred. Please try again.';
  
  // Success Messages
  static const String deliveryAcceptedMessage = 'Delivery accepted successfully';
  static const String deliveryCompletedMessage = 'Delivery completed successfully';
  static const String profileUpdatedMessage = 'Profile updated successfully';
  static const String availabilityUpdatedMessage = 'Availability status updated';
}