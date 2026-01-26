// GENERATED CODE - DO NOT MODIFY BY HAND

// **************************************************************************
// InjectableConfigGenerator
// **************************************************************************

// ignore_for_file: type=lint
// coverage:ignore-file

// ignore_for_file: no_leading_underscores_for_library_prefixes
import 'package:courier_app/core/di/injection.dart' as _i469;
import 'package:courier_app/core/network/api_client.dart' as _i294;
import 'package:courier_app/core/storage/secure_storage_service.dart' as _i362;
import 'package:courier_app/features/auth/presentation/bloc/auth_bloc.dart'
    as _i941;
import 'package:courier_app/features/deliveries/domain/repositories/delivery_repository.dart'
    as _i737;
import 'package:courier_app/features/location/data/services/location_service.dart'
    as _i306;
import 'package:dio/dio.dart' as _i361;
import 'package:flutter_secure_storage/flutter_secure_storage.dart' as _i558;
import 'package:get_it/get_it.dart' as _i174;
import 'package:injectable/injectable.dart' as _i526;
import 'package:local_auth/local_auth.dart' as _i152;
import 'package:logger/logger.dart' as _i974;

extension GetItInjectableX on _i174.GetIt {
// initializes the registration of main-scope dependencies inside of GetIt
  _i174.GetIt init({
    String? environment,
    _i526.EnvironmentFilter? environmentFilter,
  }) {
    final gh = _i526.GetItHelper(
      this,
      environment,
      environmentFilter,
    );
    final registerModule = _$RegisterModule();
    gh.singleton<_i361.Dio>(() => registerModule.dio);
    gh.singleton<_i558.FlutterSecureStorage>(
        () => registerModule.secureStorage);
    gh.singleton<_i974.Logger>(() => registerModule.logger);
    gh.singleton<_i152.LocalAuthentication>(() => registerModule.localAuth);
    gh.singleton<_i737.DeliveryRepository>(
        () => registerModule.deliveryRepository(gh<_i361.Dio>()));
    gh.singleton<_i294.ApiClient>(() => _i294.ApiClient(gh<_i361.Dio>()));
    gh.singleton<_i306.LocationService>(
        () => _i306.LocationService(gh<_i974.Logger>()));
    gh.singleton<_i362.SecureStorageService>(
        () => _i362.SecureStorageService(gh<_i558.FlutterSecureStorage>()));
    gh.factory<_i941.AuthBloc>(() => _i941.AuthBloc(
          gh<_i294.ApiClient>(),
          gh<_i362.SecureStorageService>(),
          gh<_i152.LocalAuthentication>(),
          gh<_i974.Logger>(),
        ));
    return this;
  }
}

class _$RegisterModule extends _i469.RegisterModule {}
