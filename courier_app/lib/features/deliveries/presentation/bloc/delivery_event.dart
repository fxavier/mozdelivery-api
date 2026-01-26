import 'package:equatable/equatable.dart';
import '../../domain/entities/delivery.dart';

abstract class DeliveryEvent extends Equatable {
  const DeliveryEvent();

  @override
  List<Object?> get props => [];
}

class LoadAssignedDeliveries extends DeliveryEvent {}

class AcceptDelivery extends DeliveryEvent {
  final String deliveryId;

  const AcceptDelivery(this.deliveryId);

  @override
  List<Object> get props => [deliveryId];
}

class UpdateDeliveryStatus extends DeliveryEvent {
  final String deliveryId;
  final DeliveryStatus status;
  final double? latitude;
  final double? longitude;
  final String? notes;

  const UpdateDeliveryStatus(
    this.deliveryId,
    this.status, {
    this.latitude,
    this.longitude,
    this.notes,
  });

  @override
  List<Object?> get props => [deliveryId, status, latitude, longitude, notes];
}

class CompleteDelivery extends DeliveryEvent {
  final String deliveryId;
  final String confirmationCode;
  final double latitude;
  final double longitude;
  final String? notes;
  final String? proofOfDeliveryImageUrl;

  const CompleteDelivery(
    this.deliveryId,
    this.confirmationCode,
    this.latitude,
    this.longitude, {
    this.notes,
    this.proofOfDeliveryImageUrl,
  });

  @override
  List<Object?> get props => [
        deliveryId,
        confirmationCode,
        latitude,
        longitude,
        notes,
        proofOfDeliveryImageUrl,
      ];
}

class LoadOptimizedRoute extends DeliveryEvent {
  final String deliveryId;

  const LoadOptimizedRoute(this.deliveryId);

  @override
  List<Object> get props => [deliveryId];
}

class RefreshDeliveries extends DeliveryEvent {}