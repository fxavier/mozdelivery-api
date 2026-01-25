class ServerException implements Exception {
  final String message;
  final int? statusCode;

  const ServerException({
    required this.message,
    this.statusCode,
  });
}

class NetworkException implements Exception {
  final String message;

  const NetworkException({
    required this.message,
  });
}

class CacheException implements Exception {
  final String message;

  const CacheException({
    required this.message,
  });
}

class AuthenticationException implements Exception {
  final String message;
  final int? statusCode;

  const AuthenticationException({
    required this.message,
    this.statusCode,
  });
}

class ValidationException implements Exception {
  final String message;
  final Map<String, List<String>>? errors;

  const ValidationException({
    required this.message,
    this.errors,
  });
}

class LocationException implements Exception {
  final String message;

  const LocationException({
    required this.message,
  });
}

class PermissionException implements Exception {
  final String message;

  const PermissionException({
    required this.message,
  });
}