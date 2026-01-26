import 'package:json_annotation/json_annotation.dart';
import 'package:equatable/equatable.dart';

part 'delivery_models.g.dart';

@JsonSerializable()
class DeliveryAssignmentResponse extends Equatable {
  final String deliveryId;
  final String orderId;
  final String merchantName;
  final String merchantAddress;
  final String customerName;
  final String customerPhone;
  final String deliveryAddress;
  final double deliveryLatitude;
  final double deliveryLongitude;
  final String status;
  final double totalAmount;
  final String currency;
  final DateTime estimatedPickupTime;
  final DateTime estimatedDeliveryTime;
  final String? specialInstructions;
  final List<OrderItem> items;

  const DeliveryAssignmentResponse({
    required this.deliveryId,
    required this.orderId,
    required this.merchantName,
    required this.merchantAddress,
    required this.customerName,
    required this.customerPhone,
    required this.deliveryAddress,
    required this.deliveryLatitude,
    required this.deliveryLongitude,
    required this.status,
    required this.totalAmount,
    required this.currency,
    required this.estimatedPickupTime,
    required this.estimatedDeliveryTime,
    this.specialInstructions,
    required this.items,
  });

  factory DeliveryAssignmentResponse.fromJson(Map<String, dynamic> json) =>
      _$DeliveryAssignmentResponseFromJson(json);

  Map<String, dynamic> toJson() => _$DeliveryAssignmentResponseToJson(this);

  @override
  List<Object?> get props => [
        deliveryId,
        orderId,
        merchantName,
        merchantAddress,
        customerName,
        customerPhone,
        deliveryAddress,
        deliveryLatitude,
        deliveryLongitude,
        status,
        totalAmount,
        currency,
        estimatedPickupTime,
        estimatedDeliveryTime,
        specialInstructions,
        items,
      ];
}

@JsonSerializable()
class DeliveryResponse extends Equatable {
  final String deliveryId;
  final String orderId;
  final String courierId;
  final String status;
  final DateTime? acceptedAt;
  final DateTime? pickedUpAt;
  final DateTime? deliveredAt;
  final String? cancellationReason;
  final DeliveryRoute? route;
  final List<DeliveryEvent> events;

  const DeliveryResponse({
    required this.deliveryId,
    required this.orderId,
    required this.courierId,
    required this.status,
    this.acceptedAt,
    this.pickedUpAt,
    this.deliveredAt,
    this.cancellationReason,
    this.route,
    required this.events,
  });

  factory DeliveryResponse.fromJson(Map<String, dynamic> json) =>
      _$DeliveryResponseFromJson(json);

  Map<String, dynamic> toJson() => _$DeliveryResponseToJson(this);

  @override
  List<Object?> get props => [
        deliveryId,
        orderId,
        courierId,
        status,
        acceptedAt,
        pickedUpAt,
        deliveredAt,
        cancellationReason,
        route,
        events,
      ];
}

@JsonSerializable()
class OrderItem extends Equatable {
  final String productId;
  final String productName;
  final int quantity;
  final double unitPrice;
  final double totalPrice;
  final String? specialInstructions;

  const OrderItem({
    required this.productId,
    required this.productName,
    required this.quantity,
    required this.unitPrice,
    required this.totalPrice,
    this.specialInstructions,
  });

  factory OrderItem.fromJson(Map<String, dynamic> json) =>
      _$OrderItemFromJson(json);

  Map<String, dynamic> toJson() => _$OrderItemToJson(this);

  @override
  List<Object?> get props => [
        productId,
        productName,
        quantity,
        unitPrice,
        totalPrice,
        specialInstructions,
      ];
}

@JsonSerializable()
class DeliveryRoute extends Equatable {
  final List<RoutePoint> points;
  final double totalDistance;
  final int estimatedDuration;
  final String polyline;

  const DeliveryRoute({
    required this.points,
    required this.totalDistance,
    required this.estimatedDuration,
    required this.polyline,
  });

  factory DeliveryRoute.fromJson(Map<String, dynamic> json) =>
      _$DeliveryRouteFromJson(json);

  Map<String, dynamic> toJson() => _$DeliveryRouteToJson(this);

  @override
  List<Object?> get props => [points, totalDistance, estimatedDuration, polyline];
}

@JsonSerializable()
class RoutePoint extends Equatable {
  final double latitude;
  final double longitude;
  final String type; // 'pickup', 'delivery', 'waypoint'
  final String? address;

  const RoutePoint({
    required this.latitude,
    required this.longitude,
    required this.type,
    this.address,
  });

  factory RoutePoint.fromJson(Map<String, dynamic> json) =>
      _$RoutePointFromJson(json);

  Map<String, dynamic> toJson() => _$RoutePointToJson(this);

  @override
  List<Object?> get props => [latitude, longitude, type, address];
}

@JsonSerializable()
class DeliveryEvent extends Equatable {
  final String eventType;
  final String description;
  final DateTime timestamp;
  final double? latitude;
  final double? longitude;

  const DeliveryEvent({
    required this.eventType,
    required this.description,
    required this.timestamp,
    this.latitude,
    this.longitude,
  });

  factory DeliveryEvent.fromJson(Map<String, dynamic> json) =>
      _$DeliveryEventFromJson(json);

  Map<String, dynamic> toJson() => _$DeliveryEventToJson(this);

  @override
  List<Object?> get props => [eventType, description, timestamp, latitude, longitude];
}

@JsonSerializable()
class UpdateDeliveryStatusRequest extends Equatable {
  final String status;
  final double? latitude;
  final double? longitude;
  final String? notes;

  const UpdateDeliveryStatusRequest({
    required this.status,
    this.latitude,
    this.longitude,
    this.notes,
  });

  factory UpdateDeliveryStatusRequest.fromJson(Map<String, dynamic> json) =>
      _$UpdateDeliveryStatusRequestFromJson(json);

  Map<String, dynamic> toJson() => _$UpdateDeliveryStatusRequestToJson(this);

  @override
  List<Object?> get props => [status, latitude, longitude, notes];
}

@JsonSerializable()
class CompleteDeliveryRequest extends Equatable {
  final String deliveryConfirmationCode;
  final double latitude;
  final double longitude;
  final String? notes;
  final String? proofOfDeliveryImageUrl;

  const CompleteDeliveryRequest({
    required this.deliveryConfirmationCode,
    required this.latitude,
    required this.longitude,
    this.notes,
    this.proofOfDeliveryImageUrl,
  });

  factory CompleteDeliveryRequest.fromJson(Map<String, dynamic> json) =>
      _$CompleteDeliveryRequestFromJson(json);

  Map<String, dynamic> toJson() => _$CompleteDeliveryRequestToJson(this);

  @override
  List<Object?> get props => [
        deliveryConfirmationCode,
        latitude,
        longitude,
        notes,
        proofOfDeliveryImageUrl,
      ];
}

@JsonSerializable()
class DeliveryCompletionResponse extends Equatable {
  final bool success;
  final String message;
  final DateTime? completedAt;
  final String? errorCode;

  const DeliveryCompletionResponse({
    required this.success,
    required this.message,
    this.completedAt,
    this.errorCode,
  });

  factory DeliveryCompletionResponse.fromJson(Map<String, dynamic> json) =>
      _$DeliveryCompletionResponseFromJson(json);

  Map<String, dynamic> toJson() => _$DeliveryCompletionResponseToJson(this);

  @override
  List<Object?> get props => [success, message, completedAt, errorCode];
}