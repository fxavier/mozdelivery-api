// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'order_models.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

GuestOrderRequest _$GuestOrderRequestFromJson(Map<String, dynamic> json) =>
    GuestOrderRequest(
      merchantId: json['merchantId'] as String,
      items: (json['items'] as List<dynamic>)
          .map((e) => OrderItemRequest.fromJson(e as Map<String, dynamic>))
          .toList(),
      deliveryAddress: DeliveryAddressRequest.fromJson(
          json['deliveryAddress'] as Map<String, dynamic>),
      guestInfo:
          GuestInfoRequest.fromJson(json['guestInfo'] as Map<String, dynamic>),
      paymentInfo: PaymentInfoRequest.fromJson(
          json['paymentInfo'] as Map<String, dynamic>),
    );

Map<String, dynamic> _$GuestOrderRequestToJson(GuestOrderRequest instance) =>
    <String, dynamic>{
      'merchantId': instance.merchantId,
      'items': instance.items,
      'deliveryAddress': instance.deliveryAddress,
      'guestInfo': instance.guestInfo,
      'paymentInfo': instance.paymentInfo,
    };

OrderItemRequest _$OrderItemRequestFromJson(Map<String, dynamic> json) =>
    OrderItemRequest(
      productId: json['productId'] as String,
      quantity: (json['quantity'] as num).toInt(),
      modifiers: (json['modifiers'] as List<dynamic>?)
          ?.map((e) =>
              SelectedModifierRequest.fromJson(e as Map<String, dynamic>))
          .toList(),
      notes: json['notes'] as String?,
    );

Map<String, dynamic> _$OrderItemRequestToJson(OrderItemRequest instance) =>
    <String, dynamic>{
      'productId': instance.productId,
      'quantity': instance.quantity,
      'modifiers': instance.modifiers,
      'notes': instance.notes,
    };

SelectedModifierRequest _$SelectedModifierRequestFromJson(
        Map<String, dynamic> json) =>
    SelectedModifierRequest(
      modifierId: json['modifierId'] as String,
      optionId: json['optionId'] as String,
    );

Map<String, dynamic> _$SelectedModifierRequestToJson(
        SelectedModifierRequest instance) =>
    <String, dynamic>{
      'modifierId': instance.modifierId,
      'optionId': instance.optionId,
    };

DeliveryAddressRequest _$DeliveryAddressRequestFromJson(
        Map<String, dynamic> json) =>
    DeliveryAddressRequest(
      street: json['street'] as String,
      city: json['city'] as String,
      state: json['state'] as String?,
      postalCode: json['postalCode'] as String?,
      country: json['country'] as String?,
      additionalInfo: json['additionalInfo'] as String?,
      latitude: (json['latitude'] as num?)?.toDouble(),
      longitude: (json['longitude'] as num?)?.toDouble(),
    );

Map<String, dynamic> _$DeliveryAddressRequestToJson(
        DeliveryAddressRequest instance) =>
    <String, dynamic>{
      'street': instance.street,
      'city': instance.city,
      'state': instance.state,
      'postalCode': instance.postalCode,
      'country': instance.country,
      'additionalInfo': instance.additionalInfo,
      'latitude': instance.latitude,
      'longitude': instance.longitude,
    };

GuestInfoRequest _$GuestInfoRequestFromJson(Map<String, dynamic> json) =>
    GuestInfoRequest(
      contactPhone: json['contactPhone'] as String,
      contactEmail: json['contactEmail'] as String?,
      contactName: json['contactName'] as String,
    );

Map<String, dynamic> _$GuestInfoRequestToJson(GuestInfoRequest instance) =>
    <String, dynamic>{
      'contactPhone': instance.contactPhone,
      'contactEmail': instance.contactEmail,
      'contactName': instance.contactName,
    };

PaymentInfoRequest _$PaymentInfoRequestFromJson(Map<String, dynamic> json) =>
    PaymentInfoRequest(
      method: json['method'] as String,
    );

Map<String, dynamic> _$PaymentInfoRequestToJson(PaymentInfoRequest instance) =>
    <String, dynamic>{
      'method': instance.method,
    };

