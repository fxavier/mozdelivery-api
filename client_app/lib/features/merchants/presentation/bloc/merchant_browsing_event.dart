import 'package:equatable/equatable.dart';

abstract class MerchantBrowsingEvent extends Equatable {
  const MerchantBrowsingEvent();

  @override
  List<Object?> get props => [];
}

class LoadMerchants extends MerchantBrowsingEvent {
  final String? city;
  final String? vertical;

  const LoadMerchants({this.city, this.vertical});

  @override
  List<Object?> get props => [city, vertical];
}

class SearchMerchants extends MerchantBrowsingEvent {
  final String query;
  final String? city;
  final String? vertical;

  const SearchMerchants({
    required this.query,
    this.city,
    this.vertical,
  });

  @override
  List<Object?> get props => [query, city, vertical];
}

class FilterMerchants extends MerchantBrowsingEvent {
  final String? city;
  final String? vertical;

  const FilterMerchants({this.city, this.vertical});

  @override
  List<Object?> get props => [city, vertical];
}

class ClearSearch extends MerchantBrowsingEvent {
  const ClearSearch();
}

class RefreshMerchants extends MerchantBrowsingEvent {
  const RefreshMerchants();
}