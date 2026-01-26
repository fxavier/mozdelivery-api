import 'package:json_annotation/json_annotation.dart';
import 'package:equatable/equatable.dart';

part 'auth_models.g.dart';

@JsonSerializable()
class LoginRequest extends Equatable {
  final String email;
  final String password;

  const LoginRequest({
    required this.email,
    required this.password,
  });

  factory LoginRequest.fromJson(Map<String, dynamic> json) =>
      _$LoginRequestFromJson(json);

  Map<String, dynamic> toJson() => _$LoginRequestToJson(this);

  @override
  List<Object?> get props => [email, password];
}

@JsonSerializable()
class CourierRegistrationRequest extends Equatable {
  final String email;
  final String password;
  final String firstName;
  final String lastName;
  final String phoneNumber;
  final String city;
  final String vehicleType;
  final String vehiclePlate;
  final String drivingLicenseNumber;

  const CourierRegistrationRequest({
    required this.email,
    required this.password,
    required this.firstName,
    required this.lastName,
    required this.phoneNumber,
    required this.city,
    required this.vehicleType,
    required this.vehiclePlate,
    required this.drivingLicenseNumber,
  });

  factory CourierRegistrationRequest.fromJson(Map<String, dynamic> json) =>
      _$CourierRegistrationRequestFromJson(json);

  Map<String, dynamic> toJson() => _$CourierRegistrationRequestToJson(this);

  @override
  List<Object?> get props => [
        email,
        password,
        firstName,
        lastName,
        phoneNumber,
        city,
        vehicleType,
        vehiclePlate,
        drivingLicenseNumber,
      ];
}

@JsonSerializable()
class RefreshTokenRequest extends Equatable {
  final String refreshToken;

  const RefreshTokenRequest({required this.refreshToken});

  factory RefreshTokenRequest.fromJson(Map<String, dynamic> json) =>
      _$RefreshTokenRequestFromJson(json);

  Map<String, dynamic> toJson() => _$RefreshTokenRequestToJson(this);

  @override
  List<Object?> get props => [refreshToken];
}

@JsonSerializable()
class AuthResponse extends Equatable {
  final String accessToken;
  final String refreshToken;
  final String courierId;
  final String email;
  final String firstName;
  final String lastName;
  final String role;
  final DateTime expiresAt;

  const AuthResponse({
    required this.accessToken,
    required this.refreshToken,
    required this.courierId,
    required this.email,
    required this.firstName,
    required this.lastName,
    required this.role,
    required this.expiresAt,
  });

  factory AuthResponse.fromJson(Map<String, dynamic> json) =>
      _$AuthResponseFromJson(json);

  Map<String, dynamic> toJson() => _$AuthResponseToJson(this);

  @override
  List<Object?> get props => [
        accessToken,
        refreshToken,
        courierId,
        email,
        firstName,
        lastName,
        role,
        expiresAt,
      ];
}

@JsonSerializable()
class LocationUpdateRequest extends Equatable {
  final double latitude;
  final double longitude;
  final double accuracy;
  final DateTime timestamp;

  const LocationUpdateRequest({
    required this.latitude,
    required this.longitude,
    required this.accuracy,
    required this.timestamp,
  });

  factory LocationUpdateRequest.fromJson(Map<String, dynamic> json) =>
      _$LocationUpdateRequestFromJson(json);

  Map<String, dynamic> toJson() => _$LocationUpdateRequestToJson(this);

  @override
  List<Object?> get props => [latitude, longitude, accuracy, timestamp];
}

@JsonSerializable()
class UpdateAvailabilityRequest extends Equatable {
  final bool isAvailable;
  final String? reason;

  const UpdateAvailabilityRequest({
    required this.isAvailable,
    this.reason,
  });

  factory UpdateAvailabilityRequest.fromJson(Map<String, dynamic> json) =>
      _$UpdateAvailabilityRequestFromJson(json);

  Map<String, dynamic> toJson() => _$UpdateAvailabilityRequestToJson(this);

  @override
  List<Object?> get props => [isAvailable, reason];
}