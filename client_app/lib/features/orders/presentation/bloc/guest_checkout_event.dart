import 'package:equatable/equatable.dart';
import '../../data/models/order_models.dart';

abstract class GuestCheckoutEvent extends Equatable {
  const GuestCheckoutEvent();

  @override
  List<Object?> get props => [];
}

class CreateGuestOrder extends GuestCheckoutEvent {
  final GuestOrderRequest request;

  const CreateGuestOrder({required this.request});

  @override
  List<Object?> get props => [request];
}

class TrackGuestOrder extends GuestCheckoutEvent {
  final String trackingToken;

  const TrackGuestOrder({required this.trackingToken});

  @override
  List<Object?> get props => [trackingToken];
}

class ResendDeliveryCode extends GuestCheckoutEvent {
  final String trackingToken;

  const ResendDeliveryCode({required this.trackingToken});

  @override
  List<Object?> get props => [trackingToken];
}

class ResetGuestCheckout extends GuestCheckoutEvent {}