// GENERATED CODE - DO NOT MODIFY BY HAND

// **************************************************************************
// InjectableConfigGenerator
// **************************************************************************

// ignore_for_file: type=lint
// coverage:ignore-file

// ignore_for_file: no_leading_underscores_for_library_prefixes
import 'package:dio/dio.dart' as _i361;
import 'package:flutter_secure_storage/flutter_secure_storage.dart' as _i558;
import 'package:get_it/get_it.dart' as _i174;
import 'package:injectable/injectable.dart' as _i526;
import 'package:local_auth/local_auth.dart' as _i123;
import 'package:logger/logger.dart' as _i974;

import '../../features/auth/presentation/bloc/auth_bloc.dart' as _i456;
import '../../features/location/data/services/location_service.dart' as _i789;
import '../network/api_client.dart' as _i454;
import '../storage/secure_storage_service.dart' as _i1070;
import 'injection.dart' as _i1058;

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
    gh.singleton<_i123.LocalAuthentication>(() => registerModule.localAuth);
    gh.singleton<_i454.ApiClient>(() => _i454.ApiClient(gh<_i361.Dio>()));
    gh.singleton<_i1070.SecureStorageService>(
        () => _i1070.SecureStorageService(gh<_i558.FlutterSecureStorage>()));
    gh.singleton<_i789.LocationService>(
        () => _i789.LocationService(gh<_i974.Logger>()));
    gh.singleton<_i456.AuthBloc>(() => _i456.AuthBloc(
          gh<_i454.ApiClient>(),
          gh<_i1070.SecureStorageService>(),
          gh<_i123.LocalAuthentication>(),
          gh<_i974.Logger>(),
        ));
    return this;
  }
}

class _$RegisterModule extends _i1058.RegisterModule {}