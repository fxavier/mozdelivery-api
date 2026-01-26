// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'delivery_models.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

DeliveryAssignmentResponse _$DeliveryAssignmentResponseFromJson(
        Map<String, dynamic> json) =>
    DeliveryAssignmentResponse(
      deliveryId: json['deliveryId'] as String,
      orderId: json['orderId'] as String,
      merchantName: json['merchantName'] as String,
      merchantAddress: json['merchantAddress'] as String,
      customerName: json['customerName'] as String,
      customerPhone: json['customerPhone'] as String,
      deliveryAddress: json['deliveryAddress'] as String,
      deliveryLatitude: (json['deliveryLatitude'] as num).toDouble(),
      deliveryLongitude: (json['deliveryLongitude'] as num).toDouble(),
      status: json['status'] as String,
      totalAmount: (json['totalAmount'] as num).toDouble(),
      currency: json['currency'] as String,
      estimatedPickupTime:
          DateTime.parse(json['estimatedPickupTime'] as String),
      estimatedDeliveryTime:
          DateTime.parse(json['estimatedDeliveryTime'] as String),
      specialInstructions: json['specialInstructions'] as String?,
      items: (json['items'] as List<dynamic>)
          .map((e) => OrderItem.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$DeliveryAssignmentResponseToJson(
        DeliveryAssignmentResponse instance) =>
    <String, dynamic>{
      'deliveryId': instance.deliveryId,
      'orderId': instance.orderId,
      'merchantName': instance.merchantName,
      'merchantAddress': instance.merchantAddress,
      'customerName': instance.customerName,
      'customerPhone': instance.customerPhone,
      'deliveryAddress': instance.deliveryAddress,
      'deliveryLatitude': instance.deliveryLatitude,
      'deliveryLongitude': instance.deliveryLongitude,
      'status': instance.status,
      'totalAmount': instance.totalAmount,
      'currency': instance.currency,
      'estimatedPickupTime': instance.estimatedPickupTime.toIso8601String(),
      'estimatedDeliveryTime': instance.estimatedDeliveryTime.toIso8601String(),
      'specialInstructions': instance.specialInstructions,
      'items': instance.items,
    };

DeliveryResponse _$DeliveryResponseFromJson(Map<String, dynamic> json) =>
    DeliveryResponse(
      deliveryId: json['deliveryId'] as String,
      orderId: json['orderId'] as String,
      courierId: json['courierId'] as String,
      status: json['status'] as String,
      acceptedAt: json['acceptedAt'] == null
          ? null
          : DateTime.parse(json['acceptedAt'] as String),
      pickedUpAt: json['pickedUpAt'] == null
          ? null
          : DateTime.parse(json['pickedUpAt'] as String),
      deliveredAt: json['deliveredAt'] == null
          ? null
          : DateTime.parse(json['deliveredAt'] as String),
      cancellationReason: json['cancellationReason'] as String?,
      route: json['route'] == null
          ? null
          : DeliveryRoute.fromJson(json['route'] as Map<String, dynamic>),
      events: (json['events'] as List<dynamic>)
          .map((e) => DeliveryEvent.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$DeliveryResponseToJson(DeliveryResponse instance) =>
    <String, dynamic>{
      'deliveryId': instance.deliveryId,
      'orderId': instance.orderId,
      'courierId': instance.courierId,
      'status': instance.status,
      'acceptedAt': instance.acceptedAt?.toIso8601String(),
      'pickedUpAt': instance.pickedUpAt?.toIso8601String(),
      'deliveredAt': instance.deliveredAt?.toIso8601String(),
      'cancellationReason': instance.cancellationReason,
      'route': instance.route,
      'events': instance.events,
    };

OrderItem _$OrderItemFromJson(Map<String, dynamic> json) => OrderItem(
      productId: json['productId'] as String,
      productName: json['productName'] as String,
      quantity: (json['quantity'] as num).toInt(),
      unitPrice: (json['unitPrice'] as num).toDouble(),
      totalPrice: (json['totalPrice'] as num).toDouble(),
      specialInstructions: json['specialInstructions'] as String?,
    );

Map<String, dynamic> _$OrderItemToJson(OrderItem instance) => <String, dynamic>{
      'productId': instance.productId,
      'productName': instance.productName,
      'quantity': instance.quantity,
      'unitPrice': instance.unitPrice,
      'totalPrice': instance.totalPrice,
      'specialInstructions': instance.specialInstructions,
    };

DeliveryRoute _$DeliveryRouteFromJson(Map<String, dynamic> json) =>
    DeliveryRoute(
      points: (json['points'] as List<dynamic>)
          .map((e) => RoutePoint.fromJson(e as Map<String, dynamic>))
          .toList(),
      totalDistance: (json['totalDistance'] as num).toDouble(),
      estimatedDuration: (json['estimatedDuration'] as num).toInt(),
      polyline: json['polyline'] as String,
    );

Map<String, dynamic> _$DeliveryRouteToJson(DeliveryRoute instance) =>
    <String, dynamic>{
      'points': instance.points,
      'totalDistance': instance.totalDistance,
      'estimatedDuration': instance.estimatedDuration,
      'polyline': instance.polyline,
    };

RoutePoint _$RoutePointFromJson(Map<String, dynamic> json) => RoutePoint(
      latitude: (json['latitude'] as num).toDouble(),
      longitude: (json['longitude'] as num).toDouble(),
      type: json['type'] as String,
      address: json['address'] as String?,
    );

Map<String, dynamic> _$RoutePointToJson(RoutePoint instance) =>
    <String, dynamic>{
      'latitude': instance.latitude,
      'longitude': instance.longitude,
      'type': instance.type,
      'address': instance.address,
    };

DeliveryEvent _$DeliveryEventFromJson(Map<String, dynamic> json) =>
    DeliveryEvent(
      eventType: json['eventType'] as String,
      description: json['description'] as String,
      timestamp: DateTime.parse(json['timestamp'] as String),
      latitude: (json['latitude'] as num?)?.toDouble(),
      longitude: (json['longitude'] as num?)?.toDouble(),
    );

Map<String, dynamic> _$DeliveryEventToJson(DeliveryEvent instance) =>
    <String, dynamic>{
      'eventType': instance.eventType,
      'description': instance.description,
      'timestamp': instance.timestamp.toIso8601String(),
      'latitude': instance.latitude,
      'longitude': instance.longitude,
    };

UpdateDeliveryStatusRequest _$UpdateDeliveryStatusRequestFromJson(
        Map<String, dynamic> json) =>
    UpdateDeliveryStatusRequest(
      status: json['status'] as String,
      latitude: (json['latitude'] as num?)?.toDouble(),
      longitude: (json['longitude'] as num?)?.toDouble(),
      notes: json['notes'] as String?,
    );

Map<String, dynamic> _$UpdateDeliveryStatusRequestToJson(
        UpdateDeliveryStatusRequest instance) =>
    <String, dynamic>{
      'status': instance.status,
      'latitude': instance.latitude,
      'longitude': instance.longitude,
      'notes': instance.notes,
    };

CompleteDeliveryRequest _$CompleteDeliveryRequestFromJson(
        Map<String, dynamic> json) =>
    CompleteDeliveryRequest(
      deliveryConfirmationCode: json['deliveryConfirmationCode'] as String,
      latitude: (json['latitude'] as num).toDouble(),
      longitude: (json['longitude'] as num).toDouble(),
      notes: json['notes'] as String?,
      proofOfDeliveryImageUrl: json['proofOfDeliveryImageUrl'] as String?,
    );

Map<String, dynamic> _$CompleteDeliveryRequestToJson(
        CompleteDeliveryRequest instance) =>
    <String, dynamic>{
      'deliveryConfirmationCode': instance.deliveryConfirmationCode,
      'latitude': instance.latitude,
      'longitude': instance.longitude,
      'notes': instance.notes,
      'proofOfDeliveryImageUrl': instance.proofOfDeliveryImageUrl,
    };

DeliveryCompletionResponse _$DeliveryCompletionResponseFromJson(
        Map<String, dynamic> json) =>
    DeliveryCompletionResponse(
      success: json['success'] as bool,
      message: json['message'] as String,
      completedAt: json['completedAt'] == null
          ? null
          : DateTime.parse(json['completedAt'] as String),
      errorCode: json['errorCode'] as String?,
    );

Map<String, dynamic> _$DeliveryCompletionResponseToJson(
        DeliveryCompletionResponse instance) =>
    <String, dynamic>{
      'success': instance.success,
      'message': instance.message,
      'completedAt': instance.completedAt?.toIso8601String(),
      'errorCode': instance.errorCode,
    };
