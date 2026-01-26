import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:injectable/injectable.dart';

import '../../domain/repositories/order_repository.dart';
import '../../domain/entities/cart.dart';
import 'cart_event.dart';
import 'cart_state.dart';

@injectable
class CartBloc extends Bloc<CartEvent, CartState> {
  final OrderRepository _orderRepository;

  CartBloc(this._orderRepository) : super(CartInitial()) {
    on<LoadCart>(_onLoadCart);
    on<AddToCart>(_onAddToCart);
    on<RemoveFromCart>(_onRemoveFromCart);
    on<UpdateCartItemQuantity>(_onUpdateCartItemQuantity);
    on<ClearCart>(_onClearCart);
    on<SwitchMerchant>(_onSwitchMerchant);
  }

  Future<void> _onLoadCart(LoadCart event, Emitter<CartState> emit) async {
    emit(CartLoading());
    try {
      final cart = await _orderRepository.getCart();
      emit(CartLoaded(cart: cart));
    } catch (e) {
      emit(CartError(message: 'Failed to load cart: $e'));
    }
  }

  Future<void> _onAddToCart(AddToCart event, Emitter<CartState> emit) async {
    try {
      final currentCart = await _orderRepository.getCart();
      
      Cart updatedCart;
      if (currentCart == null || currentCart.isEmpty) {
        // Create new cart
        updatedCart = Cart(
          merchantId: event.merchantId,
          merchantName: event.merchantName,
          items: [event.item],
          updatedAt: DateTime.now(),
        );
      } else if (currentCart.merchantId != event.merchantId) {
        // Different merchant - show confirmation dialog in UI
        emit(CartError(message: 'DIFFERENT_MERCHANT'));
        return;
      } else {
        // Same merchant - add to existing cart
        updatedCart = currentCart.addItem(event.item);
      }
      
      await _orderRepository.saveCart(updatedCart);
      emit(CartLoaded(cart: updatedCart));
    } catch (e) {
      emit(CartError(message: 'Failed to add item to cart: $e'));
    }
  }

  Future<void> _onRemoveFromCart(RemoveFromCart event, Emitter<CartState> emit) async {
    try {
      final currentCart = await _orderRepository.getCart();
      if (currentCart == null) return;
      
      final updatedCart = currentCart.removeItem(event.productId, event.modifiers);
      
      if (updatedCart.isEmpty) {
        await _orderRepository.clearCart();
        emit(const CartLoaded(cart: null));
      } else {
        await _orderRepository.saveCart(updatedCart);
        emit(CartLoaded(cart: updatedCart));
      }
    } catch (e) {
      emit(CartError(message: 'Failed to remove item from cart: $e'));
    }
  }

  Future<void> _onUpdateCartItemQuantity(UpdateCartItemQuantity event, Emitter<CartState> emit) async {
    try {
      final currentCart = await _orderRepository.getCart();
      if (currentCart == null) return;
      
      final updatedCart = currentCart.updateItemQuantity(
        event.productId,
        event.modifiers,
        event.quantity,
      );
      
      if (updatedCart.isEmpty) {
        await _orderRepository.clearCart();
        emit(const CartLoaded(cart: null));
      } else {
        await _orderRepository.saveCart(updatedCart);
        emit(CartLoaded(cart: updatedCart));
      }
    } catch (e) {
      emit(CartError(message: 'Failed to update cart item: $e'));
    }
  }

  Future<void> _onClearCart(ClearCart event, Emitter<CartState> emit) async {
    try {
      await _orderRepository.clearCart();
      emit(const CartLoaded(cart: null));
    } catch (e) {
      emit(CartError(message: 'Failed to clear cart: $e'));
    }
  }

  Future<void> _onSwitchMerchant(SwitchMerchant event, Emitter<CartState> emit) async {
    try {
      // Clear current cart and create new empty cart for new merchant
      await _orderRepository.clearCart();
      final newCart = Cart(
        merchantId: event.newMerchantId,
        merchantName: event.newMerchantName,
        items: const [],
        updatedAt: DateTime.now(),
      );
      await _orderRepository.saveCart(newCart);
      emit(CartLoaded(cart: newCart));
    } catch (e) {
      emit(CartError(message: 'Failed to switch merchant: $e'));
    }
  }
}