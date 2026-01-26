import 'dart:convert';
import 'package:injectable/injectable.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../domain/repositories/order_repository.dart';
import '../../domain/entities/cart.dart';
import '../../domain/entities/order.dart';
import '../models/order_models.dart';
import '../../../../core/network/api_client.dart';

@LazySingleton(as: OrderRepository)
class OrderRepositoryImpl implements OrderRepository {
  final ApiService _apiService;
  final SharedPreferences _prefs;

  static const String _cartKey = 'cart_data';

  OrderRepositoryImpl(this._apiService, this._prefs);

  @override
  Future<GuestOrderResponse> createGuestOrder(GuestOrderRequest request) async {
    final response = await _apiService.createGuestOrder(request.toJson());
    return GuestOrderResponse.fromJson(response);
  }

  @override
  Future<GuestTrackingResponse> trackGuestOrder(String trackingToken) async {
    final response = await _apiService.trackGuestOrder(trackingToken);
    return GuestTrackingResponse.fromJson(response);
  }

  @override
  Future<void> resendGuestDeliveryCode(String trackingToken) async {
    await _apiService.handleApiCall(() async {
      // For now, we'll create a direct call since we need access to the resend method
      throw UnimplementedError('Resend delivery code not yet implemented in API client');
    });
  }

  @override
  Future<Cart?> getCart() async {
    final cartJson = _prefs.getString(_cartKey);
    if (cartJson == null) return null;
    
    try {
      final cartData = json.decode(cartJson);
      return _cartFromJson(cartData);
    } catch (e) {
      // If cart data is corrupted, return null
      return null;
    }
  }

  @override
  Future<void> saveCart(Cart cart) async {
    final cartJson = json.encode(_cartToJson(cart));
    await _prefs.setString(_cartKey, cartJson);
  }

  @override
  Future<void> clearCart() async {
    await _prefs.remove(_cartKey);
  }

  Cart _cartFromJson(Map<String, dynamic> json) {
    return Cart(
      merchantId: json['merchantId'],
      merchantName: json['merchantName'],
      items: (json['items'] as List)
          .map((item) => _cartItemFromJson(item))
          .toList(),
      updatedAt: DateTime.parse(json['updatedAt']),
    );
  }

  CartItem _cartItemFromJson(Map<String, dynamic> json) {
    return CartItem(
      productId: json['productId'],
      productName: json['productName'],
      price: json['price'].toDouble(),
      quantity: json['quantity'],
      modifiers: json['modifiers'] != null
          ? (json['modifiers'] as List)
              .map((mod) => _selectedModifierFromJson(mod))
              .toList()
          : null,
      notes: json['notes'],
      imageUrl: json['imageUrl'],
    );
  }

  SelectedModifier _selectedModifierFromJson(Map<String, dynamic> json) {
    return SelectedModifier(
      modifierId: json['modifierId'],
      optionId: json['optionId'],
      optionName: json['optionName'],
      priceAdjustment: json['priceAdjustment']?.toDouble(),
    );
  }

  Map<String, dynamic> _cartToJson(Cart cart) {
    return {
      'merchantId': cart.merchantId,
      'merchantName': cart.merchantName,
      'items': cart.items.map((item) => _cartItemToJson(item)).toList(),
      'updatedAt': cart.updatedAt.toIso8601String(),
    };
  }

  Map<String, dynamic> _cartItemToJson(CartItem item) {
    return {
      'productId': item.productId,
      'productName': item.productName,
      'price': item.price,
      'quantity': item.quantity,
      'modifiers': item.modifiers?.map((mod) => _selectedModifierToJson(mod)).toList(),
      'notes': item.notes,
      'imageUrl': item.imageUrl,
    };
  }

  Map<String, dynamic> _selectedModifierToJson(SelectedModifier modifier) {
    return {
      'modifierId': modifier.modifierId,
      'optionId': modifier.optionId,
      'optionName': modifier.optionName,
      'priceAdjustment': modifier.priceAdjustment,
    };
  }
}