import 'package:flutter_bloc/flutter_bloc.dart';
import '../../domain/repositories/delivery_repository.dart';
import 'delivery_event.dart';
import 'delivery_state.dart';

class DeliveryBloc extends Bloc<DeliveryEvent, DeliveryState> {
  final DeliveryRepository _deliveryRepository;

  DeliveryBloc(this._deliveryRepository) : super(DeliveryInitial()) {
    on<LoadAssignedDeliveries>(_onLoadAssignedDeliveries);
    on<AcceptDelivery>(_onAcceptDelivery);
    on<UpdateDeliveryStatus>(_onUpdateDeliveryStatus);
    on<CompleteDelivery>(_onCompleteDelivery);
    on<LoadOptimizedRoute>(_onLoadOptimizedRoute);
    on<RefreshDeliveries>(_onRefreshDeliveries);
  }

  Future<void> _onLoadAssignedDeliveries(
    LoadAssignedDeliveries event,
    Emitter<DeliveryState> emit,
  ) async {
    emit(DeliveryLoading());
    try {
      final deliveries = await _deliveryRepository.getAssignedDeliveries();
      emit(DeliveryLoaded(deliveries: deliveries));
    } catch (e) {
      emit(DeliveryError('Failed to load deliveries: ${e.toString()}'));
    }
  }

  Future<void> _onAcceptDelivery(
    AcceptDelivery event,
    Emitter<DeliveryState> emit,
  ) async {
    emit(const DeliveryActionLoading('Accepting delivery...'));
    try {
      await _deliveryRepository.acceptDelivery(event.deliveryId);
      emit(const DeliveryActionSuccess('Delivery accepted successfully'));
      
      // Refresh deliveries after accepting
      add(LoadAssignedDeliveries());
    } catch (e) {
      emit(DeliveryActionError('Failed to accept delivery: ${e.toString()}'));
    }
  }

  Future<void> _onUpdateDeliveryStatus(
    UpdateDeliveryStatus event,
    Emitter<DeliveryState> emit,
  ) async {
    emit(const DeliveryActionLoading('Updating status...'));
    try {
      await _deliveryRepository.updateDeliveryStatus(
        event.deliveryId,
        event.status,
        latitude: event.latitude,
        longitude: event.longitude,
        notes: event.notes,
      );
      emit(const DeliveryActionSuccess('Status updated successfully'));
      
      // Refresh deliveries after status update
      add(LoadAssignedDeliveries());
    } catch (e) {
      emit(DeliveryActionError('Failed to update status: ${e.toString()}'));
    }
  }

  Future<void> _onCompleteDelivery(
    CompleteDelivery event,
    Emitter<DeliveryState> emit,
  ) async {
    emit(const DeliveryActionLoading('Completing delivery...'));
    try {
      await _deliveryRepository.completeDelivery(
        event.deliveryId,
        event.confirmationCode,
        latitude: event.latitude,
        longitude: event.longitude,
        notes: event.notes,
        proofOfDeliveryImageUrl: event.proofOfDeliveryImageUrl,
      );
      emit(const DeliveryActionSuccess('Delivery completed successfully'));
      
      // Refresh deliveries after completion
      add(LoadAssignedDeliveries());
    } catch (e) {
      emit(DeliveryActionError('Failed to complete delivery: ${e.toString()}'));
    }
  }

  Future<void> _onLoadOptimizedRoute(
    LoadOptimizedRoute event,
    Emitter<DeliveryState> emit,
  ) async {
    if (state is DeliveryLoaded) {
      final currentState = state as DeliveryLoaded;
      try {
        final route = await _deliveryRepository.getOptimizedRoute(event.deliveryId);
        emit(currentState.copyWith(optimizedRoute: route));
      } catch (e) {
        emit(DeliveryError('Failed to load route: ${e.toString()}'));
      }
    }
  }

  Future<void> _onRefreshDeliveries(
    RefreshDeliveries event,
    Emitter<DeliveryState> emit,
  ) async {
    add(LoadAssignedDeliveries());
  }
}