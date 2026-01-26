// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'merchant_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

MerchantModel _$MerchantModelFromJson(Map<String, dynamic> json) =>
    MerchantModel(
      id: json['id'] as String,
      businessName: json['businessName'] as String,
      displayName: json['displayName'] as String,
      vertical: json['vertical'] as String,
      status: json['status'] as String,
      description: json['description'] as String?,
      imageUrl: json['imageUrl'] as String?,
      city: json['city'] as String?,
      rating: (json['rating'] as num?)?.toDouble(),
      reviewCount: (json['reviewCount'] as num?)?.toInt(),
      isOpen: json['isOpen'] as bool?,
      openingHours: json['openingHours'] as String?,
      deliveryFee: (json['deliveryFee'] as num?)?.toDouble(),
      estimatedDeliveryTime: (json['estimatedDeliveryTime'] as num?)?.toInt(),
    );

Map<String, dynamic> _$MerchantModelToJson(MerchantModel instance) =>
    <String, dynamic>{
      'id': instance.id,
      'businessName': instance.businessName,
      'displayName': instance.displayName,
      'vertical': instance.vertical,
      'status': instance.status,
      'description': instance.description,
      'imageUrl': instance.imageUrl,
      'city': instance.city,
      'rating': instance.rating,
      'reviewCount': instance.reviewCount,
      'isOpen': instance.isOpen,
      'openingHours': instance.openingHours,
      'deliveryFee': instance.deliveryFee,
      'estimatedDeliveryTime': instance.estimatedDeliveryTime,
    };
