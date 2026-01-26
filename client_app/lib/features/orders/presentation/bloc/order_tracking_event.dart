import 'package:equatable/equatable.dart';

abstract class OrderTrackingEvent extends Equatable {
  const OrderTrackingEvent();

  @override
  List<Object?> get props => [];
}

class LoadOrderTracking extends OrderTrackingEvent {
  final String? orderId;
  final String? guestToken;

  const LoadOrderTracking({
    this.orderId,
    this.guestToken,
  });

  @override
  List<Object?> get props => [orderId, guestToken];
}

class RefreshOrderStatus extends OrderTrackingEvent {
  const RefreshOrderStatus();
}

class ResendDeliveryCode extends OrderTrackingEvent {
  const ResendDeliveryCode();
}

class StartRealTimeUpdates extends OrderTrackingEvent {
  const StartRealTimeUpdates();
}

class StopRealTimeUpdates extends OrderTrackingEvent {
  const StopRealTimeUpdates();
}

class OrderStatusUpdated extends OrderTrackingEvent {
  final String status;
  final DateTime updatedAt;

  const OrderStatusUpdated({
    required this.status,
    required this.updatedAt,
  });

  @override
  List<Object?> get props => [status, updatedAt];
}