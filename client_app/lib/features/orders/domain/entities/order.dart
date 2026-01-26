import 'package:equatable/equatable.dart';

class Order extends Equatable {
  final String id;
  final String merchantId;
  final String? clientId;
  final GuestInfo? guestInfo;
  final List<OrderItem> items;
  final DeliveryAddress deliveryAddress;
  final OrderStatus status;
  final PaymentInfo paymentInfo;
  final double totalAmount;
  final String currency;
  final String? deliveryConfirmationCode;
  final DateTime createdAt;
  final DateTime updatedAt;

  const Order({
    required this.id,
    required this.merchantId,
    this.clientId,
    this.guestInfo,
    required this.items,
    required this.deliveryAddress,
    required this.status,
    required this.paymentInfo,
    required this.totalAmount,
    required this.currency,
    this.deliveryConfirmationCode,
    required this.createdAt,
    required this.updatedAt,
  });

  @override
  List<Object?> get props => [
        id,
        merchantId,
        clientId,
        guestInfo,
        items,
        deliveryAddress,
        status,
        paymentInfo,
        totalAmount,
        currency,
        deliveryConfirmationCode,
        createdAt,
        updatedAt,
      ];
}

class OrderItem extends Equatable {
  final String productId;
  final String productName;
  final double price;
  final int quantity;
  final List<SelectedModifier>? modifiers;
  final String? notes;

  const OrderItem({
    required this.productId,
    required this.productName,
    required this.price,
    required this.quantity,
    this.modifiers,
    this.notes,
  });

  double get totalPrice {
    final modifierPrice = modifiers?.fold(0.0, (sum, mod) => sum + (mod.priceAdjustment ?? 0.0)) ?? 0.0;
    return (price * quantity) + modifierPrice;
  }

  @override
  List<Object?> get props => [productId, productName, price, quantity, modifiers, notes];
}

class SelectedModifier extends Equatable {
  final String modifierId;
  final String optionId;
  final String optionName;
  final double? priceAdjustment;

  const SelectedModifier({
    required this.modifierId,
    required this.optionId,
    required this.optionName,
    this.priceAdjustment,
  });

  @override
  List<Object?> get props => [modifierId, optionId, optionName, priceAdjustment];
}

class GuestInfo extends Equatable {
  final String contactPhone;
  final String? contactEmail;
  final String contactName;
  final String? trackingToken;

  const GuestInfo({
    required this.contactPhone,
    this.contactEmail,
    required this.contactName,
    this.trackingToken,
  });

  @override
  List<Object?> get props => [contactPhone, contactEmail, contactName, trackingToken];
}

class DeliveryAddress extends Equatable {
  final String street;
  final String city;
  final String? state;
  final String? postalCode;
  final String? country;
  final String? additionalInfo;
  final double? latitude;
  final double? longitude;

  const DeliveryAddress({
    required this.street,
    required this.city,
    this.state,
    this.postalCode,
    this.country,
    this.additionalInfo,
    this.latitude,
    this.longitude,
  });

  @override
  List<Object?> get props => [
        street,
        city,
        state,
        postalCode,
        country,
        additionalInfo,
        latitude,
        longitude,
      ];
}

class PaymentInfo extends Equatable {
  final String method;
  final String status;
  final String? transactionId;

  const PaymentInfo({
    required this.method,
    required this.status,
    this.transactionId,
  });

  @override
  List<Object?> get props => [method, status, transactionId];
}

enum OrderStatus {
  created,
  paymentProcessing,
  paymentConfirmed,
  paymentFailed,
  preparing,
  readyForPickup,
  pickedUp,
  outForDelivery,
  delivered,
  cancelled,
  deliveryFailed;

  String get displayName {
    switch (this) {
      case OrderStatus.created:
        return 'Order Created';
      case OrderStatus.paymentProcessing:
        return 'Processing Payment';
      case OrderStatus.paymentConfirmed:
        return 'Payment Confirmed';
      case OrderStatus.paymentFailed:
        return 'Payment Failed';
      case OrderStatus.preparing:
        return 'Preparing Order';
      case OrderStatus.readyForPickup:
        return 'Ready for Pickup';
      case OrderStatus.pickedUp:
        return 'Picked Up';
      case OrderStatus.outForDelivery:
        return 'Out for Delivery';
      case OrderStatus.delivered:
        return 'Delivered';
      case OrderStatus.cancelled:
        return 'Cancelled';
      case OrderStatus.deliveryFailed:
        return 'Delivery Failed';
    }
  }

  static OrderStatus fromString(String value) {
    return OrderStatus.values.firstWhere(
      (status) => status.name.toLowerCase() == value.toLowerCase(),
      orElse: () => OrderStatus.created,
    );
  }
}