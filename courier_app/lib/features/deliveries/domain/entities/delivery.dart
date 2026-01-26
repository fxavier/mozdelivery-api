import 'package:equatable/equatable.dart';

enum DeliveryStatus {
  assigned,
  accepted,
  pickedUp,
  outForDelivery,
  delivered,
  cancelled,
  failed
}

class Delivery extends Equatable {
  final String deliveryId;
  final String orderId;
  final String merchantName;
  final String merchantAddress;
  final String customerName;
  final String customerPhone;
  final String deliveryAddress;
  final double deliveryLatitude;
  final double deliveryLongitude;
  final DeliveryStatus status;
  final double totalAmount;
  final String currency;
  final DateTime estimatedPickupTime;
  final DateTime estimatedDeliveryTime;
  final String? specialInstructions;
  final List<OrderItem> items;
  final DeliveryRoute? route;
  final DateTime? acceptedAt;
  final DateTime? pickedUpAt;
  final DateTime? deliveredAt;

  const Delivery({
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
    this.route,
    this.acceptedAt,
    this.pickedUpAt,
    this.deliveredAt,
  });

  Delivery copyWith({
    String? deliveryId,
    String? orderId,
    String? merchantName,
    String? merchantAddress,
    String? customerName,
    String? customerPhone,
    String? deliveryAddress,
    double? deliveryLatitude,
    double? deliveryLongitude,
    DeliveryStatus? status,
    double? totalAmount,
    String? currency,
    DateTime? estimatedPickupTime,
    DateTime? estimatedDeliveryTime,
    String? specialInstructions,
    List<OrderItem>? items,
    DeliveryRoute? route,
    DateTime? acceptedAt,
    DateTime? pickedUpAt,
    DateTime? deliveredAt,
  }) {
    return Delivery(
      deliveryId: deliveryId ?? this.deliveryId,
      orderId: orderId ?? this.orderId,
      merchantName: merchantName ?? this.merchantName,
      merchantAddress: merchantAddress ?? this.merchantAddress,
      customerName: customerName ?? this.customerName,
      customerPhone: customerPhone ?? this.customerPhone,
      deliveryAddress: deliveryAddress ?? this.deliveryAddress,
      deliveryLatitude: deliveryLatitude ?? this.deliveryLatitude,
      deliveryLongitude: deliveryLongitude ?? this.deliveryLongitude,
      status: status ?? this.status,
      totalAmount: totalAmount ?? this.totalAmount,
      currency: currency ?? this.currency,
      estimatedPickupTime: estimatedPickupTime ?? this.estimatedPickupTime,
      estimatedDeliveryTime: estimatedDeliveryTime ?? this.estimatedDeliveryTime,
      specialInstructions: specialInstructions ?? this.specialInstructions,
      items: items ?? this.items,
      route: route ?? this.route,
      acceptedAt: acceptedAt ?? this.acceptedAt,
      pickedUpAt: pickedUpAt ?? this.pickedUpAt,
      deliveredAt: deliveredAt ?? this.deliveredAt,
    );
  }

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
        route,
        acceptedAt,
        pickedUpAt,
        deliveredAt,
      ];
}

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

  @override
  List<Object?> get props => [points, totalDistance, estimatedDuration, polyline];
}

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

  @override
  List<Object?> get props => [latitude, longitude, type, address];
}