import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:geolocator/geolocator.dart';
import 'package:url_launcher/url_launcher.dart';
import '../../domain/entities/delivery.dart';
import '../bloc/delivery_bloc.dart';
import '../bloc/delivery_event.dart';
import '../bloc/delivery_state.dart';
import '../../../../core/theme/app_theme.dart';

class DeliveryRoutePage extends StatefulWidget {
  final Delivery delivery;

  const DeliveryRoutePage({
    super.key,
    required this.delivery,
  });

  @override
  State<DeliveryRoutePage> createState() => _DeliveryRoutePageState();
}

class _DeliveryRoutePageState extends State<DeliveryRoutePage> {
  GoogleMapController? _mapController;
  Position? _currentPosition;
  Set<Marker> _markers = {};
  Set<Polyline> _polylines = {};

  @override
  void initState() {
    super.initState();
    _getCurrentLocation();
    _loadOptimizedRoute();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Delivery Route'),
        actions: [
          IconButton(
            icon: const Icon(Icons.navigation),
            onPressed: _openInMaps,
          ),
        ],
      ),
      body: BlocListener<DeliveryBloc, DeliveryState>(
        listener: (context, state) {
          if (state is DeliveryLoaded && state.optimizedRoute != null) {
            _updateMapWithRoute(state.optimizedRoute!);
          }
        },
        child: Column(
          children: [
            _buildRouteInfo(),
            Expanded(
              child: GoogleMap(
                onMapCreated: (GoogleMapController controller) {
                  _mapController = controller;
                  _setupInitialMarkers();
                },
                initialCameraPosition: CameraPosition(
                  target: LatLng(
                    widget.delivery.deliveryLatitude,
                    widget.delivery.deliveryLongitude,
                  ),
                  zoom: 14,
                ),
                markers: _markers,
                polylines: _polylines,
                myLocationEnabled: true,
                myLocationButtonEnabled: true,
                mapType: MapType.normal,
                zoomControlsEnabled: false,
              ),
            ),
            _buildNavigationButtons(),
          ],
        ),
      ),
    );
  }

  Widget _buildRouteInfo() {
    return BlocBuilder<DeliveryBloc, DeliveryState>(
      builder: (context, state) {
        if (state is DeliveryLoaded && state.optimizedRoute != null) {
          final route = state.optimizedRoute!;
          return Container(
            padding: const EdgeInsets.all(16),
            color: AppTheme.primaryColor.withOpacity(0.1),
            child: Row(
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Distance: ${(route.totalDistance / 1000).toStringAsFixed(1)} km',
                        style: AppTheme.bodyMedium.copyWith(fontWeight: FontWeight.w500),
                      ),
                      Text(
                        'Est. Time: ${(route.estimatedDuration / 60).round()} min',
                        style: AppTheme.bodySmall.copyWith(
                          color: AppTheme.textSecondaryColor,
                        ),
                      ),
                    ],
                  ),
                ),
                ElevatedButton.icon(
                  onPressed: _refreshRoute,
                  icon: const Icon(Icons.refresh, size: 18),
                  label: const Text('Refresh'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: AppTheme.primaryColor,
                    foregroundColor: Colors.white,
                  ),
                ),
              ],
            ),
          );
        }
        return Container(
          padding: const EdgeInsets.all(16),
          color: AppTheme.primaryColor.withOpacity(0.1),
          child: const Row(
            children: [
              CircularProgressIndicator(strokeWidth: 2),
              SizedBox(width: 16),
              Text('Loading route...'),
            ],
          ),
        );
      },
    );
  }

  Widget _buildNavigationButtons() {
    return Container(
      padding: const EdgeInsets.all(16),
      child: Row(
        children: [
          Expanded(
            child: OutlinedButton.icon(
              onPressed: _navigateToPickup,
              icon: const Icon(Icons.store),
              label: const Text('Navigate to Pickup'),
            ),
          ),
          const SizedBox(width: 16),
          Expanded(
            child: ElevatedButton.icon(
              onPressed: _navigateToDelivery,
              icon: const Icon(Icons.location_on),
              label: const Text('Navigate to Delivery'),
              style: ElevatedButton.styleFrom(
                backgroundColor: AppTheme.primaryColor,
                foregroundColor: Colors.white,
              ),
            ),
          ),
        ],
      ),
    );
  }

  void _getCurrentLocation() async {
    try {
      final position = await Geolocator.getCurrentPosition();
      setState(() {
        _currentPosition = position;
      });
      // Update map camera to current location if needed
      if (_mapController != null) {
        _mapController!.animateCamera(
          CameraUpdate.newLatLng(
            LatLng(position.latitude, position.longitude),
          ),
        );
      }
    } catch (e) {
      // Handle location error
      debugPrint('Error getting location: $e');
    }
  }

  void _loadOptimizedRoute() {
    context.read<DeliveryBloc>().add(LoadOptimizedRoute(widget.delivery.deliveryId));
  }

  void _setupInitialMarkers() {
    setState(() {
      _markers = {
        Marker(
          markerId: const MarkerId('pickup'),
          position: LatLng(
            // Assuming merchant coordinates are available
            widget.delivery.deliveryLatitude - 0.01, // Placeholder
            widget.delivery.deliveryLongitude - 0.01, // Placeholder
          ),
          infoWindow: InfoWindow(
            title: 'Pickup Location',
            snippet: widget.delivery.merchantName,
          ),
          icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueBlue),
        ),
        Marker(
          markerId: const MarkerId('delivery'),
          position: LatLng(
            widget.delivery.deliveryLatitude,
            widget.delivery.deliveryLongitude,
          ),
          infoWindow: InfoWindow(
            title: 'Delivery Location',
            snippet: widget.delivery.customerName,
          ),
          icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueRed),
        ),
      };
    });
  }

  void _updateMapWithRoute(DeliveryRoute route) {
    setState(() {
      _markers = route.points.map((point) {
        return Marker(
          markerId: MarkerId(point.type),
          position: LatLng(point.latitude, point.longitude),
          infoWindow: InfoWindow(
            title: point.type == 'pickup' ? 'Pickup' : 'Delivery',
            snippet: point.address,
          ),
          icon: BitmapDescriptor.defaultMarkerWithHue(
            point.type == 'pickup' ? BitmapDescriptor.hueBlue : BitmapDescriptor.hueRed,
          ),
        );
      }).toSet();

      _polylines = {
        Polyline(
          polylineId: const PolylineId('route'),
          points: _decodePolyline(route.polyline),
          color: AppTheme.primaryColor,
          width: 4,
        ),
      };
    });

    // Fit the map to show all markers
    if (_mapController != null && route.points.isNotEmpty) {
      _fitMapToMarkers(route.points);
    }
  }

  List<LatLng> _decodePolyline(String polyline) {
    // Simple polyline decoding - in a real app, use a proper polyline decoder
    // This is a placeholder implementation
    return [];
  }

  void _fitMapToMarkers(List<RoutePoint> points) {
    if (points.isEmpty) return;

    double minLat = points.first.latitude;
    double maxLat = points.first.latitude;
    double minLng = points.first.longitude;
    double maxLng = points.first.longitude;

    for (final point in points) {
      minLat = minLat < point.latitude ? minLat : point.latitude;
      maxLat = maxLat > point.latitude ? maxLat : point.latitude;
      minLng = minLng < point.longitude ? minLng : point.longitude;
      maxLng = maxLng > point.longitude ? maxLng : point.longitude;
    }

    _mapController?.animateCamera(
      CameraUpdate.newLatLngBounds(
        LatLngBounds(
          southwest: LatLng(minLat, minLng),
          northeast: LatLng(maxLat, maxLng),
        ),
        100.0, // padding
      ),
    );
  }

  void _refreshRoute() {
    _loadOptimizedRoute();
  }

  void _navigateToPickup() {
    // Navigate to pickup location using external maps app
    _openMapsNavigation(
      widget.delivery.deliveryLatitude - 0.01, // Placeholder merchant coordinates
      widget.delivery.deliveryLongitude - 0.01,
      widget.delivery.merchantName,
    );
  }

  void _navigateToDelivery() {
    // Navigate to delivery location using external maps app
    _openMapsNavigation(
      widget.delivery.deliveryLatitude,
      widget.delivery.deliveryLongitude,
      widget.delivery.customerName,
    );
  }

  void _openInMaps() {
    _navigateToDelivery();
  }

  void _openMapsNavigation(double lat, double lng, String label) async {
    final Uri googleMapsUri = Uri.parse(
      'https://www.google.com/maps/dir/?api=1&destination=$lat,$lng&destination_place_id=$label',
    );
    
    final Uri appleMapsUri = Uri.parse(
      'https://maps.apple.com/?daddr=$lat,$lng&dirflg=d',
    );

    try {
      if (Theme.of(context).platform == TargetPlatform.iOS) {
        if (await canLaunchUrl(appleMapsUri)) {
          await launchUrl(appleMapsUri);
        } else if (await canLaunchUrl(googleMapsUri)) {
          await launchUrl(googleMapsUri);
        }
      } else {
        if (await canLaunchUrl(googleMapsUri)) {
          await launchUrl(googleMapsUri);
        }
      }
    } catch (e) {
      debugPrint('Error launching maps: $e');
    }
  }
}