GuestOrderResponse _$GuestOrderResponseFromJson(Map<String, dynamic> json) =>
    GuestOrderResponse(
      orderId: json['orderId'] as String,
      trackingToken: json['trackingToken'] as String,
      deliveryConfirmationCode: json['deliveryConfirmationCode'] as String,
      status: json['status'] as String,
      totalAmount: (json['totalAmount'] as num).toDouble(),
      currency: json['currency'] as String,
      createdAt: DateTime.parse(json['createdAt'] as String),
    );

Map<String, dynamic> _$GuestOrderResponseToJson(GuestOrderResponse instance) =>
    <String, dynamic>{
      'orderId': instance.orderId,
      'trackingToken': instance.trackingToken,
      'deliveryConfirmationCode': instance.deliveryConfirmationCode,
      'status': instance.status,
      'totalAmount': instance.totalAmount,
      'currency': instance.currency,
      'createdAt': instance.createdAt.toIso8601String(),
    };

GuestTrackingResponse _$GuestTrackingResponseFromJson(
        Map<String, dynamic> json) =>
    GuestTrackingResponse(
      orderId: json['orderId'] as String,
      status: json['status'] as String,
      merchantName: json['merchantName'] as String,
      items: (json['items'] as List<dynamic>)
          .map((e) => OrderItemResponse.fromJson(e as Map<String, dynamic>))
          .toList(),
      deliveryAddress: DeliveryAddressResponse.fromJson(
          json['deliveryAddress'] as Map<String, dynamic>),
      totalAmount: (json['totalAmount'] as num).toDouble(),
      currency: json['currency'] as String,
      deliveryConfirmationCode: json['deliveryConfirmationCode'] as String?,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: DateTime.parse(json['updatedAt'] as String),
    );

Map<String, dynamic> _$GuestTrackingResponseToJson(
        GuestTrackingResponse instance) =>
    <String, dynamic>{
      'orderId': instance.orderId,
      'status': instance.status,
      'merchantName': instance.merchantName,
      'items': instance.items,
      'deliveryAddress': instance.deliveryAddress,
      'totalAmount': instance.totalAmount,
      'currency': instance.currency,
      'deliveryConfirmationCode': instance.deliveryConfirmationCode,
      'createdAt': instance.createdAt.toIso8601String(),
      'updatedAt': instance.updatedAt.toIso8601String(),
    };

OrderItemResponse _$OrderItemResponseFromJson(Map<String, dynamic> json) =>
    OrderItemResponse(
      productId: json['productId'] as String,
      productName: json['productName'] as String,
      price: (json['price'] as num).toDouble(),
      quantity: (json['quantity'] as num).toInt(),
      modifiers: (json['modifiers'] as List<dynamic>?)
          ?.map((e) =>
              SelectedModifierResponse.fromJson(e as Map<String, dynamic>))
          .toList(),
      notes: json['notes'] as String?,
    );

Map<String, dynamic> _$OrderItemResponseToJson(OrderItemResponse instance) =>
    <String, dynamic>{
      'productId': instance.productId,
      'productName': instance.productName,
      'price': instance.price,
      'quantity': instance.quantity,
      'modifiers': instance.modifiers,
      'notes': instance.notes,
    };

SelectedModifierResponse _$SelectedModifierResponseFromJson(
        Map<String, dynamic> json) =>
    SelectedModifierResponse(
      modifierId: json['modifierId'] as String,
      optionId: json['optionId'] as String,
      optionName: json['optionName'] as String,
      priceAdjustment: (json['priceAdjustment'] as num?)?.toDouble(),
    );

Map<String, dynamic> _$SelectedModifierResponseToJson(
        SelectedModifierResponse instance) =>
    <String, dynamic>{
      'modifierId': instance.modifierId,
      'optionId': instance.optionId,
      'optionName': instance.optionName,
      'priceAdjustment': instance.priceAdjustment,
    };

DeliveryAddressResponse _$DeliveryAddressResponseFromJson(
        Map<String, dynamic> json) =>
    DeliveryAddressResponse(
      street: json['street'] as String,
      city: json['city'] as String,
      state: json['state'] as String?,
      postalCode: json['postalCode'] as String?,
      country: json['country'] as String?,
      additionalInfo: json['additionalInfo'] as String?,
    );

Map<String, dynamic> _$DeliveryAddressResponseToJson(
        DeliveryAddressResponse instance) =>
    <String, dynamic>{
      'street': instance.street,
      'city': instance.city,
      'state': instance.state,
      'postalCode': instance.postalCode,
      'country': instance.country,
      'additionalInfo': instance.additionalInfo,
    };
