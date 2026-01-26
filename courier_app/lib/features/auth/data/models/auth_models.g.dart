// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'auth_models.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

LoginRequest _$LoginRequestFromJson(Map<String, dynamic> json) => LoginRequest(
      email: json['email'] as String,
      password: json['password'] as String,
    );

Map<String, dynamic> _$LoginRequestToJson(LoginRequest instance) =>
    <String, dynamic>{
      'email': instance.email,
      'password': instance.password,
    };

CourierRegistrationRequest _$CourierRegistrationRequestFromJson(
        Map<String, dynamic> json) =>
    CourierRegistrationRequest(
      email: json['email'] as String,
      password: json['password'] as String,
      firstName: json['firstName'] as String,
      lastName: json['lastName'] as String,
      phoneNumber: json['phoneNumber'] as String,
      city: json['city'] as String,
      vehicleType: json['vehicleType'] as String,
      vehiclePlate: json['vehiclePlate'] as String,
      drivingLicenseNumber: json['drivingLicenseNumber'] as String,
    );

Map<String, dynamic> _$CourierRegistrationRequestToJson(
        CourierRegistrationRequest instance) =>
    <String, dynamic>{
      'email': instance.email,
      'password': instance.password,
      'firstName': instance.firstName,
      'lastName': instance.lastName,
      'phoneNumber': instance.phoneNumber,
      'city': instance.city,
      'vehicleType': instance.vehicleType,
      'vehiclePlate': instance.vehiclePlate,
      'drivingLicenseNumber': instance.drivingLicenseNumber,
    };

RefreshTokenRequest _$RefreshTokenRequestFromJson(Map<String, dynamic> json) =>
    RefreshTokenRequest(
      refreshToken: json['refreshToken'] as String,
    );

Map<String, dynamic> _$RefreshTokenRequestToJson(
        RefreshTokenRequest instance) =>
    <String, dynamic>{
      'refreshToken': instance.refreshToken,
    };

AuthResponse _$AuthResponseFromJson(Map<String, dynamic> json) => AuthResponse(
      accessToken: json['accessToken'] as String,
      refreshToken: json['refreshToken'] as String,
      courierId: json['courierId'] as String,
      email: json['email'] as String,
      firstName: json['firstName'] as String,
      lastName: json['lastName'] as String,
      role: json['role'] as String,
      expiresAt: DateTime.parse(json['expiresAt'] as String),
    );

Map<String, dynamic> _$AuthResponseToJson(AuthResponse instance) =>
    <String, dynamic>{
      'accessToken': instance.accessToken,
      'refreshToken': instance.refreshToken,
      'courierId': instance.courierId,
      'email': instance.email,
      'firstName': instance.firstName,
      'lastName': instance.lastName,
      'role': instance.role,
      'expiresAt': instance.expiresAt.toIso8601String(),
    };

LocationUpdateRequest _$LocationUpdateRequestFromJson(
        Map<String, dynamic> json) =>
    LocationUpdateRequest(
      latitude: (json['latitude'] as num).toDouble(),
      longitude: (json['longitude'] as num).toDouble(),
      accuracy: (json['accuracy'] as num).toDouble(),
      timestamp: DateTime.parse(json['timestamp'] as String),
    );

Map<String, dynamic> _$LocationUpdateRequestToJson(
        LocationUpdateRequest instance) =>
    <String, dynamic>{
      'latitude': instance.latitude,
      'longitude': instance.longitude,
      'accuracy': instance.accuracy,
      'timestamp': instance.timestamp.toIso8601String(),
    };

UpdateAvailabilityRequest _$UpdateAvailabilityRequestFromJson(
        Map<String, dynamic> json) =>
    UpdateAvailabilityRequest(
      isAvailable: json['isAvailable'] as bool,
      reason: json['reason'] as String?,
    );

Map<String, dynamic> _$UpdateAvailabilityRequestToJson(
        UpdateAvailabilityRequest instance) =>
    <String, dynamic>{
      'isAvailable': instance.isAvailable,
      'reason': instance.reason,
    };
