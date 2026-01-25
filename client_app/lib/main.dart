import 'package:flutter/material.dart';
import 'package:dio/dio.dart';

import 'core/di/injection.dart';
import 'core/theme/app_theme.dart';
import 'core/utils/app_router.dart';
import 'core/network/network_interceptor.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  
  // Initialize dependency injection
  await configureDependencies();
  
  // Configure Dio with interceptors
  final dio = getIt<Dio>();
  dio.interceptors.add(getIt<NetworkInterceptor>());
  
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'MozDelivery Client',
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      routerConfig: AppRouter.router,
      debugShowCheckedModeBanner: false,
    );
  }
}