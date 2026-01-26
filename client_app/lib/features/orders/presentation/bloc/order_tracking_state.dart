import 'package:equatable/equatable.dart';

import '../../data/models/order_models.dart';
import '../../domain/entities/order.dart';

abstract class OrderTrackingState extends Equatable {
  const OrderTrackingState();

  @override
  List<Object?> get props => [];
}

class OrderTrackingInitial extends OrderTrackingState {
  const OrderTrackingInitial();
}

class OrderTrackingLoading extends OrderTrackingState {
  const OrderTrackingLoading();
}

class OrderTrackingLoaded extends OrderTrackingState {
  final GuestTrackingResponse orderData;
  final bool isRealTimeActive;
  final DateTime lastUpdated;

  const OrderTrackingLoaded({
    required this.orderData,
    this.isRealTimeActive = false,
    required this.lastUpdated,
  });

  @override
  List<Object?> get props => [orderData, isRealTimeActive, lastUpdated];

  OrderTrackingLoaded copyWith({
    GuestTrackingResponse? orderData,
    bool? isRealTimeActive,
    DateTime? lastUpdated,
  }) {
    return OrderTrackingLoaded(
      orderData: orderData ?? this.orderData,
      isRealTimeActive: isRealTimeActive ?? this.isRealTimeActive,
      lastUpdated: lastUpdated ?? this.lastUpdated,
    );
  }

  OrderStatus get orderStatus => OrderStatus.fromString(orderData.status);
  
  bool get canResendCode {
    final status = orderStatus;
    return status == OrderStatus.paymentConfirmed ||
           status == OrderStatus.preparing ||
           status == OrderStatus.readyForPickup ||
           status == OrderStatus.pickedUp ||
           status == OrderStatus.outForDelivery;
  }

  bool get showDeliveryCode {
    return orderData.deliveryConfirmationCode != null && canResendCode;
  }
}

class OrderTrackingError extends OrderTrackingState {
  final String message;
  final String? errorCode;

  const OrderTrackingError({
    required this.message,
    this.errorCode,
  });

  @override
  List<Object?> get props => [message, errorCode];
}

class DeliveryCodeResending extends OrderTrackingState {
  final GuestTrackingResponse orderData;

  const DeliveryCodeResending({
    required this.orderData,
  });

  @override
  List<Object?> get props => [orderData];
}

class DeliveryCodeResent extends OrderTrackingState {
  final GuestTrackingResponse orderData;
  final String message;

  const DeliveryCodeResent({
    required this.orderData,
    required this.message,
  });

  @override
  List<Object?> get props => [orderData, message];
}