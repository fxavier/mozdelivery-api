import 'package:equatable/equatable.dart';
import '../../domain/entities/delivery.dart';

abstract class DeliveryState extends Equatable {
  const DeliveryState();

  @override
  List<Object?> get props => [];
}

class DeliveryInitial extends DeliveryState {}

class DeliveryLoading extends DeliveryState {}

class DeliveryLoaded extends DeliveryState {
  final List<Delivery> deliveries;
  final Delivery? selectedDelivery;
  final DeliveryRoute? optimizedRoute;

  const DeliveryLoaded({
    required this.deliveries,
    this.selectedDelivery,
    this.optimizedRoute,
  });

  DeliveryLoaded copyWith({
    List<Delivery>? deliveries,
    Delivery? selectedDelivery,
    DeliveryRoute? optimizedRoute,
  }) {
    return DeliveryLoaded(
      deliveries: deliveries ?? this.deliveries,
      selectedDelivery: selectedDelivery ?? this.selectedDelivery,
      optimizedRoute: optimizedRoute ?? this.optimizedRoute,
    );
  }

  @override
  List<Object?> get props => [deliveries, selectedDelivery, optimizedRoute];
}

class DeliveryError extends DeliveryState {
  final String message;

  const DeliveryError(this.message);

  @override
  List<Object> get props => [message];
}

class DeliveryActionLoading extends DeliveryState {
  final String action;

  const DeliveryActionLoading(this.action);

  @override
  List<Object> get props => [action];
}

class DeliveryActionSuccess extends DeliveryState {
  final String message;

  const DeliveryActionSuccess(this.message);

  @override
  List<Object> get props => [message];
}

class DeliveryActionError extends DeliveryState {
  final String message;

  const DeliveryActionError(this.message);

  @override
  List<Object> get props => [message];
}