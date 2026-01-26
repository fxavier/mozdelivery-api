import '../entities/cart.dart';
import '../../data/models/order_models.dart';

abstract class OrderRepository {
  Future<GuestOrderResponse> createGuestOrder(GuestOrderRequest request);
  Future<GuestTrackingResponse> trackGuestOrder(String trackingToken);
  Future<void> resendGuestDeliveryCode(String trackingToken);
  
  // Cart management (local storage)
  Future<Cart?> getCart();
  Future<void> saveCart(Cart cart);
  Future<void> clearCart();
}