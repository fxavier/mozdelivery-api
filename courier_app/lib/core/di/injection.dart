import 'package:get_it/get_it.dart';
import 'package:injectable/injectable.dart';
import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:logger/logger.dart';
import 'package:local_auth/local_auth.dart';

import '../../features/deliveries/domain/repositories/delivery_repository.dart';
import '../../features/deliveries/data/repositories/delivery_repository_impl.dart';
import '../services/image_upload_service.dart';
import 'injection.config.dart';

final GetIt getIt = GetIt.instance;

@InjectableInit()
Future<void> configureDependencies() async {
  getIt.init();
}

@module
abstract class RegisterModule {
  @singleton
  Dio get dio => Dio(BaseOptions(
    baseUrl: 'http://localhost:8080/api',
    connectTimeout: const Duration(seconds: 30),
    receiveTimeout: const Duration(seconds: 30),
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
  ));

  @singleton
  FlutterSecureStorage get secureStorage => const FlutterSecureStorage(
    aOptions: AndroidOptions(
      encryptedSharedPreferences: true,
    ),
    iOptions: IOSOptions(
      accessibility: KeychainAccessibility.first_unlock_this_device,
    ),
  );

  @singleton
  Logger get logger => Logger(
    printer: PrettyPrinter(
      methodCount: 2,
      errorMethodCount: 8,
      lineLength: 120,
      colors: true,
      printEmojis: true,
      dateTimeFormat: DateTimeFormat.onlyTimeAndSinceStart,
    ),
  );

  @singleton
  LocalAuthentication get localAuth => LocalAuthentication();

  @singleton
  DeliveryRepository deliveryRepository(Dio dio) => DeliveryRepositoryImpl(dio);

  @singleton
  ImageUploadService imageUploadService(Dio dio) => ImageUploadService(dio);
}