// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'courier_profile_models.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

CourierProfileResponse _$CourierProfileResponseFromJson(
        Map<String, dynamic> json) =>
    CourierProfileResponse(
      courierId: json['courierId'] as String,
      email: json['email'] as String,
      firstName: json['firstName'] as String,
      lastName: json['lastName'] as String,
      phoneNumber: json['phoneNumber'] as String,
      city: json['city'] as String,
      vehicleInfo: VehicleInfo.fromJson(json['vehicleInfo'] as Map<String, dynamic>),
      availabilityInfo: AvailabilityInfo.fromJson(json['availabilityInfo'] as Map<String, dynamic>),
      stats: CourierStats.fromJson(json['stats'] as Map<String, dynamic>),
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: DateTime.parse(json['updatedAt'] as String),
    );

Map<String, dynamic> _$CourierProfileResponseToJson(
        CourierProfileResponse instance) =>
    <String, dynamic>{
      'courierId': instance.courierId,
      'email': instance.email,
      'firstName': instance.firstName,
      'lastName': instance.lastName,
      'phoneNumber': instance.phoneNumber,
      'city': instance.city,
      'vehicleInfo': instance.vehicleInfo,
      'availabilityInfo': instance.availabilityInfo,
      'stats': instance.stats,
      'createdAt': instance.createdAt.toIso8601String(),
      'updatedAt': instance.updatedAt.toIso8601String(),
    };

VehicleInfo _$VehicleInfoFromJson(Map<String, dynamic> json) => VehicleInfo(
      vehicleType: json['vehicleType'] as String,
      vehiclePlate: json['vehiclePlate'] as String,
      drivingLicenseNumber: json['drivingLicenseNumber'] as String,
      vehicleModel: json['vehicleModel'] as String?,
      vehicleColor: json['vehicleColor'] as String?,
    );

Map<String, dynamic> _$VehicleInfoToJson(VehicleInfo instance) =>
    <String, dynamic>{
      'vehicleType': instance.vehicleType,
      'vehiclePlate': instance.vehiclePlate,
      'drivingLicenseNumber': instance.drivingLicenseNumber,
      'vehicleModel': instance.vehicleModel,
      'vehicleColor': instance.vehicleColor,
    };

AvailabilityInfo _$AvailabilityInfoFromJson(Map<String, dynamic> json) =>
    AvailabilityInfo(
      isAvailable: json['isAvailable'] as bool,
      status: json['status'] as String,
      lastStatusUpdate: json['lastStatusUpdate'] == null
          ? null
          : DateTime.parse(json['lastStatusUpdate'] as String),
      schedule: (json['schedule'] as List<dynamic>)
          .map((e) => AvailabilitySchedule.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$AvailabilityInfoToJson(AvailabilityInfo instance) =>
    <String, dynamic>{
      'isAvailable': instance.isAvailable,
      'status': instance.status,
      'lastStatusUpdate': instance.lastStatusUpdate?.toIso8601String(),
      'schedule': instance.schedule,
    };

AvailabilitySchedule _$AvailabilityScheduleFromJson(
        Map<String, dynamic> json) =>
    AvailabilitySchedule(
      dayOfWeek: json['dayOfWeek'] as String,
      startTime: json['startTime'] as String,
      endTime: json['endTime'] as String,
      isActive: json['isActive'] as bool,
    );

Map<String, dynamic> _$AvailabilityScheduleToJson(
        AvailabilitySchedule instance) =>
    <String, dynamic>{
      'dayOfWeek': instance.dayOfWeek,
      'startTime': instance.startTime,
      'endTime': instance.endTime,
      'isActive': instance.isActive,
    };

CourierStats _$CourierStatsFromJson(Map<String, dynamic> json) =>
    CourierStats(
      totalDeliveries: json['totalDeliveries'] as int,
      completedDeliveries: json['completedDeliveries'] as int,
      cancelledDeliveries: json['cancelledDeliveries'] as int,
      averageRating: (json['averageRating'] as num).toDouble(),
      totalRatings: json['totalRatings'] as int,
      totalEarnings: (json['totalEarnings'] as num).toDouble(),
      deliveriesToday: json['deliveriesToday'] as int,
      deliveriesThisWeek: json['deliveriesThisWeek'] as int,
      deliveriesThisMonth: json['deliveriesThisMonth'] as int,
    );

Map<String, dynamic> _$CourierStatsToJson(CourierStats instance) =>
    <String, dynamic>{
      'totalDeliveries': instance.totalDeliveries,
      'completedDeliveries': instance.completedDeliveries,
      'cancelledDeliveries': instance.cancelledDeliveries,
      'averageRating': instance.averageRating,
      'totalRatings': instance.totalRatings,
      'totalEarnings': instance.totalEarnings,
      'deliveriesToday': instance.deliveriesToday,
      'deliveriesThisWeek': instance.deliveriesThisWeek,
      'deliveriesThisMonth': instance.deliveriesThisMonth,
    };

UpdateCourierProfileRequest _$UpdateCourierProfileRequestFromJson(
        Map<String, dynamic> json) =>
    UpdateCourierProfileRequest(
      firstName: json['firstName'] as String?,
      lastName: json['lastName'] as String?,
      phoneNumber: json['phoneNumber'] as String?,
      city: json['city'] as String?,
      vehicleInfo: json['vehicleInfo'] == null
          ? null
          : VehicleInfo.fromJson(json['vehicleInfo'] as Map<String, dynamic>),
    );

Map<String, dynamic> _$UpdateCourierProfileRequestToJson(
        UpdateCourierProfileRequest instance) =>
    <String, dynamic>{
      'firstName': instance.firstName,
      'lastName': instance.lastName,
      'phoneNumber': instance.phoneNumber,
      'city': instance.city,
      'vehicleInfo': instance.vehicleInfo,
    };