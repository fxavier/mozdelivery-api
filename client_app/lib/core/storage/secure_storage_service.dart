import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:injectable/injectable.dart';

import '../constants/app_constants.dart';

@singleton
class SecureStorageService {
  final FlutterSecureStorage _secureStorage;

  SecureStorageService(this._secureStorage);

  // Authentication tokens
  Future<void> saveAccessToken(String token) async {
    await _secureStorage.write(key: AppConstants.accessTokenKey, value: token);
  }

  Future<String?> getAccessToken() async {
    return await _secureStorage.read(key: AppConstants.accessTokenKey);
  }

  Future<void> saveRefreshToken(String token) async {
    await _secureStorage.write(key: AppConstants.refreshTokenKey, value: token);
  }

  Future<String?> getRefreshToken() async {
    return await _secureStorage.read(key: AppConstants.refreshTokenKey);
  }

  Future<void> clearAuthTokens() async {
    await _secureStorage.delete(key: AppConstants.accessTokenKey);
    await _secureStorage.delete(key: AppConstants.refreshTokenKey);
    await _secureStorage.delete(key: AppConstants.userDataKey);
  }

  // Guest mode
  Future<void> saveGuestToken(String token) async {
    await _secureStorage.write(key: AppConstants.guestTokenKey, value: token);
  }

  Future<String?> getGuestToken() async {
    return await _secureStorage.read(key: AppConstants.guestTokenKey);
  }

  Future<void> setGuestMode(bool isGuest) async {
    await _secureStorage.write(
      key: AppConstants.isGuestModeKey,
      value: isGuest.toString(),
    );
  }

  Future<bool> isGuestMode() async {
    final value = await _secureStorage.read(key: AppConstants.isGuestModeKey);
    return value == 'true';
  }

  Future<void> clearGuestData() async {
    await _secureStorage.delete(key: AppConstants.guestTokenKey);
    await _secureStorage.delete(key: AppConstants.isGuestModeKey);
  }

  // User data
  Future<void> saveUserData(String userData) async {
    await _secureStorage.write(key: AppConstants.userDataKey, value: userData);
  }

  Future<String?> getUserData() async {
    return await _secureStorage.read(key: AppConstants.userDataKey);
  }

  // Clear all data
  Future<void> clearAll() async {
    await _secureStorage.deleteAll();
  }

  // Check if user is authenticated
  Future<bool> isAuthenticated() async {
    final token = await getAccessToken();
    return token != null && token.isNotEmpty;
  }
}