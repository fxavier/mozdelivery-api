import 'package:bloc_test/bloc_test.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';

import 'package:client_app/features/merchants/presentation/bloc/merchant_browsing_bloc.dart';
import 'package:client_app/features/merchants/presentation/bloc/merchant_browsing_event.dart';
import 'package:client_app/features/merchants/presentation/bloc/merchant_browsing_state.dart';
import 'package:client_app/features/merchants/domain/repositories/merchant_repository.dart';
import 'package:client_app/features/merchants/data/models/merchant_model.dart';

class MockMerchantRepository extends Mock implements MerchantRepository {}

void main() {
  group('MerchantBrowsingBloc', () {
    late MerchantRepository mockRepository;
    late MerchantBrowsingBloc bloc;

    setUp(() {
      mockRepository = MockMerchantRepository();
      bloc = MerchantBrowsingBloc(mockRepository);
    });

    tearDown(() {
      bloc.close();
    });

    test('initial state is MerchantBrowsingInitial', () {
      expect(bloc.state, equals(const MerchantBrowsingInitial()));
    });

    group('LoadMerchants', () {
      final mockMerchants = [
        const MerchantModel(
          id: '1',
          businessName: 'Test Restaurant',
          displayName: 'Test Restaurant',
          vertical: 'restaurant',
          status: 'active',
          description: 'A test restaurant',
          city: 'Maputo',
          rating: 4.5,
          reviewCount: 100,
          isOpen: true,
          deliveryFee: 2.50,
          estimatedDeliveryTime: 30,
        ),
      ];

      blocTest<MerchantBrowsingBloc, MerchantBrowsingState>(
        'emits [MerchantBrowsingLoading, MerchantBrowsingLoaded] when LoadMerchants is added',
        build: () {
          when(() => mockRepository.getMerchants(city: any(named: 'city'), vertical: any(named: 'vertical')))
              .thenAnswer((_) async => mockMerchants);
          return bloc;
        },
        act: (bloc) => bloc.add(const LoadMerchants()),
        expect: () => [
          const MerchantBrowsingLoading(),
          MerchantBrowsingLoaded(
            merchants: mockMerchants,
            filteredMerchants: mockMerchants,
          ),
        ],
      );

      blocTest<MerchantBrowsingBloc, MerchantBrowsingState>(
        'emits [MerchantBrowsingLoading, MerchantBrowsingError] when repository throws',
        build: () {
          when(() => mockRepository.getMerchants(city: any(named: 'city'), vertical: any(named: 'vertical')))
              .thenThrow(Exception('Network error'));
          return bloc;
        },
        act: (bloc) => bloc.add(const LoadMerchants()),
        expect: () => [
          const MerchantBrowsingLoading(),
          const MerchantBrowsingError(message: 'Exception: Network error'),
        ],
      );
    });

    group('SearchMerchants', () {
      final mockMerchants = [
        const MerchantModel(
          id: '1',
          businessName: 'Pizza Palace',
          displayName: 'Pizza Palace',
          vertical: 'restaurant',
          status: 'active',
        ),
        const MerchantModel(
          id: '2',
          businessName: 'Burger Joint',
          displayName: 'Burger Joint',
          vertical: 'restaurant',
          status: 'active',
        ),
      ];

      blocTest<MerchantBrowsingBloc, MerchantBrowsingState>(
        'filters merchants based on search query',
        build: () {
          when(() => mockRepository.searchMerchants(
                query: any(named: 'query'),
                city: any(named: 'city'),
                vertical: any(named: 'vertical'),
              )).thenAnswer((_) async => [mockMerchants.first]);
          return bloc;
        },
        seed: () => MerchantBrowsingLoaded(
          merchants: mockMerchants,
          filteredMerchants: mockMerchants,
        ),
        act: (bloc) => bloc.add(const SearchMerchants(query: 'pizza')),
        expect: () => [
          MerchantBrowsingLoaded(
            merchants: mockMerchants,
            filteredMerchants: mockMerchants,
            isSearching: true,
          ),
          MerchantBrowsingLoaded(
            merchants: mockMerchants,
            filteredMerchants: [mockMerchants.first],
            searchQuery: 'pizza',
            isSearching: false,
          ),
        ],
      );
    });
  });
}