import 'package:equatable/equatable.dart';

class User extends Equatable {
  final String id;
  final String email;
  final String name;
  final String? phone;
  final UserRole role;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  const User({
    required this.id,
    required this.email,
    required this.name,
    this.phone,
    required this.role,
    this.createdAt,
    this.updatedAt,
  });

  @override
  List<Object?> get props => [id, email, name, phone, role, createdAt, updatedAt];
}

enum UserRole {
  client,
  merchant,
  courier,
  admin,
}

class AuthTokens extends Equatable {
  final String accessToken;
  final String refreshToken;
  final DateTime expiresAt;

  const AuthTokens({
    required this.accessToken,
    required this.refreshToken,
    required this.expiresAt,
  });

  @override
  List<Object> get props => [accessToken, refreshToken, expiresAt];
}

class GuestSession extends Equatable {
  final String guestToken;
  final DateTime createdAt;
  final DateTime expiresAt;

  const GuestSession({
    required this.guestToken,
    required this.createdAt,
    required this.expiresAt,
  });

  @override
  List<Object> get props => [guestToken, createdAt, expiresAt];
}