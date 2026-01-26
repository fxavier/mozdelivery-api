import 'package:equatable/equatable.dart';

import '../../data/models/merchant_model.dart';

abstract class MerchantBrowsingState extends Equatable {
  const MerchantBrowsingState();

  @override
  List<Object?> get props => [];
}

class MerchantBrowsingInitial extends MerchantBrowsingState {
  const MerchantBrowsingInitial();
}

class MerchantBrowsingLoading extends MerchantBrowsingState {
  const MerchantBrowsingLoading();
}

class MerchantBrowsingLoaded extends MerchantBrowsingState {
  final List<MerchantModel> merchants;
  final List<MerchantModel> filteredMerchants;
  final String? currentCity;
  final String? currentVertical;
  final String? searchQuery;
  final bool isSearching;

  const MerchantBrowsingLoaded({
    required this.merchants,
    required this.filteredMerchants,
    this.currentCity,
    this.currentVertical,
    this.searchQuery,
    this.isSearching = false,
  });

  @override
  List<Object?> get props => [
        merchants,
        filteredMerchants,
        currentCity,
        currentVertical,
        searchQuery,
        isSearching,
      ];

  MerchantBrowsingLoaded copyWith({
    List<MerchantModel>? merchants,
    List<MerchantModel>? filteredMerchants,
    String? currentCity,
    String? currentVertical,
    String? searchQuery,
    bool? isSearching,
  }) {
    return MerchantBrowsingLoaded(
      merchants: merchants ?? this.merchants,
      filteredMerchants: filteredMerchants ?? this.filteredMerchants,
      currentCity: currentCity ?? this.currentCity,
      currentVertical: currentVertical ?? this.currentVertical,
      searchQuery: searchQuery ?? this.searchQuery,
      isSearching: isSearching ?? this.isSearching,
    );
  }
}

class MerchantBrowsingError extends MerchantBrowsingState {
  final String message;

  const MerchantBrowsingError({required this.message});

  @override
  List<Object?> get props => [message];
}