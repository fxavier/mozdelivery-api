import 'dart:async';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:injectable/injectable.dart';

import '../../domain/repositories/order_repository.dart';
import '../../../../core/errors/exceptions.dart';
import 'order_tracking_event.dart';
import 'order_tracking_state.dart';

@injectable
class OrderTrackingBloc extends Bloc<OrderTrackingEvent, OrderTrackingState> {
  final OrderRepository _orderRepository;
  Timer? _refreshTimer;
  String? _currentTrackingToken;

  OrderTrackingBloc(this._orderRepository) : super(const OrderTrackingInitial()) {
    on<LoadOrderTracking>(_onLoadOrderTracking);
    on<RefreshOrderStatus>(_onRefreshOrderStatus);
    on<ResendDeliveryCode>(_onResendDeliveryCode);
    on<StartRealTimeUpdates>(_onStartRealTimeUpdates);
    on<StopRealTimeUpdates>(_onStopRealTimeUpdates);
    on<OrderStatusUpdated>(_onOrderStatusUpdated);
  }

  @override
  Future<void> close() {
    _refreshTimer?.cancel();
    return super.close();
  }

  Future<void> _onLoadOrderTracking(
    LoadOrderTracking event,
    Emitter<OrderTrackingState> emit,
  ) async {
    emit(const OrderTrackingLoading());

    try {
      _currentTrackingToken = event.guestToken;

      if (event.guestToken != null) {
        // Guest order tracking
        final orderData = await _orderRepository.trackGuestOrder(event.guestToken!);
        emit(OrderTrackingLoaded(
          orderData: orderData,
          lastUpdated: DateTime.now(),
        ));
      } else if (event.orderId != null) {
        // Registered user order tracking - for now, we'll use the same guest tracking
        // In a real implementation, this would call a different API endpoint
        throw UnimplementedError('Registered user order tracking not yet implemented');
      } else {
        emit(const OrderTrackingError(
          message: 'Either order ID or guest token must be provided',
          errorCode: 'INVALID_PARAMETERS',
        ));
      }
    } catch (e) {
      emit(OrderTrackingError(
        message: _getErrorMessage(e),
        errorCode: _getErrorCode(e),
      ));
    }
  }

  Future<void> _onRefreshOrderStatus(
    RefreshOrderStatus event,
    Emitter<OrderTrackingState> emit,
  ) async {
    if (state is! OrderTrackingLoaded) return;

    try {
      if (_currentTrackingToken != null) {
        final orderData = await _orderRepository.trackGuestOrder(_currentTrackingToken!);
        final currentState = state as OrderTrackingLoaded;
        emit(currentState.copyWith(
          orderData: orderData,
          lastUpdated: DateTime.now(),
        ));
      }
    } catch (e) {
      // Don't emit error state on refresh failure, just keep current state
      // Could emit a snackbar message instead
    }
  }

  Future<void> _onResendDeliveryCode(
    ResendDeliveryCode event,
    Emitter<OrderTrackingState> emit,
  ) async {
    if (state is! OrderTrackingLoaded) return;

    final currentState = state as OrderTrackingLoaded;
    emit(DeliveryCodeResending(orderData: currentState.orderData));

    try {
      if (_currentTrackingToken != null) {
        await _orderRepository.resendGuestDeliveryCode(_currentTrackingToken!);
        emit(DeliveryCodeResent(
          orderData: currentState.orderData,
          message: 'Delivery confirmation code has been resent',
        ));
        
        // Return to loaded state after a brief delay
        await Future.delayed(const Duration(seconds: 2));
        emit(currentState.copyWith(lastUpdated: DateTime.now()));
      }
    } catch (e) {
      emit(OrderTrackingError(
        message: _getErrorMessage(e),
        errorCode: _getErrorCode(e),
      ));
    }
  }

  void _onStartRealTimeUpdates(
    StartRealTimeUpdates event,
    Emitter<OrderTrackingState> emit,
  ) {
    if (state is! OrderTrackingLoaded) return;

    final currentState = state as OrderTrackingLoaded;
    emit(currentState.copyWith(isRealTimeActive: true));

    // Start periodic refresh every 30 seconds
    _refreshTimer?.cancel();
    _refreshTimer = Timer.periodic(const Duration(seconds: 30), (timer) {
      add(const RefreshOrderStatus());
    });
  }

  void _onStopRealTimeUpdates(
    StopRealTimeUpdates event,
    Emitter<OrderTrackingState> emit,
  ) {
    if (state is! OrderTrackingLoaded) return;

    final currentState = state as OrderTrackingLoaded;
    emit(currentState.copyWith(isRealTimeActive: false));

    _refreshTimer?.cancel();
    _refreshTimer = null;
  }

  void _onOrderStatusUpdated(
    OrderStatusUpdated event,
    Emitter<OrderTrackingState> emit,
  ) {
    if (state is! OrderTrackingLoaded) return;

    // Update the order status in the current data
    // This would typically come from a WebSocket or push notification
    // For now, we'll just trigger a refresh
    add(const RefreshOrderStatus());
  }

  String _getErrorMessage(dynamic error) {
    if (error is NetworkException) {
      return error.message;
    } else if (error is ServerException) {
      return error.message;
    } else if (error is ValidationException) {
      return error.message;
    } else {
      return 'An unexpected error occurred. Please try again.';
    }
  }

  String? _getErrorCode(dynamic error) {
    if (error is ServerException) {
      return error.statusCode?.toString();
    } else if (error is ValidationException) {
      return 'VALIDATION_ERROR';
    } else if (error is NetworkException) {
      return 'NETWORK_ERROR';
    }
    return null;
  }
}