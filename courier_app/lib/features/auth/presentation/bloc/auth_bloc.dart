import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:injectable/injectable.dart';
import 'package:local_auth/local_auth.dart';
import 'package:logger/logger.dart';

import '../../data/models/auth_models.dart';
import '../../../../core/network/api_client.dart';
import '../../../../core/storage/secure_storage_service.dart';
import 'auth_event.dart';
import 'auth_state.dart';

@injectable
class AuthBloc extends Bloc<AuthEvent, AuthState> {
  final ApiClient _apiClient;
  final SecureStorageService _storageService;
  final LocalAuthentication _localAuth;
  final Logger _logger;

  AuthBloc(
    this._apiClient,
    this._storageService,
    this._localAuth,
    this._logger,
  ) : super(const AuthInitial()) {
    on<AuthLoginRequested>(_onLoginRequested);
    on<AuthRegisterRequested>(_onRegisterRequested);
    on<AuthLogoutRequested>(_onLogoutRequested);
    on<AuthTokenRefreshRequested>(_onTokenRefreshRequested);
    on<AuthStatusChecked>(_onStatusChecked);
    on<AuthBiometricLoginRequested>(_onBiometricLoginRequested);
  }

  Future<void> _onLoginRequested(
    AuthLoginRequested event,
    Emitter<AuthState> emit,
  ) async {
    emit(const AuthLoading());

    try {
      final request = LoginRequest(
        email: event.email,
        password: event.password,
      );

      final response = await _apiClient.loginCourier(request);

      // Store authentication data
      await _storageService.saveAccessToken(response.accessToken);
      await _storageService.saveRefreshToken(response.refreshToken);
      await _storageService.saveCourierId(response.courierId);
      await _storageService.saveUserRole(response.role);

      emit(AuthAuthenticated(
        courierId: response.courierId,
        email: response.email,
        firstName: response.firstName,
        lastName: response.lastName,
        accessToken: response.accessToken,
      ));

      _logger.i('Courier login successful: ${response.email}');
    } catch (e) {
      _logger.e('Login failed: $e');
      emit(AuthError(
        message: _getErrorMessage(e),
        errorCode: _getErrorCode(e),
      ));
    }
  }

  Future<void> _onRegisterRequested(
    AuthRegisterRequested event,
    Emitter<AuthState> emit,
  ) async {
    emit(const AuthLoading());

    try {
      final request = CourierRegistrationRequest(
        email: event.email,
        password: event.password,
        firstName: event.firstName,
        lastName: event.lastName,
        phoneNumber: event.phoneNumber,
        city: event.city,
        vehicleType: event.vehicleType,
        vehiclePlate: event.vehiclePlate,
        drivingLicenseNumber: event.drivingLicenseNumber,
      );

      await _apiClient.registerCourier(request);

      emit(const AuthRegistrationSuccess(
        message: 'Registration successful! Please wait for approval.',
      ));

      _logger.i('Courier registration successful: ${event.email}');
    } catch (e) {
      _logger.e('Registration failed: $e');
      emit(AuthError(
        message: _getErrorMessage(e),
        errorCode: _getErrorCode(e),
      ));
    }
  }

  Future<void> _onLogoutRequested(
    AuthLogoutRequested event,
    Emitter<AuthState> emit,
  ) async {
    try {
      await _apiClient.logout();
    } catch (e) {
      _logger.w('Logout API call failed: $e');
    }

    await _storageService.clearAuthData();
    emit(const AuthUnauthenticated());
    _logger.i('Courier logged out');
  }

  Future<void> _onTokenRefreshRequested(
    AuthTokenRefreshRequested event,
    Emitter<AuthState> emit,
  ) async {
    try {
      final refreshToken = await _storageService.getRefreshToken();
      if (refreshToken == null) {
        emit(const AuthUnauthenticated());
        return;
      }

      final request = RefreshTokenRequest(refreshToken: refreshToken);
      final response = await _apiClient.refreshToken(request);

      await _storageService.saveAccessToken(response.accessToken);
      await _storageService.saveRefreshToken(response.refreshToken);

      emit(AuthAuthenticated(
        courierId: response.courierId,
        email: response.email,
        firstName: response.firstName,
        lastName: response.lastName,
        accessToken: response.accessToken,
      ));

      _logger.i('Token refreshed successfully');
    } catch (e) {
      _logger.e('Token refresh failed: $e');
      await _storageService.clearAuthData();
      emit(const AuthUnauthenticated());
    }
  }

  Future<void> _onStatusChecked(
    AuthStatusChecked event,
    Emitter<AuthState> emit,
  ) async {
    try {
      final accessToken = await _storageService.getAccessToken();
      final courierId = await _storageService.getCourierId();

      if (accessToken != null && courierId != null) {
        // TODO: Validate token with backend
        // For now, assume token is valid
        emit(AuthAuthenticated(
          courierId: courierId,
          email: '', // Will be loaded from profile
          firstName: '',
          lastName: '',
          accessToken: accessToken,
        ));
      } else {
        emit(const AuthUnauthenticated());
      }
    } catch (e) {
      _logger.e('Auth status check failed: $e');
      emit(const AuthUnauthenticated());
    }
  }

  Future<void> _onBiometricLoginRequested(
    AuthBiometricLoginRequested event,
    Emitter<AuthState> emit,
  ) async {
    try {
      final isAvailable = await _localAuth.canCheckBiometrics;
      if (!isAvailable) {
        emit(const AuthBiometricUnavailable(
          reason: 'Biometric authentication is not available on this device',
        ));
        return;
      }

      final availableBiometrics = await _localAuth.getAvailableBiometrics();
      if (availableBiometrics.isEmpty) {
        emit(const AuthBiometricUnavailable(
          reason: 'No biometric methods are set up on this device',
        ));
        return;
      }

      final isAuthenticated = await _localAuth.authenticate(
        localizedReason: 'Please authenticate to access the courier app',
        options: const AuthenticationOptions(
          biometricOnly: true,
          stickyAuth: true,
        ),
      );

      if (isAuthenticated) {
        // Check if we have stored credentials
        final accessToken = await _storageService.getAccessToken();
        final courierId = await _storageService.getCourierId();

        if (accessToken != null && courierId != null) {
          emit(AuthAuthenticated(
            courierId: courierId,
            email: '',
            firstName: '',
            lastName: '',
            accessToken: accessToken,
          ));
        } else {
          emit(const AuthUnauthenticated());
        }
      } else {
        emit(const AuthError(message: 'Biometric authentication failed'));
      }
    } catch (e) {
      _logger.e('Biometric authentication error: $e');
      emit(AuthError(message: 'Biometric authentication error: ${e.toString()}'));
    }
  }

  String _getErrorMessage(dynamic error) {
    // TODO: Parse API error responses
    return error.toString();
  }

  String? _getErrorCode(dynamic error) {
    // TODO: Extract error codes from API responses
    return null;
  }
}