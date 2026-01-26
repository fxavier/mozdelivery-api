import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:injectable/injectable.dart';

import '../../domain/repositories/merchant_repository.dart';
import 'merchant_browsing_event.dart';
import 'merchant_browsing_state.dart';

@injectable
class MerchantBrowsingBloc extends Bloc<MerchantBrowsingEvent, MerchantBrowsingState> {
  final MerchantRepository _merchantRepository;

  MerchantBrowsingBloc(this._merchantRepository) : super(const MerchantBrowsingInitial()) {
    on<LoadMerchants>(_onLoadMerchants);
    on<SearchMerchants>(_onSearchMerchants);
    on<FilterMerchants>(_onFilterMerchants);
    on<ClearSearch>(_onClearSearch);
    on<RefreshMerchants>(_onRefreshMerchants);
  }

  Future<void> _onLoadMerchants(
    LoadMerchants event,
    Emitter<MerchantBrowsingState> emit,
  ) async {
    emit(const MerchantBrowsingLoading());

    try {
      final merchants = await _merchantRepository.getMerchants(
        city: event.city,
        vertical: event.vertical,
      );

      emit(MerchantBrowsingLoaded(
        merchants: merchants,
        filteredMerchants: merchants,
        currentCity: event.city,
        currentVertical: event.vertical,
      ));
    } catch (e) {
      emit(MerchantBrowsingError(message: e.toString()));
    }
  }

  Future<void> _onSearchMerchants(
    SearchMerchants event,
    Emitter<MerchantBrowsingState> emit,
  ) async {
    final currentState = state;
    if (currentState is MerchantBrowsingLoaded) {
      emit(currentState.copyWith(isSearching: true));

      try {
        final merchants = await _merchantRepository.searchMerchants(
          query: event.query,
          city: event.city ?? currentState.currentCity,
          vertical: event.vertical ?? currentState.currentVertical,
        );

        emit(currentState.copyWith(
          filteredMerchants: merchants,
          searchQuery: event.query,
          isSearching: false,
        ));
      } catch (e) {
        emit(MerchantBrowsingError(message: e.toString()));
      }
    }
  }

  Future<void> _onFilterMerchants(
    FilterMerchants event,
    Emitter<MerchantBrowsingState> emit,
  ) async {
    emit(const MerchantBrowsingLoading());

    try {
      final merchants = await _merchantRepository.getMerchants(
        city: event.city,
        vertical: event.vertical,
      );

      emit(MerchantBrowsingLoaded(
        merchants: merchants,
        filteredMerchants: merchants,
        currentCity: event.city,
        currentVertical: event.vertical,
      ));
    } catch (e) {
      emit(MerchantBrowsingError(message: e.toString()));
    }
  }

  Future<void> _onClearSearch(
    ClearSearch event,
    Emitter<MerchantBrowsingState> emit,
  ) async {
    final currentState = state;
    if (currentState is MerchantBrowsingLoaded) {
      emit(currentState.copyWith(
        filteredMerchants: currentState.merchants,
        searchQuery: null,
        isSearching: false,
      ));
    }
  }

  Future<void> _onRefreshMerchants(
    RefreshMerchants event,
    Emitter<MerchantBrowsingState> emit,
  ) async {
    final currentState = state;
    if (currentState is MerchantBrowsingLoaded) {
      add(LoadMerchants(
        city: currentState.currentCity,
        vertical: currentState.currentVertical,
      ));
    } else {
      add(const LoadMerchants());
    }
  }
}