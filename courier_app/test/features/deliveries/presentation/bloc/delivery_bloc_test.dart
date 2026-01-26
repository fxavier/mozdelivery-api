import 'package:bloc_test/bloc_test.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:mockito/annotations.dart';

import 'package:courier_app/features/deliveries/domain/entities/delivery.dart';
import 'package:courier_app/features/deliveries/domain/repositories/delivery_repository.dart';
import 'package:courier_app/features/deliveries/presentation/bloc/delivery_bloc.dart';
import 'package:courier_app/features/deliveries/presentation/bloc/delivery_event.dart';
import 'package:courier_app/features/deliveries/presentation/bloc/delivery_state.dart';

import 'delivery_bloc_test.mocks.dart';

@GenerateMocks([DeliveryRepository])
void main() {
  group('DeliveryBloc', () {
    late MockDeliveryRepository mockRepository;
    late DeliveryBloc deliveryBloc;

    setUp(() {
      mockRepository = MockDeliveryRepository();
      deliveryBloc = DeliveryBloc(mockRepository);
    });

    tearDown(() {
      deliveryBloc.close();
    });

    test('initial state is DeliveryInitial', () {
      expect(deliveryBloc.state, equals(DeliveryInitial()));
    });

    group('LoadAssignedDeliveries', () {
      final mockDeliveries = [
        Delivery(
          deliveryId: '1',
          orderId: 'order-1',
          merchantName: 'Test Merchant',
          merchantAddress: 'Test Address',
          customerName: 'Test Customer',
          customerPhone: '+258123456789',
          deliveryAddress: 'Delivery Address',
          deliveryLatitude: -25.9664,
          deliveryLongitude: 32.5832,
          status: DeliveryStatus.assigned,
          totalAmount: 100.0,
          currency: 'MZN',
          estimatedPickupTime: DateTime.now(),
          estimatedDeliveryTime: DateTime.now().add(const Duration(hours: 1)),
          items: [],
        ),
      ];

      blocTest<DeliveryBloc, DeliveryState>(
        'emits [DeliveryLoading, DeliveryLoaded] when deliveries are loaded successfully',
        build: () {
          when(mockRepository.getAssignedDeliveries())
              .thenAnswer((_) async => mockDeliveries);
          return deliveryBloc;
        },
        act: (bloc) => bloc.add(LoadAssignedDeliveries()),
        expect: () => [
          DeliveryLoading(),
          DeliveryLoaded(deliveries: mockDeliveries),
        ],
      );

      blocTest<DeliveryBloc, DeliveryState>(
        'emits [DeliveryLoading, DeliveryError] when loading deliveries fails',
        build: () {
          when(mockRepository.getAssignedDeliveries())
              .thenThrow(Exception('Failed to load deliveries'));
          return deliveryBloc;
        },
        act: (bloc) => bloc.add(LoadAssignedDeliveries()),
        expect: () => [
          DeliveryLoading(),
          const DeliveryError('Failed to load deliveries: Exception: Failed to load deliveries'),
        ],
      );
    });

    group('AcceptDelivery', () {
      blocTest<DeliveryBloc, DeliveryState>(
        'emits [DeliveryActionLoading, DeliveryActionSuccess, DeliveryLoading, DeliveryLoaded] when delivery is accepted successfully',
        build: () {
          when(mockRepository.acceptDelivery('1'))
              .thenAnswer((_) async {});
          when(mockRepository.getAssignedDeliveries())
              .thenAnswer((_) async => []);
          return deliveryBloc;
        },
        act: (bloc) => bloc.add(const AcceptDelivery('1')),
        expect: () => [
          const DeliveryActionLoading('Accepting delivery...'),
          const DeliveryActionSuccess('Delivery accepted successfully'),
          DeliveryLoading(),
          const DeliveryLoaded(deliveries: []),
        ],
      );

      blocTest<DeliveryBloc, DeliveryState>(
        'emits [DeliveryActionLoading, DeliveryActionError] when accepting delivery fails',
        build: () {
          when(mockRepository.acceptDelivery('1'))
              .thenThrow(Exception('Failed to accept delivery'));
          return deliveryBloc;
        },
        act: (bloc) => bloc.add(const AcceptDelivery('1')),
        expect: () => [
          const DeliveryActionLoading('Accepting delivery...'),
          const DeliveryActionError('Failed to accept delivery: Exception: Failed to accept delivery'),
        ],
      );
    });

    group('UpdateDeliveryStatus', () {
      blocTest<DeliveryBloc, DeliveryState>(
        'emits [DeliveryActionLoading, DeliveryActionSuccess, DeliveryLoading, DeliveryLoaded] when status is updated successfully',
        build: () {
          when(mockRepository.updateDeliveryStatus('1', DeliveryStatus.pickedUp))
              .thenAnswer((_) async {});
          when(mockRepository.getAssignedDeliveries())
              .thenAnswer((_) async => []);
          return deliveryBloc;
        },
        act: (bloc) => bloc.add(const UpdateDeliveryStatus('1', DeliveryStatus.pickedUp)),
        expect: () => [
          const DeliveryActionLoading('Updating status...'),
          const DeliveryActionSuccess('Status updated successfully'),
          DeliveryLoading(),
          const DeliveryLoaded(deliveries: []),
        ],
      );
    });

    group('CompleteDelivery', () {
      blocTest<DeliveryBloc, DeliveryState>(
        'emits [DeliveryActionLoading, DeliveryActionSuccess, DeliveryLoading, DeliveryLoaded] when delivery is completed successfully',
        build: () {
          when(mockRepository.completeDelivery(
            '1',
            '1234',
            latitude: -25.9664,
            longitude: 32.5832,
          )).thenAnswer((_) async {});
          when(mockRepository.getAssignedDeliveries())
              .thenAnswer((_) async => []);
          return deliveryBloc;
        },
        act: (bloc) => bloc.add(const CompleteDelivery('1', '1234', -25.9664, 32.5832)),
        expect: () => [
          const DeliveryActionLoading('Completing delivery...'),
          const DeliveryActionSuccess('Delivery completed successfully'),
          DeliveryLoading(),
          const DeliveryLoaded(deliveries: []),
        ],
      );
    });
  });
}