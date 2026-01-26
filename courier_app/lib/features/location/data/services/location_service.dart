import 'dart:async';
import 'package:geolocator/geolocator.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:injectable/injectable.dart';
import 'package:logger/logger.dart';

import '../../../../core/constants/app_constants.dart';

@singleton
class LocationService {
  final Logger _logger;
  StreamSubscription<Position>? _positionStreamSubscription;
  final StreamController<Position> _locationController = StreamController<Position>.broadcast();

  LocationService(this._logger);

  Stream<Position> get locationStream => _locationController.stream;

  Future<bool> requestLocationPermission() async {
    try {
      final permission = await Permission.location.request();
      
      if (permission.isGranted) {
        return true;
      } else if (permission.isDenied) {
        _logger.w('Location permission denied');
        return false;
      } else if (permission.isPermanentlyDenied) {
        _logger.e('Location permission permanently denied');
        await openAppSettings();
        return false;
      }
      
      return false;
    } catch (e) {
      _logger.e('Error requesting location permission: $e');
      return false;
    }
  }

  Future<bool> isLocationServiceEnabled() async {
    try {
      return await Geolocator.isLocationServiceEnabled();
    } catch (e) {
      _logger.e('Error checking location service: $e');
      return false;
    }
  }

  Future<Position?> getCurrentLocation() async {
    try {
      final hasPermission = await requestLocationPermission();
      if (!hasPermission) {
        _logger.w('Location permission not granted');
        return null;
      }

      final isServiceEnabled = await isLocationServiceEnabled();
      if (!isServiceEnabled) {
        _logger.w('Location service not enabled');
        return null;
      }

      final position = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high,
        timeLimit: AppConstants.locationTimeout,
      );

      _logger.d('Current location: ${position.latitude}, ${position.longitude}');
      return position;
    } catch (e) {
      _logger.e('Error getting current location: $e');
      return null;
    }
  }

  Future<void> startLocationTracking() async {
    try {
      final hasPermission = await requestLocationPermission();
      if (!hasPermission) {
        _logger.w('Cannot start location tracking: permission not granted');
        return;
      }

      final isServiceEnabled = await isLocationServiceEnabled();
      if (!isServiceEnabled) {
        _logger.w('Cannot start location tracking: service not enabled');
        return;
      }

      const locationSettings = LocationSettings(
        accuracy: LocationAccuracy.high,
        distanceFilter: 10, // Update every 10 meters
        timeLimit: Duration(seconds: 30),
      );

      _positionStreamSubscription = Geolocator.getPositionStream(
        locationSettings: locationSettings,
      ).listen(
        (Position position) {
          _logger.d('Location update: ${position.latitude}, ${position.longitude}');
          _locationController.add(position);
        },
        onError: (error) {
          _logger.e('Location tracking error: $error');
        },
      );

      _logger.i('Location tracking started');
    } catch (e) {
      _logger.e('Error starting location tracking: $e');
    }
  }

  Future<void> stopLocationTracking() async {
    try {
      await _positionStreamSubscription?.cancel();
      _positionStreamSubscription = null;
      _logger.i('Location tracking stopped');
    } catch (e) {
      _logger.e('Error stopping location tracking: $e');
    }
  }

  double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    return Geolocator.distanceBetween(lat1, lon1, lat2, lon2);
  }

  double calculateBearing(double lat1, double lon1, double lat2, double lon2) {
    return Geolocator.bearingBetween(lat1, lon1, lat2, lon2);
  }

  bool isLocationAccurate(Position position) {
    return position.accuracy <= AppConstants.locationAccuracyThreshold;
  }

  void dispose() {
    _positionStreamSubscription?.cancel();
    _locationController.close();
  }
}