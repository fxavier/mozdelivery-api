import 'package:json_annotation/json_annotation.dart';
import 'package:equatable/equatable.dart';

part 'courier_profile_models.g.dart';

@JsonSerializable()
class CourierProfileResponse extends Equatable {
  final String courierId;
  final String email;
  final String firstName;
  final String lastName;
  final String phoneNumber;
  final String city;
  final VehicleInfo vehicleInfo;
  final AvailabilityInfo availabilityInfo;
  final CourierStats stats;
  final DateTime createdAt;
  final DateTime updatedAt;

  const CourierProfileResponse({
    required this.courierId,
    required this.email,
    required this.firstName,
    required this.lastName,
    required this.phoneNumber,
    required this.city,
    required this.vehicleInfo,
    required this.availabilityInfo,
    required this.stats,
    required this.createdAt,
    required this.updatedAt,
  });

  factory CourierProfileResponse.fromJson(Map<String, dynamic> json) =>
      _$CourierProfileResponseFromJson(json);

  Map<String, dynamic> toJson() => _$CourierProfileResponseToJson(this);

  @override
  List<Object?> get props => [
        courierId,
        email,
        firstName,
        lastName,
        phoneNumber,
        city,
        vehicleInfo,
        availabilityInfo,
        stats,
        createdAt,
        updatedAt,
      ];
}

@JsonSerializable()
class VehicleInfo extends Equatable {
  final String vehicleType;
  final String vehiclePlate;
  final String drivingLicenseNumber;
  final String? vehicleModel;
  final String? vehicleColor;

  const VehicleInfo({
    required this.vehicleType,
    required this.vehiclePlate,
    required this.drivingLicenseNumber,
    this.vehicleModel,
    this.vehicleColor,
  });

  factory VehicleInfo.fromJson(Map<String, dynamic> json) =>
      _$VehicleInfoFromJson(json);

  Map<String, dynamic> toJson() => _$VehicleInfoToJson(this);

  @override
  List<Object?> get props => [
        vehicleType,
        vehiclePlate,
        drivingLicenseNumber,
        vehicleModel,
        vehicleColor,
      ];
}

@JsonSerializable()
class AvailabilityInfo extends Equatable {
  final bool isAvailable;
  final String status;
  final DateTime? lastStatusUpdate;
  final List<AvailabilitySchedule> schedule;

  const AvailabilityInfo({
    required this.isAvailable,
    required this.status,
    this.lastStatusUpdate,
    required this.schedule,
  });

  factory AvailabilityInfo.fromJson(Map<String, dynamic> json) =>
      _$AvailabilityInfoFromJson(json);

  Map<String, dynamic> toJson() => _$AvailabilityInfoToJson(this);

  @override
  List<Object?> get props => [isAvailable, status, lastStatusUpdate, schedule];
}

@JsonSerializable()
class AvailabilitySchedule extends Equatable {
  final String dayOfWeek;
  final String startTime;
  final String endTime;
  final bool isActive;

  const AvailabilitySchedule({
    required this.dayOfWeek,
    required this.startTime,
    required this.endTime,
    required this.isActive,
  });

  factory AvailabilitySchedule.fromJson(Map<String, dynamic> json) =>
      _$AvailabilityScheduleFromJson(json);

  Map<String, dynamic> toJson() => _$AvailabilityScheduleToJson(this);

  @override
  List<Object?> get props => [dayOfWeek, startTime, endTime, isActive];
}

@JsonSerializable()
class CourierStats extends Equatable {
  final int totalDeliveries;
  final int completedDeliveries;
  final int cancelledDeliveries;
  final double averageRating;
  final int totalRatings;
  final double totalEarnings;
  final int deliveriesToday;
  final int deliveriesThisWeek;
  final int deliveriesThisMonth;

  const CourierStats({
    required this.totalDeliveries,
    required this.completedDeliveries,
    required this.cancelledDeliveries,
    required this.averageRating,
    required this.totalRatings,
    required this.totalEarnings,
    required this.deliveriesToday,
    required this.deliveriesThisWeek,
    required this.deliveriesThisMonth,
  });

  factory CourierStats.fromJson(Map<String, dynamic> json) =>
      _$CourierStatsFromJson(json);

  Map<String, dynamic> toJson() => _$CourierStatsToJson(this);

  @override
  List<Object?> get props => [
        totalDeliveries,
        completedDeliveries,
        cancelledDeliveries,
        averageRating,
        totalRatings,
        totalEarnings,
        deliveriesToday,
        deliveriesThisWeek,
        deliveriesThisMonth,
      ];
}

@JsonSerializable()
class UpdateCourierProfileRequest extends Equatable {
  final String? firstName;
  final String? lastName;
  final String? phoneNumber;
  final String? city;
  final VehicleInfo? vehicleInfo;

  const UpdateCourierProfileRequest({
    this.firstName,
    this.lastName,
    this.phoneNumber,
    this.city,
    this.vehicleInfo,
  });

  factory UpdateCourierProfileRequest.fromJson(Map<String, dynamic> json) =>
      _$UpdateCourierProfileRequestFromJson(json);

  Map<String, dynamic> toJson() => _$UpdateCourierProfileRequestToJson(this);

  @override
  List<Object?> get props => [firstName, lastName, phoneNumber, city, vehicleInfo];
}