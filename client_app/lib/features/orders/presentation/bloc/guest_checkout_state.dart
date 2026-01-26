import 'package:equatable/equatable.dart';
import '../../data/models/order_models.dart';

abstract class GuestCheckoutState extends Equatable {
  const GuestCheckoutState();

  @override
  List<Object?> get props => [];
}

class GuestCheckoutInitial extends GuestCheckoutState {}

class GuestCheckoutLoading extends GuestCheckoutState {}

class GuestOrderCreated extends GuestCheckoutState {
  final GuestOrderResponse orderResponse;

  const GuestOrderCreated({required this.orderResponse});

  @override
  List<Object?> get props => [orderResponse];
}

class GuestOrderTracked extends GuestCheckoutState {
  final GuestTrackingResponse trackingResponse;

  const GuestOrderTracked({required this.trackingResponse});

  @override
  List<Object?> get props => [trackingResponse];
}

class DeliveryCodeResent extends GuestCheckoutState {
  final String message;

  const DeliveryCodeResent({required this.message});

  @override
  List<Object?> get props => [message];
}

class GuestCheckoutError extends GuestCheckoutState {
  final String message;

  const GuestCheckoutError({required this.message});

  @override
  List<Object?> get props => [message];
}