import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:injectable/injectable.dart';

import '../../domain/repositories/merchant_repository.dart';
import 'merchant_detail_event.dart';
import 'merchant_detail_state.dart';

@injectable
class MerchantDetailBloc extends Bloc<MerchantDetailEvent, MerchantDetailState> {
  final MerchantRepository _merchantRepository;

  MerchantDetailBloc(this._merchantRepository) : super(const MerchantDetailInitial()) {
    on<LoadMerchantDetail>(_onLoadMerchantDetail);
    on<LoadMerchantCatalogs>(_onLoadMerchantCatalogs);
    on<LoadCatalogCategories>(_onLoadCatalogCategories);
    on<LoadCategoryProducts>(_onLoadCategoryProducts);
    on<SearchProducts>(_onSearchProducts);
    on<ClearProductSearch>(_onClearProductSearch);
    on<RefreshMerchantDetail>(_onRefreshMerchantDetail);
  }

  Future<void> _onLoadMerchantDetail(
    LoadMerchantDetail event,
    Emitter<MerchantDetailState> emit,
  ) async {
    emit(const MerchantDetailLoading());

    try {
      final merchant = await _merchantRepository.getMerchant(event.merchantId);
      final catalogs = await _merchantRepository.getMerchantCatalogs(event.merchantId);

      emit(MerchantDetailLoaded(
        merchant: merchant,
        catalogs: catalogs,
      ));
    } catch (e) {
      emit(MerchantDetailError(message: e.toString()));
    }
  }

  Future<void> _onLoadMerchantCatalogs(
    LoadMerchantCatalogs event,
    Emitter<MerchantDetailState> emit,
  ) async {
    final currentState = state;
    if (currentState is MerchantDetailLoaded) {
      try {
        final catalogs = await _merchantRepository.getMerchantCatalogs(event.merchantId);
        emit(currentState.copyWith(catalogs: catalogs));
      } catch (e) {
        emit(MerchantDetailError(message: e.toString()));
      }
    }
  }

  Future<void> _onLoadCatalogCategories(
    LoadCatalogCategories event,
    Emitter<MerchantDetailState> emit,
  ) async {
    final currentState = state;
    if (currentState is MerchantDetailLoaded) {
      try {
        final categories = await _merchantRepository.getCatalogCategories(event.catalogId);
        emit(currentState.copyWith(
          categories: categories,
          selectedCatalogId: event.catalogId,
          products: [],
          filteredProducts: [],
          selectedCategoryId: null,
        ));
      } catch (e) {
        emit(MerchantDetailError(message: e.toString()));
      }
    }
  }

  Future<void> _onLoadCategoryProducts(
    LoadCategoryProducts event,
    Emitter<MerchantDetailState> emit,
  ) async {
    final currentState = state;
    if (currentState is MerchantDetailLoaded) {
      try {
        final products = await _merchantRepository.getCategoryProducts(event.categoryId);
        emit(currentState.copyWith(
          products: products,
          filteredProducts: products,
          selectedCategoryId: event.categoryId,
        ));
      } catch (e) {
        emit(MerchantDetailError(message: e.toString()));
      }
    }
  }

  Future<void> _onSearchProducts(
    SearchProducts event,
    Emitter<MerchantDetailState> emit,
  ) async {
    final currentState = state;
    if (currentState is MerchantDetailLoaded) {
      emit(currentState.copyWith(isSearchingProducts: true));

      try {
        final products = await _merchantRepository.searchProducts(
          query: event.query,
          categoryId: event.categoryId ?? currentState.selectedCategoryId,
        );

        emit(currentState.copyWith(
          filteredProducts: products,
          productSearchQuery: event.query,
          isSearchingProducts: false,
        ));
      } catch (e) {
        emit(MerchantDetailError(message: e.toString()));
      }
    }
  }

  Future<void> _onClearProductSearch(
    ClearProductSearch event,
    Emitter<MerchantDetailState> emit,
  ) async {
    final currentState = state;
    if (currentState is MerchantDetailLoaded) {
      emit(currentState.copyWith(
        filteredProducts: currentState.products,
        productSearchQuery: null,
        isSearchingProducts: false,
      ));
    }
  }

  Future<void> _onRefreshMerchantDetail(
    RefreshMerchantDetail event,
    Emitter<MerchantDetailState> emit,
  ) async {
    final currentState = state;
    if (currentState is MerchantDetailLoaded) {
      add(LoadMerchantDetail(merchantId: currentState.merchant.id));
    }
  }
}