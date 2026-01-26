import 'package:equatable/equatable.dart';
import 'package:json_annotation/json_annotation.dart';

part 'order_models.g.dart';

@JsonSerializable()
class GuestOrderRequest extends Equatable {
  final String merchantId;
  final List<OrderItemRequest> items;
  final DeliveryAddressRequest deliveryAddress;
  final GuestInfoRequest guestInfo;
  final PaymentInfoRequest paymentInfo;

  const GuestOrderRequest({
    required this.merchantId,
    required this.items,
    required this.deliveryAddress,
    required this.guestInfo,
    required this.paymentInfo,
  });

  factory GuestOrderRequest.fromJson(Map<String, dynamic> json) =>
      _$GuestOrderRequestFromJson(json);

  Map<String, dynamic> toJson() => _$GuestOrderRequestToJson(this);

  @override
  List<Object?> get props => [merchantId, items, deliveryAddress, guestInfo, paymentInfo];
}

@JsonSerializable()
class OrderItemRequest extends Equatable {
  final String productId;
  final int quantity;
  final List<SelectedModifierRequest>? modifiers;
  final String? notes;

  const OrderItemRequest({
    required this.productId,
    required this.quantity,
    this.modifiers,
    this.notes,
  });

  factory OrderItemRequest.fromJson(Map<String, dynamic> json) =>
      _$OrderItemRequestFromJson(json);

  Map<String, dynamic> toJson() => _$OrderItemRequestToJson(this);

  @override
  List<Object?> get props => [productId, quantity, modifiers, notes];
}

@JsonSerializable()
class SelectedModifierRequest extends Equatable {
  final String modifierId;
  final String optionId;

  const SelectedModifierRequest({
    required this.modifierId,
    required this.optionId,
  });

  factory SelectedModifierRequest.fromJson(Map<String, dynamic> json) =>
      _$SelectedModifierRequestFromJson(json);

  Map<String, dynamic> toJson() => _$SelectedModifierRequestToJson(this);

  @override
  List<Object?> get props => [modifierId, optionId];
}

@JsonSerializable()
class DeliveryAddressRequest extends Equatable {
  final String street;
  final String city;
  final String? state;
  final String? postalCode;
  final String? country;
  final String? additionalInfo;
  final double? latitude;
  final double? longitude;

  const DeliveryAddressRequest({
    required this.street,
    required this.city,
    this.state,
    this.postalCode,
    this.country,
    this.additionalInfo,
    this.latitude,
    this.longitude,
  });

  factory DeliveryAddressRequest.fromJson(Map<String, dynamic> json) =>
      _$DeliveryAddressRequestFromJson(json);

  Map<String, dynamic> toJson() => _$DeliveryAddressRequestToJson(this);

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

@JsonSerializable()
class GuestInfoRequest extends Equatable {
  final String contactPhone;
  final String? contactEmail;
  final String contactName;

  const GuestInfoRequest({
    required this.contactPhone,
    this.contactEmail,
    required this.contactName,
  });

  factory GuestInfoRequest.fromJson(Map<String, dynamic> json) =>
      _$GuestInfoRequestFromJson(json);

  Map<String, dynamic> toJson() => _$GuestInfoRequestToJson(this);

  @override
  List<Object?> get props => [contactPhone, contactEmail, contactName];
}

@JsonSerializable()
class PaymentInfoRequest extends Equatable {
  final String method;

  const PaymentInfoRequest({
    required this.method,
  });

  factory PaymentInfoRequest.fromJson(Map<String, dynamic> json) =>
      _$PaymentInfoRequestFromJson(json);

  Map<String, dynamic> toJson() => _$PaymentInfoRequestToJson(this);

  @override
  List<Object?> get props => [method];
}

@JsonSerializable()
class GuestOrderResponse extends Equatable {
  final String orderId;
  final String trackingToken;
  final String deliveryConfirmationCode;
  final String status;
  final double totalAmount;
  final String currency;
  final DateTime createdAt;

  const GuestOrderResponse({
    required this.orderId,
    required this.trackingToken,
    required this.deliveryConfirmationCode,
    required this.status,
    required this.totalAmount,
    required this.currency,
    required this.createdAt,
  });

  factory GuestOrderResponse.fromJson(Map<String, dynamic> json) =>
      _$GuestOrderResponseFromJson(json);

  Map<String, dynamic> toJson() => _$GuestOrderResponseToJson(this);

  @override
  List<Object?> get props => [
        orderId,
        trackingToken,
        deliveryConfirmationCode,
        status,
        totalAmount,
        currency,
        createdAt,
      ];
}

@JsonSerializable()
class GuestTrackingResponse extends Equatable {
  final String orderId;
  final String status;
  final String merchantName;
  final List<OrderItemResponse> items;
  final DeliveryAddressResponse deliveryAddress;
  final double totalAmount;
  final String currency;
  final String? deliveryConfirmationCode;
  final DateTime createdAt;
  final DateTime updatedAt;

  const GuestTrackingResponse({
    required this.orderId,
    required this.status,
    required this.merchantName,
    required this.items,
    required this.deliveryAddress,
    required this.totalAmount,
    required this.currency,
    this.deliveryConfirmationCode,
    required this.createdAt,
    required this.updatedAt,
  });

  factory GuestTrackingResponse.fromJson(Map<String, dynamic> json) =>
      _$GuestTrackingResponseFromJson(json);

  Map<String, dynamic> toJson() => _$GuestTrackingResponseToJson(this);

  @override
  List<Object?> get props => [
        orderId,
        status,
        merchantName,
        items,
        deliveryAddress,
        totalAmount,
        currency,
        deliveryConfirmationCode,
        createdAt,
        updatedAt,
      ];
}

@JsonSerializable()
class OrderItemResponse extends Equatable {
  final String productId;
  final String productName;
  final double price;
  final int quantity;
  final List<SelectedModifierResponse>? modifiers;
  final String? notes;

  const OrderItemResponse({
    required this.productId,
    required this.productName,
    required this.price,
    required this.quantity,
    this.modifiers,
    this.notes,
  });

  factory OrderItemResponse.fromJson(Map<String, dynamic> json) =>
      _$OrderItemResponseFromJson(json);

  Map<String, dynamic> toJson() => _$OrderItemResponseToJson(this);

  @override
  List<Object?> get props => [productId, productName, price, quantity, modifiers, notes];
}

@JsonSerializable()
class SelectedModifierResponse extends Equatable {
  final String modifierId;
  final String optionId;
  final String optionName;
  final double? priceAdjustment;

  const SelectedModifierResponse({
    required this.modifierId,
    required this.optionId,
    required this.optionName,
    this.priceAdjustment,
  });

  factory SelectedModifierResponse.fromJson(Map<String, dynamic> json) =>
      _$SelectedModifierResponseFromJson(json);

  Map<String, dynamic> toJson() => _$SelectedModifierResponseToJson(this);

  @override
  List<Object?> get props => [modifierId, optionId, optionName, priceAdjustment];
}

@JsonSerializable()
class DeliveryAddressResponse extends Equatable {
  final String street;
  final String city;
  final String? state;
  final String? postalCode;
  final String? country;
  final String? additionalInfo;

  const DeliveryAddressResponse({
    required this.street,
    required this.city,
    this.state,
    this.postalCode,
    this.country,
    this.additionalInfo,
  });

  factory DeliveryAddressResponse.fromJson(Map<String, dynamic> json) =>
      _$DeliveryAddressResponseFromJson(json);

  Map<String, dynamic> toJson() => _$DeliveryAddressResponseToJson(this);

  @override
  List<Object?> get props => [street, city, state, postalCode, country, additionalInfo];
}