import 'package:dio/dio.dart';
import 'package:injectable/injectable.dart';
import 'package:retrofit/retrofit.dart';

import '../../features/auth/data/models/auth_models.dart';
import '../../features/deliveries/data/models/delivery_models.dart';
import '../../features/profile/data/models/courier_profile_models.dart';

part 'api_client.g.dart';

@singleton
@RestApi()
abstract class ApiClient {
  @factoryMethod
  factory ApiClient(Dio dio) = _ApiClient;

  // Authentication endpoints
  @POST('/v1/auth/courier/login')
  Future<AuthResponse> loginCourier(@Body() LoginRequest request);

  @POST('/v1/auth/courier/register')
  Future<AuthResponse> registerCourier(@Body() CourierRegistrationRequest request);

  @POST('/v1/auth/refresh')
  Future<AuthResponse> refreshToken(@Body() RefreshTokenRequest request);

  @POST('/v1/auth/logout')
  Future<void> logout();

  // Courier profile endpoints
  @GET('/v1/couriers/{courierId}/profile')
  Future<CourierProfileResponse> getCourierProfile(@Path() String courierId);

  @PUT('/v1/couriers/{courierId}/profile')
  Future<CourierProfileResponse> updateCourierProfile(
    @Path() String courierId,
    @Body() UpdateCourierProfileRequest request,
  );

  @PUT('/v1/couriers/{courierId}/availability')
  Future<void> updateAvailability(
    @Path() String courierId,
    @Body() UpdateAvailabilityRequest request,
  );

  @PUT('/v1/couriers/{courierId}/location')
  Future<void> updateLocation(
    @Path() String courierId,
    @Body() LocationUpdateRequest request,
  );

  // Delivery endpoints
  @GET('/v1/couriers/{courierId}/assignments')
  Future<List<DeliveryAssignmentResponse>> getDeliveryAssignments(
    @Path() String courierId,
  );

  @PUT('/v1/deliveries/{deliveryId}/accept')
  Future<DeliveryResponse> acceptDelivery(@Path() String deliveryId);

  @PUT('/v1/deliveries/{deliveryId}/status')
  Future<DeliveryResponse> updateDeliveryStatus(
    @Path() String deliveryId,
    @Body() UpdateDeliveryStatusRequest request,
  );

  @POST('/v1/deliveries/{deliveryId}/complete')
  Future<DeliveryCompletionResponse> completeDelivery(
    @Path() String deliveryId,
    @Body() CompleteDeliveryRequest request,
  );

  @GET('/v1/deliveries/{deliveryId}')
  Future<DeliveryResponse> getDelivery(@Path() String deliveryId);

  // Location tracking
  @POST('/v1/couriers/{courierId}/location/track')
  Future<void> startLocationTracking(@Path() String courierId);

  @DELETE('/v1/couriers/{courierId}/location/track')
  Future<void> stopLocationTracking(@Path() String courierId);
}