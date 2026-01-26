import '../entities/delivery.dart';

abstract class DeliveryRepository {
  Future<List<Delivery>> getAssignedDeliveries();
  Future<Delivery> getDeliveryById(String deliveryId);
  Future<void> acceptDelivery(String deliveryId);
  Future<void> updateDeliveryStatus(String deliveryId, DeliveryStatus status, {
    double? latitude,
    double? longitude,
    String? notes,
  });
  Future<void> completeDelivery(String deliveryId, String confirmationCode, {
    required double latitude,
    required double longitude,
    String? notes,
    String? proofOfDeliveryImageUrl,
  });
  Future<DeliveryRoute> getOptimizedRoute(String deliveryId);
}