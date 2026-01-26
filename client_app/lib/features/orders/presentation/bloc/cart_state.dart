import 'package:equatable/equatable.dart';
import '../../domain/entities/cart.dart';

abstract class CartState extends Equatable {
  const CartState();

  @override
  List<Object?> get props => [];
}

class CartInitial extends CartState {}

class CartLoading extends CartState {}

class CartLoaded extends CartState {
  final Cart? cart;

  const CartLoaded({this.cart});

  bool get hasCart => cart != null && cart!.isNotEmpty;
  Cart get currentCart => cart ?? Cart(
    merchantId: '',
    merchantName: '',
    items: const [],
    updatedAt: DateTime.now(),
  );

  @override
  List<Object?> get props => [cart];
}

class CartError extends CartState {
  final String message;

  const CartError({required this.message});

  @override
  List<Object?> get props => [message];
}