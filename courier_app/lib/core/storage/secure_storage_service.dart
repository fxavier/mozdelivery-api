import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:injectable/injectable.dart';

@singleton
class SecureStorageService {
  final FlutterSecureStorage _storage;

  SecureStorageService(this._storage);

  // Authentication tokens
  static const String _accessTokenKey = 'access_token';
  static const String _refreshTokenKey = 'refresh_token';
  static const String _courierIdKey = 'courier_id';
  static const String _userRoleKey = 'user_role';

  // Courier profile
  static const String _courierProfileKey = 'courier_profile';
  static const String _availabilityStatusKey = 'availability_status';

  // Location settings
  static const String _locationTrackingKey = 'location_tracking_enabled';
  static const String _lastKnownLocationKey = 'last_known_location';

  // Authentication methods
  Future<void> saveAccessToken(String token) async {
    await _storage.write(key: _accessTokenKey, value: token);
  }

  Future<String?> getAccessToken() async {
    return await _storage.read(key: _accessTokenKey);
  }

  Future<void> saveRefreshToken(String token) async {
    await _storage.write(key: _refreshTokenKey, value: token);
  }

  Future<String?> getRefreshToken() async {
    return await _storage.read(key: _refreshTokenKey);
  }

  Future<void> saveCourierId(String courierId) async {
    await _storage.write(key: _courierIdKey, value: courierId);
  }

  Future<String?> getCourierId() async {
    return await _storage.read(key: _courierIdKey);
  }

  Future<void> saveUserRole(String role) async {
    await _storage.write(key: _userRoleKey, value: role);
  }

  Future<String?> getUserRole() async {
    return await _storage.read(key: _userRoleKey);
  }

  // Courier profile methods
  Future<void> saveCourierProfile(String profileJson) async {
    await _storage.write(key: _courierProfileKey, value: profileJson);
  }

  Future<String?> getCourierProfile() async {
    return await _storage.read(key: _courierProfileKey);
  }

  Future<void> saveAvailabilityStatus(bool isAvailable) async {
    await _storage.write(key: _availabilityStatusKey, value: isAvailable.toString());
  }

  Future<bool> getAvailabilityStatus() async {
    final status = await _storage.read(key: _availabilityStatusKey);
    return status == 'true';
  }

  // Location methods
  Future<void> saveLocationTrackingEnabled(bool enabled) async {
    await _storage.write(key: _locationTrackingKey, value: enabled.toString());
  }

  Future<bool> getLocationTrackingEnabled() async {
    final enabled = await _storage.read(key: _locationTrackingKey);
    return enabled == 'true';
  }

  Future<void> saveLastKnownLocation(String locationJson) async {
    await _storage.write(key: _lastKnownLocationKey, value: locationJson);
  }

  Future<String?> getLastKnownLocation() async {
    return await _storage.read(key: _lastKnownLocationKey);
  }

  // Clear all data
  Future<void> clearAll() async {
    await _storage.deleteAll();
  }

  // Clear authentication data only
  Future<void> clearAuthData() async {
    await _storage.delete(key: _accessTokenKey);
    await _storage.delete(key: _refreshTokenKey);
    await _storage.delete(key: _courierIdKey);
    await _storage.delete(key: _userRoleKey);
  }
}