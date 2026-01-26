import 'package:equatable/equatable.dart';
import 'order.dart';

class Cart extends Equatable {
  final String merchantId;
  final String merchantName;
  final List<CartItem> items;
  final DateTime updatedAt;

  const Cart({
    required this.merchantId,
    required this.merchantName,
    required this.items,
    required this.updatedAt,
  });

  double get subtotal => items.fold(0.0, (sum, item) => sum + item.totalPrice);
  
  double get deliveryFee => 50.0; // TODO: Get from merchant configuration
  
  double get total => subtotal + deliveryFee;
  
  int get itemCount => items.fold(0, (sum, item) => sum + item.quantity);
  
  bool get isEmpty => items.isEmpty;
  
  bool get isNotEmpty => items.isNotEmpty;

  Cart addItem(CartItem item) {
    final existingIndex = items.indexWhere((cartItem) => 
        cartItem.productId == item.productId && 
        _modifiersEqual(cartItem.modifiers, item.modifiers));
    
    if (existingIndex >= 0) {
      final updatedItems = List<CartItem>.from(items);
      updatedItems[existingIndex] = updatedItems[existingIndex].copyWith(
        quantity: updatedItems[existingIndex].quantity + item.quantity,
      );
      return copyWith(items: updatedItems, updatedAt: DateTime.now());
    } else {
      return copyWith(
        items: [...items, item],
        updatedAt: DateTime.now(),
      );
    }
  }

  Cart removeItem(String productId, List<SelectedModifier>? modifiers) {
    final updatedItems = items.where((item) => 
        !(item.productId == productId && _modifiersEqual(item.modifiers, modifiers))).toList();
    return copyWith(items: updatedItems, updatedAt: DateTime.now());
  }

  Cart updateItemQuantity(String productId, List<SelectedModifier>? modifiers, int quantity) {
    if (quantity <= 0) {
      return removeItem(productId, modifiers);
    }
    
    final updatedItems = items.map((item) {
      if (item.productId == productId && _modifiersEqual(item.modifiers, modifiers)) {
        return item.copyWith(quantity: quantity);
      }
      return item;
    }).toList();
    
    return copyWith(items: updatedItems, updatedAt: DateTime.now());
  }

  Cart clear() {
    return copyWith(items: [], updatedAt: DateTime.now());
  }

  bool _modifiersEqual(List<SelectedModifier>? modifiers1, List<SelectedModifier>? modifiers2) {
    if (modifiers1 == null && modifiers2 == null) return true;
    if (modifiers1 == null || modifiers2 == null) return false;
    if (modifiers1.length != modifiers2.length) return false;
    
    for (int i = 0; i < modifiers1.length; i++) {
      if (modifiers1[i] != modifiers2[i]) return false;
    }
    return true;
  }

  Cart copyWith({
    String? merchantId,
    String? merchantName,
    List<CartItem>? items,
    DateTime? updatedAt,
  }) {
    return Cart(
      merchantId: merchantId ?? this.merchantId,
      merchantName: merchantName ?? this.merchantName,
      items: items ?? this.items,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }

  @override
  List<Object?> get props => [merchantId, merchantName, items, updatedAt];
}

class CartItem extends Equatable {
  final String productId;
  final String productName;
  final double price;
  final int quantity;
  final List<SelectedModifier>? modifiers;
  final String? notes;
  final String? imageUrl;

  const CartItem({
    required this.productId,
    required this.productName,
    required this.price,
    required this.quantity,
    this.modifiers,
    this.notes,
    this.imageUrl,
  });

  double get totalPrice {
    final modifierPrice = modifiers?.fold(0.0, (sum, mod) => sum + (mod.priceAdjustment ?? 0.0)) ?? 0.0;
    return (price + modifierPrice) * quantity;
  }

  CartItem copyWith({
    String? productId,
    String? productName,
    double? price,
    int? quantity,
    List<SelectedModifier>? modifiers,
    String? notes,
    String? imageUrl,
  }) {
    return CartItem(
      productId: productId ?? this.productId,
      productName: productName ?? this.productName,
      price: price ?? this.price,
      quantity: quantity ?? this.quantity,
      modifiers: modifiers ?? this.modifiers,
      notes: notes ?? this.notes,
      imageUrl: imageUrl ?? this.imageUrl,
    );
  }

  OrderItem toOrderItem() {
    return OrderItem(
      productId: productId,
      productName: productName,
      price: price,
      quantity: quantity,
      modifiers: modifiers,
      notes: notes,
    );
  }

  @override
  List<Object?> get props => [productId, productName, price, quantity, modifiers, notes, imageUrl];
}