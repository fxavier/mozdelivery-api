import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../features/auth/presentation/pages/login_page.dart';
import '../../features/auth/presentation/pages/register_page.dart';
import '../../features/home/presentation/pages/home_page.dart';
import '../../features/merchants/presentation/pages/merchant_list_page.dart';
import '../../features/merchants/presentation/pages/merchant_detail_page.dart';
import '../../features/orders/presentation/pages/order_tracking_page.dart';
import '../../features/profile/presentation/pages/profile_page.dart';
import '../storage/secure_storage_service.dart';
import '../di/injection.dart';

class AppRouter {
  static const String home = '/';
  static const String login = '/login';
  static const String register = '/register';
  static const String merchants = '/merchants';
  static const String merchantDetail = '/merchants/:merchantId';
  static const String orderTracking = '/orders/:orderId/track';
  static const String guestOrderTracking = '/guest/orders/track';
  static const String profile = '/profile';

  static final GoRouter router = GoRouter(
    initialLocation: home,
    redirect: _redirect,
    routes: [
      GoRoute(
        path: home,
        name: 'home',
        builder: (context, state) => const HomePage(),
      ),
      GoRoute(
        path: login,
        name: 'login',
        builder: (context, state) => const LoginPage(),
      ),
      GoRoute(
        path: register,
        name: 'register',
        builder: (context, state) => const RegisterPage(),
      ),
      GoRoute(
        path: merchants,
        name: 'merchants',
        builder: (context, state) => const MerchantListPage(),
      ),
      GoRoute(
        path: merchantDetail,
        name: 'merchantDetail',
        builder: (context, state) {
          final merchantId = state.pathParameters['merchantId']!;
          return MerchantDetailPage(merchantId: merchantId);
        },
      ),
      GoRoute(
        path: orderTracking,
        name: 'orderTracking',
        builder: (context, state) {
          final orderId = state.pathParameters['orderId']!;
          return OrderTrackingPage(orderId: orderId);
        },
      ),
      GoRoute(
        path: guestOrderTracking,
        name: 'guestOrderTracking',
        builder: (context, state) {
          final token = state.uri.queryParameters['token'];
          return OrderTrackingPage(guestToken: token);
        },
      ),
      GoRoute(
        path: profile,
        name: 'profile',
        builder: (context, state) => const ProfilePage(),
      ),
    ],
  );

  static Future<String?> _redirect(BuildContext context, GoRouterState state) async {
    final secureStorage = getIt<SecureStorageService>();
    final isAuthenticated = await secureStorage.isAuthenticated();
    final isGuestMode = await secureStorage.isGuestMode();
    
    final isAuthRoute = state.matchedLocation == login || state.matchedLocation == register;
    final isPublicRoute = state.matchedLocation == home || 
                         state.matchedLocation == merchants ||
                         state.matchedLocation.startsWith('/merchants/') ||
                         state.matchedLocation == guestOrderTracking;

    // Allow access to public routes regardless of authentication status
    if (isPublicRoute) {
      return null;
    }

    // Redirect to home if trying to access auth routes while authenticated
    if (isAuthRoute && (isAuthenticated || isGuestMode)) {
      return home;
    }

    // Redirect to login if trying to access protected routes while not authenticated
    if (!isAuthRoute && !isAuthenticated && !isGuestMode) {
      return login;
    }

    return null;
  }
}