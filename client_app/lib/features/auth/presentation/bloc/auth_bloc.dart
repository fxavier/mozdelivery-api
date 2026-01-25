import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';
import 'package:injectable/injectable.dart';

import '../../domain/entities/user.dart';
import '../../../../core/storage/secure_storage_service.dart';
import '../../../../core/network/api_client.dart';
import '../../../../core/errors/failures.dart';

part 'auth_event.dart';
part 'auth_state.dart';

@injectable
class AuthBloc extends Bloc<AuthEvent, AuthState> {
  final ApiService _apiService;
  final SecureStorageService _secureStorage;

  AuthBloc(this._apiService, this._secureStorage) : super(AuthInitial()) {
    on<CheckAuthStatus>(_onCheckAuthStatus);
    on<LoginRequested>(_onLoginRequested);
    on<RegisterRequested>(_onRegisterRequested);
    on<LogoutRequested>(_onLogoutRequested);
    on<StartGuestMode>(_onStartGuestMode);
    on<ConvertGuestToUser>(_onConvertGuestToUser);
  }

  Future<void> _onCheckAuthStatus(
    CheckAuthStatus event,
    Emitter<AuthState> emit,
  ) async {
    emit(AuthLoading());
    
    try {
      final isAuthenticated = await _secureStorage.isAuthenticated();
      final isGuestMode = await _secureStorage.isGuestMode();
      
      if (isAuthenticated) {
        final userData = await _secureStorage.getUserData();
        if (userData != null) {
          // Parse user data and emit authenticated state
          emit(Authenticated(user: _parseUserData(userData)));
        } else {
          emit(Unauthenticated());
        }
      } else if (isGuestMode) {
        final guestToken = await _secureStorage.getGuestToken();
        if (guestToken != null) {
          emit(GuestMode(guestToken: guestToken));
        } else {
          emit(Unauthenticated());
        }
      } else {
        emit(Unauthenticated());
      }
    } catch (e) {
      emit(AuthError(message: 'Failed to check authentication status'));
    }
  }

  Future<void> _onLoginRequested(
    LoginRequested event,
    Emitter<AuthState> emit,
  ) async {
    emit(AuthLoading());
    
    try {
      final response = await _apiService.login({
        'email': event.email,
        'password': event.password,
      });

      // Save tokens and user data
      await _secureStorage.saveAccessToken(response['accessToken']);
      await _secureStorage.saveRefreshToken(response['refreshToken']);
      await _secureStorage.saveUserData(response['user'].toString());
      await _secureStorage.clearGuestData();

      final user = _parseUserData(response['user'].toString());
      emit(Authenticated(user: user));
    } catch (e) {
      if (e is AuthenticationFailure) {
        emit(AuthError(message: e.message));
      } else if (e is NetworkFailure) {
        emit(AuthError(message: 'Network error. Please check your connection.'));
      } else {
        emit(AuthError(message: 'Login failed. Please try again.'));
      }
    }
  }

  Future<void> _onRegisterRequested(
    RegisterRequested event,
    Emitter<AuthState> emit,
  ) async {
    emit(AuthLoading());
    
    try {
      final response = await _apiService.register({
        'name': event.name,
        'email': event.email,
        'password': event.password,
        'phone': event.phone,
        'role': 'CLIENT',
      });

      // Save tokens and user data
      await _secureStorage.saveAccessToken(response['accessToken']);
      await _secureStorage.saveRefreshToken(response['refreshToken']);
      await _secureStorage.saveUserData(response['user'].toString());
      await _secureStorage.clearGuestData();

      final user = _parseUserData(response['user'].toString());
      emit(Authenticated(user: user));
    } catch (e) {
      if (e is ValidationFailure) {
        emit(AuthError(message: e.message));
      } else if (e is NetworkFailure) {
        emit(AuthError(message: 'Network error. Please check your connection.'));
      } else {
        emit(AuthError(message: 'Registration failed. Please try again.'));
      }
    }
  }

  Future<void> _onLogoutRequested(
    LogoutRequested event,
    Emitter<AuthState> emit,
  ) async {
    emit(AuthLoading());
    
    try {
      await _secureStorage.clearAll();
      emit(Unauthenticated());
    } catch (e) {
      emit(AuthError(message: 'Logout failed'));
    }
  }

  Future<void> _onStartGuestMode(
    StartGuestMode event,
    Emitter<AuthState> emit,
  ) async {
    emit(AuthLoading());
    
    try {
      // Generate a guest token (UUID)
      final guestToken = DateTime.now().millisecondsSinceEpoch.toString();
      await _secureStorage.saveGuestToken(guestToken);
      await _secureStorage.setGuestMode(true);
      await _secureStorage.clearAuthTokens();
      
      emit(GuestMode(guestToken: guestToken));
    } catch (e) {
      emit(AuthError(message: 'Failed to start guest mode'));
    }
  }

  Future<void> _onConvertGuestToUser(
    ConvertGuestToUser event,
    Emitter<AuthState> emit,
  ) async {
    emit(AuthLoading());
    
    try {
      // Register the user and convert guest session
      final response = await _apiService.register({
        'name': event.name,
        'email': event.email,
        'password': event.password,
        'phone': event.phone,
        'role': 'CLIENT',
        'guestToken': event.guestToken,
      });

      // Save tokens and user data
      await _secureStorage.saveAccessToken(response['accessToken']);
      await _secureStorage.saveRefreshToken(response['refreshToken']);
      await _secureStorage.saveUserData(response['user'].toString());
      await _secureStorage.clearGuestData();

      final user = _parseUserData(response['user'].toString());
      emit(Authenticated(user: user));
    } catch (e) {
      emit(AuthError(message: 'Failed to convert guest account'));
    }
  }

  User _parseUserData(String userData) {
    // This is a simplified parser - in a real app, you'd use proper JSON parsing
    return const User(
      id: '1',
      email: 'user@example.com',
      name: 'User Name',
      role: UserRole.client,
      createdAt: null,
      updatedAt: null,
    );
  }
}