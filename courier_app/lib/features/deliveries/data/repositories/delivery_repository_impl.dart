import 'package:dio/dio.dart';
import '../../domain/entities/delivery.dart' as domain;
import '../../domain/repositories/delivery_repository.dart';
import '../models/delivery_models.dart' as models;

class DeliveryRepositoryImpl implements DeliveryRepository {
  final Dio _dio;

  DeliveryRepositoryImpl(this._dio);

  @override
  Future<List<domain.Delivery>> getAssignedDeliveries() async {
    try {
      final response = await _dio.get('/api/v1/couriers/assignments');
      final List<dynamic> data = response.data;
      return data.map((json) => _mapToDelivery(models.DeliveryAssignmentResponse.fromJson(json))).toList();
    } catch (e) {
      throw Exception('Failed to fetch assigned deliveries: $e');
    }
  }

  @override
  Future<domain.Delivery> getDeliveryById(String deliveryId) async {
    try {
      final response = await _dio.get('/api/v1/deliveries/$deliveryId');
      final deliveryResponse = models.DeliveryResponse.fromJson(response.data);
      return _mapToDeliveryFromResponse(deliveryResponse);
    } catch (e) {
      throw Exception('Failed to fetch delivery: $e');
    }
  }

  @override
  Future<void> acceptDelivery(String deliveryId) async {
    try {
      await _dio.put('/api/v1/deliveries/$deliveryId/accept');
    } catch (e) {
      throw Exception('Failed to accept delivery: $e');
    }
  }

  @override
  Future<void> updateDeliveryStatus(String deliveryId, domain.DeliveryStatus status, {
    double? latitude,
    double? longitude,
    String? notes,
  }) async {
    try {
      final request = models.UpdateDeliveryStatusRequest(
        status: _mapStatusToString(status),
        latitude: latitude,
        longitude: longitude,
        notes: notes,
      );
      await _dio.put('/api/v1/deliveries/$deliveryId/status', data: request.toJson());
    } catch (e) {
      throw Exception('Failed to update delivery status: $e');
    }
  }

  @override
  Future<void> completeDelivery(String deliveryId, String confirmationCode, {
    required double latitude,
    required double longitude,
    String? notes,
    String? proofOfDeliveryImageUrl,
  }) async {
    try {
      final request = models.CompleteDeliveryRequest(
        deliveryConfirmationCode: confirmationCode,
        latitude: latitude,
        longitude: longitude,
        notes: notes,
        proofOfDeliveryImageUrl: proofOfDeliveryImageUrl,
      );
      await _dio.post('/api/v1/deliveries/$deliveryId/complete', data: request.toJson());
    } catch (e) {
      throw Exception('Failed to complete delivery: $e');
    }
  }

  @override
  Future<domain.DeliveryRoute> getOptimizedRoute(String deliveryId) async {
    try {
      final response = await _dio.get('/api/v1/deliveries/$deliveryId/route');
      final routeData = response.data;
      return domain.DeliveryRoute(
        points: (routeData['points'] as List)
            .map((point) => domain.RoutePoint(
                  latitude: point['latitude'],
                  longitude: point['longitude'],
                  type: point['type'],
                  address: point['address'],
                ))
            .toList(),
        totalDistance: routeData['totalDistance'],
        estimatedDuration: routeData['estimatedDuration'],
        polyline: routeData['polyline'],
      );
    } catch (e) {
      throw Exception('Failed to get optimized route: $e');
    }
  }

  domain.Delivery _mapToDelivery(models.DeliveryAssignmentResponse response) {
    return domain.Delivery(
      deliveryId: response.deliveryId,
      orderId: response.orderId,
      merchantName: response.merchantName,
      merchantAddress: response.merchantAddress,
      customerName: response.customerName,
      customerPhone: response.customerPhone,
      deliveryAddress: response.deliveryAddress,
      deliveryLatitude: response.deliveryLatitude,
      deliveryLongitude: response.deliveryLongitude,
      status: _mapStringToStatus(response.status),
      totalAmount: response.totalAmount,
      currency: response.currency,
      estimatedPickupTime: response.estimatedPickupTime,
      estimatedDeliveryTime: response.estimatedDeliveryTime,
      specialInstructions: response.specialInstructions,
      items: response.items.map((item) => domain.OrderItem(
        productId: item.productId,
        productName: item.productName,
        quantity: item.quantity,
        unitPrice: item.unitPrice,
        totalPrice: item.totalPrice,
        specialInstructions: item.specialInstructions,
      )).toList(),
    );
  }

  domain.Delivery _mapToDeliveryFromResponse(models.DeliveryResponse response) {
    return domain.Delivery(
      deliveryId: response.deliveryId,
      orderId: response.orderId,
      merchantName: '', // Not available in DeliveryResponse
      merchantAddress: '', // Not available in DeliveryResponse
      customerName: '', // Not available in DeliveryResponse
      customerPhone: '', // Not available in DeliveryResponse
      deliveryAddress: '', // Not available in DeliveryResponse
      deliveryLatitude: 0.0, // Not available in DeliveryResponse
      deliveryLongitude: 0.0, // Not available in DeliveryResponse
      status: _mapStringToStatus(response.status),
      totalAmount: 0.0, // Not available in DeliveryResponse
      currency: 'MZN', // Default currency
      estimatedPickupTime: DateTime.now(), // Not available in DeliveryResponse
      estimatedDeliveryTime: DateTime.now(), // Not available in DeliveryResponse
      items: [], // Not available in DeliveryResponse
      acceptedAt: response.acceptedAt,
      pickedUpAt: response.pickedUpAt,
      deliveredAt: response.deliveredAt,
      route: response.route != null ? _mapRoute(response.route!) : null,
    );
  }

  domain.DeliveryRoute _mapRoute(models.DeliveryRoute route) {
    return domain.DeliveryRoute(
      points: route.points.map((point) => domain.RoutePoint(
        latitude: point.latitude,
        longitude: point.longitude,
        type: point.type,
        address: point.address,
      )).toList(),
      totalDistance: route.totalDistance,
      estimatedDuration: route.estimatedDuration,
      polyline: route.polyline,
    );
  }

  domain.DeliveryStatus _mapStringToStatus(String status) {
    switch (status.toLowerCase()) {
      case 'assigned':
        return domain.DeliveryStatus.assigned;
      case 'accepted':
        return domain.DeliveryStatus.accepted;
      case 'picked_up':
        return domain.DeliveryStatus.pickedUp;
      case 'out_for_delivery':
        return domain.DeliveryStatus.outForDelivery;
      case 'delivered':
        return domain.DeliveryStatus.delivered;
      case 'cancelled':
        return domain.DeliveryStatus.cancelled;
      case 'failed':
        return domain.DeliveryStatus.failed;
      default:
        return domain.DeliveryStatus.assigned;
    }
  }

  String _mapStatusToString(domain.DeliveryStatus status) {
    switch (status) {
      case domain.DeliveryStatus.assigned:
        return 'assigned';
      case domain.DeliveryStatus.accepted:
        return 'accepted';
      case domain.DeliveryStatus.pickedUp:
        return 'picked_up';
      case domain.DeliveryStatus.outForDelivery:
        return 'out_for_delivery';
      case domain.DeliveryStatus.delivered:
        return 'delivered';
      case domain.DeliveryStatus.cancelled:
        return 'cancelled';
      case domain.DeliveryStatus.failed:
        return 'failed';
    }
  }
}