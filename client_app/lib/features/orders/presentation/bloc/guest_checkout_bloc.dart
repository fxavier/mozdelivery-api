import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:injectable/injectable.dart';

import '../../domain/repositories/order_repository.dart';
import '../../../../core/storage/secure_storage_service.dart';
import 'guest_checkout_event.dart';
import 'guest_checkout_state.dart';

@injectable
class GuestCheckoutBloc extends Bloc<GuestCheckoutEvent, GuestCheckoutState> {
  final OrderRepository _orderRepository;
  final SecureStorageService _secureStorage;

  GuestCheckoutBloc(
    this._orderRepository,
    this._secureStorage,
  ) : super(GuestCheckoutInitial()) {
    on<CreateGuestOrder>(_onCreateGuestOrder);
    on<TrackGuestOrder>(_onTrackGuestOrder);
    on<ResendDeliveryCode>(_onResendDeliveryCode);
    on<ResetGuestCheckout>(_onResetGuestCheckout);
  }

  Future<void> _onCreateGuestOrder(CreateGuestOrder event, Emitter<GuestCheckoutState> emit) async {
    emit(GuestCheckoutLoading());
    try {
      final response = await _orderRepository.createGuestOrder(event.request);
      
      // Save guest tracking token for future use
      await _secureStorage.saveGuestToken(response.trackingToken);
      await _secureStorage.setGuestMode(true);
      
      // Clear cart after successful order
      await _orderRepository.clearCart();
      
      emit(GuestOrderCreated(orderResponse: response));
    } catch (e) {
      emit(GuestCheckoutError(message: 'Failed to create order: $e'));
    }
  }

  Future<void> _onTrackGuestOrder(TrackGuestOrder event, Emitter<GuestCheckoutState> emit) async {
    emit(GuestCheckoutLoading());
    try {
      final response = await _orderRepository.trackGuestOrder(event.trackingToken);
      emit(GuestOrderTracked(trackingResponse: response));
    } catch (e) {
      emit(GuestCheckoutError(message: 'Failed to track order: $e'));
    }
  }

  Future<void> _onResendDeliveryCode(ResendDeliveryCode event, Emitter<GuestCheckoutState> emit) async {
    emit(GuestCheckoutLoading());
    try {
      await _orderRepository.resendGuestDeliveryCode(event.trackingToken);
      emit(const DeliveryCodeResent(message: 'Delivery code has been resent'));
    } catch (e) {
      emit(GuestCheckoutError(message: 'Failed to resend delivery code: $e'));
    }
  }

  Future<void> _onResetGuestCheckout(ResetGuestCheckout event, Emitter<GuestCheckoutState> emit) async {
    emit(GuestCheckoutInitial());
  }
}