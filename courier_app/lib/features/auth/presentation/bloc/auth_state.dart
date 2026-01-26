import 'package:equatable/equatable.dart';

abstract class AuthState extends Equatable {
  const AuthState();

  @override
  List<Object?> get props => [];
}

class AuthInitial extends AuthState {
  const AuthInitial();
}

class AuthLoading extends AuthState {
  const AuthLoading();
}

class AuthAuthenticated extends AuthState {
  final String courierId;
  final String email;
  final String firstName;
  final String lastName;
  final String accessToken;

  const AuthAuthenticated({
    required this.courierId,
    required this.email,
    required this.firstName,
    required this.lastName,
    required this.accessToken,
  });

  @override
  List<Object?> get props => [courierId, email, firstName, lastName, accessToken];
}

class AuthUnauthenticated extends AuthState {
  const AuthUnauthenticated();
}

class AuthError extends AuthState {
  final String message;
  final String? errorCode;

  const AuthError({
    required this.message,
    this.errorCode,
  });

  @override
  List<Object?> get props => [message, errorCode];
}

class AuthRegistrationSuccess extends AuthState {
  final String message;

  const AuthRegistrationSuccess({required this.message});

  @override
  List<Object?> get props => [message];
}

class AuthBiometricAvailable extends AuthState {
  const AuthBiometricAvailable();
}

class AuthBiometricUnavailable extends AuthState {
  final String reason;

  const AuthBiometricUnavailable({required this.reason});

  @override
  List<Object?> get props => [reason];
}