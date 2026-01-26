// GENERATED CODE - DO NOT MODIFY BY HAND

// **************************************************************************
// InjectableConfigGenerator
// **************************************************************************

// ignore_for_file: type=lint
// coverage:ignore-file

// ignore_for_file: no_leading_underscores_for_library_prefixes
import 'package:client_app/core/di/injection.dart' as _i177;
import 'package:client_app/core/network/api_client.dart' as _i330;
import 'package:client_app/core/network/network_interceptor.dart' as _i577;
import 'package:client_app/core/storage/secure_storage_service.dart' as _i791;
import 'package:client_app/features/auth/presentation/bloc/auth_bloc.dart'
    as _i725;
import 'package:client_app/features/merchants/data/repositories/merchant_repository_impl.dart'
    as _i772;
import 'package:client_app/features/merchants/domain/repositories/merchant_repository.dart'
    as _i439;
import 'package:client_app/features/merchants/presentation/bloc/merchant_browsing_bloc.dart'
    as _i368;
import 'package:client_app/features/merchants/presentation/bloc/merchant_detail_bloc.dart'
    as _i515;
import 'package:client_app/features/orders/data/repositories/order_repository_impl.dart'
    as _i1017;
import 'package:client_app/features/orders/domain/repositories/order_repository.dart'
    as _i665;
import 'package:client_app/features/orders/presentation/bloc/cart_bloc.dart'
    as _i782;
import 'package:client_app/features/orders/presentation/bloc/guest_checkout_bloc.dart'
    as _i332;
import 'package:client_app/features/orders/presentation/bloc/order_tracking_bloc.dart'
    as _i304;
import 'package:dio/dio.dart' as _i361;
import 'package:flutter_secure_storage/flutter_secure_storage.dart' as _i558;
import 'package:get_it/get_it.dart' as _i174;
import 'package:injectable/injectable.dart' as _i526;
import 'package:shared_preferences/shared_preferences.dart' as _i460;

extension GetItInjectableX on _i174.GetIt {
// initializes the registration of main-scope dependencies inside of GetIt
  Future<_i174.GetIt> init({
    String? environment,
    _i526.EnvironmentFilter? environmentFilter,
  }) async {
    final gh = _i526.GetItHelper(
      this,
      environment,
      environmentFilter,
    );
    final registerModule = _$RegisterModule();
    await gh.factoryAsync<_i460.SharedPreferences>(
      () => registerModule.prefs,
      preResolve: true,
    );
    gh.singleton<_i558.FlutterSecureStorage>(
        () => registerModule.secureStorage);
    gh.singleton<_i361.Dio>(() => registerModule.dio);
    gh.singleton<_i330.ApiClient>(() => _i330.ApiClient(gh<_i361.Dio>()));
    gh.singleton<_i791.SecureStorageService>(
        () => _i791.SecureStorageService(gh<_i558.FlutterSecureStorage>()));
    gh.singleton<_i330.ApiService>(
        () => _i330.ApiService(gh<_i330.ApiClient>()));
    gh.lazySingleton<_i439.MerchantRepository>(
        () => _i772.MerchantRepositoryImpl(gh<_i330.ApiService>()));
    gh.singleton<_i577.NetworkInterceptor>(
        () => _i577.NetworkInterceptor(gh<_i791.SecureStorageService>()));
    gh.lazySingleton<_i665.OrderRepository>(() => _i1017.OrderRepositoryImpl(
          gh<_i330.ApiService>(),
          gh<_i460.SharedPreferences>(),
        ));
    gh.factory<_i782.CartBloc>(
        () => _i782.CartBloc(gh<_i665.OrderRepository>()));
    gh.factory<_i304.OrderTrackingBloc>(
        () => _i304.OrderTrackingBloc(gh<_i665.OrderRepository>()));
    gh.factory<_i725.AuthBloc>(() => _i725.AuthBloc(
          gh<_i330.ApiService>(),
          gh<_i791.SecureStorageService>(),
        ));
    gh.factory<_i515.MerchantDetailBloc>(
        () => _i515.MerchantDetailBloc(gh<_i439.MerchantRepository>()));
    gh.factory<_i368.MerchantBrowsingBloc>(
        () => _i368.MerchantBrowsingBloc(gh<_i439.MerchantRepository>()));
    gh.factory<_i332.GuestCheckoutBloc>(() => _i332.GuestCheckoutBloc(
          gh<_i665.OrderRepository>(),
          gh<_i791.SecureStorageService>(),
        ));
    return this;
  }
}

class _$RegisterModule extends _i177.RegisterModule {}
