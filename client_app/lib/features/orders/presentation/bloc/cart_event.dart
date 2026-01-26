import 'package:equatable/equatable.dart';
import '../../domain/entities/cart.dart';
import '../../domain/entities/order.dart';

abstract class CartEvent extends Equatable {
  const CartEvent();

  @override
  List<Object?> get props => [];
}

class LoadCart extends CartEvent {}

class AddToCart extends CartEvent {
  final String merchantId;
  final String merchantName;
  final CartItem item;

  const AddToCart({
    required this.merchantId,
    required this.merchantName,
    required this.item,
  });

  @override
  List<Object?> get props => [merchantId, merchantName, item];
}

class RemoveFromCart extends CartEvent {
  final String productId;
  final List<SelectedModifier>? modifiers;

  const RemoveFromCart({
    required this.productId,
    this.modifiers,
  });

  @override
  List<Object?> get props => [productId, modifiers];
}

class UpdateCartItemQuantity extends CartEvent {
  final String productId;
  final List<SelectedModifier>? modifiers;
  final int quantity;

  const UpdateCartItemQuantity({
    required this.productId,
    this.modifiers,
    required this.quantity,
  });

  @override
  List<Object?> get props => [productId, modifiers, quantity];
}

class ClearCart extends CartEvent {}

class SwitchMerchant extends CartEvent {
  final String newMerchantId;
  final String newMerchantName;

  const SwitchMerchant({
    required this.newMerchantId,
    required this.newMerchantName,
  });

  @override
  List<Object?> get props => [newMerchantId, newMerchantName];
}