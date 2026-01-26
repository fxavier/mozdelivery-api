import 'package:equatable/equatable.dart';
import 'package:json_annotation/json_annotation.dart';

part 'merchant_model.g.dart';

@JsonSerializable()
class MerchantModel extends Equatable {
  final String id;
  final String businessName;
  final String displayName;
  final String vertical;
  final String status;
  final String? description;
  final String? imageUrl;
  final String? city;
  final double? rating;
  final int? reviewCount;
  final bool? isOpen;
  final String? openingHours;
  final double? deliveryFee;
  final int? estimatedDeliveryTime;

  const MerchantModel({
    required this.id,
    required this.businessName,
    required this.displayName,
    required this.vertical,
    required this.status,
    this.description,
    this.imageUrl,
    this.city,
    this.rating,
    this.reviewCount,
    this.isOpen,
    this.openingHours,
    this.deliveryFee,
    this.estimatedDeliveryTime,
  });

  factory MerchantModel.fromJson(Map<String, dynamic> json) =>
      _$MerchantModelFromJson(json);

  Map<String, dynamic> toJson() => _$MerchantModelToJson(this);

  @override
  List<Object?> get props => [
        id,
        businessName,
        displayName,
        vertical,
        status,
        description,
        imageUrl,
        city,
        rating,
        reviewCount,
        isOpen,
        openingHours,
        deliveryFee,
        estimatedDeliveryTime,
      ];
}

enum MerchantVertical {
  restaurant,
  grocery,
  pharmacy,
  convenience,
  electronics,
  florist,
  beverages,
  fuel;

  String get displayName {
    switch (this) {
      case MerchantVertical.restaurant:
        return 'Restaurant';
      case MerchantVertical.grocery:
        return 'Grocery';
      case MerchantVertical.pharmacy:
        return 'Pharmacy';
      case MerchantVertical.convenience:
        return 'Convenience Store';
      case MerchantVertical.electronics:
        return 'Electronics';
      case MerchantVertical.florist:
        return 'Florist';
      case MerchantVertical.beverages:
        return 'Beverages';
      case MerchantVertical.fuel:
        return 'Fuel Station';
    }
  }

  static MerchantVertical? fromString(String value) {
    try {
      return MerchantVertical.values.firstWhere(
        (vertical) => vertical.name.toLowerCase() == value.toLowerCase(),
      );
    } catch (e) {
      return null;
    }
  }
}