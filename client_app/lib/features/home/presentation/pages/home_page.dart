import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';

import '../../../auth/presentation/bloc/auth_bloc.dart';
import '../../../../core/utils/app_router.dart';
import '../../../../core/di/injection.dart';

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (context) => getIt<AuthBloc>()..add(CheckAuthStatus()),
      child: const HomeView(),
    );
  }
}

class HomeView extends StatelessWidget {
  const HomeView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('MozDelivery'),
        actions: [
          BlocBuilder<AuthBloc, AuthState>(
            builder: (context, state) {
              if (state is Authenticated) {
                return PopupMenuButton<String>(
                  onSelected: (value) {
                    switch (value) {
                      case 'profile':
                        context.go(AppRouter.profile);
                        break;
                      case 'logout':
                        context.read<AuthBloc>().add(LogoutRequested());
                        break;
                    }
                  },
                  itemBuilder: (context) => [
                    const PopupMenuItem(
                      value: 'profile',
                      child: Text('Profile'),
                    ),
                    const PopupMenuItem(
                      value: 'logout',
                      child: Text('Logout'),
                    ),
                  ],
                  child: const Icon(Icons.account_circle),
                );
              } else if (state is GuestMode) {
                return PopupMenuButton<String>(
                  onSelected: (value) {
                    switch (value) {
                      case 'login':
                        context.go(AppRouter.login);
                        break;
                      case 'register':
                        context.go(AppRouter.register);
                        break;
                    }
                  },
                  itemBuilder: (context) => [
                    const PopupMenuItem(
                      value: 'login',
                      child: Text('Login'),
                    ),
                    const PopupMenuItem(
                      value: 'register',
                      child: Text('Register'),
                    ),
                  ],
                  child: const Icon(Icons.person_outline),
                );
              } else {
                return IconButton(
                  onPressed: () => context.go(AppRouter.login),
                  icon: const Icon(Icons.login),
                );
              }
            },
          ),
        ],
      ),
      body: BlocBuilder<AuthBloc, AuthState>(
        builder: (context, state) {
          if (state is AuthLoading) {
            return const Center(child: CircularProgressIndicator());
          }

          return SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                // Welcome Section
                Card(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        if (state is Authenticated) ...[
                          Text(
                            'Welcome back, ${state.user.name}!',
                            style: Theme.of(context).textTheme.headlineSmall,
                          ),
                          const SizedBox(height: 8),
                          const Text('What would you like to order today?'),
                        ] else if (state is GuestMode) ...[
                          Text(
                            'Welcome, Guest!',
                            style: Theme.of(context).textTheme.headlineSmall,
                          ),
                          const SizedBox(height: 8),
                          const Text('Browse and order without creating an account.'),
                        ] else ...[
                          Text(
                            'Welcome to MozDelivery!',
                            style: Theme.of(context).textTheme.headlineSmall,
                          ),
                          const SizedBox(height: 8),
                          const Text('Login or continue as guest to start ordering.'),
                        ],
                      ],
                    ),
                  ),
                ),
                
                const SizedBox(height: 24),
                
                // Quick Actions
                Text(
                  'Quick Actions',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
                const SizedBox(height: 16),
                
                GridView.count(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  crossAxisCount: 2,
                  crossAxisSpacing: 16,
                  mainAxisSpacing: 16,
                  children: [
                    _QuickActionCard(
                      icon: Icons.store,
                      title: 'Browse Merchants',
                      subtitle: 'Find restaurants and stores',
                      onTap: () => context.go(AppRouter.merchants),
                    ),
                    _QuickActionCard(
                      icon: Icons.track_changes,
                      title: 'Track Order',
                      subtitle: 'Check your order status',
                      onTap: () => _showTrackOrderDialog(context),
                    ),
                    if (state is Unauthenticated) ...[
                      _QuickActionCard(
                        icon: Icons.login,
                        title: 'Login',
                        subtitle: 'Access your account',
                        onTap: () => context.go(AppRouter.login),
                      ),
                      _QuickActionCard(
                        icon: Icons.person_add,
                        title: 'Register',
                        subtitle: 'Create new account',
                        onTap: () => context.go(AppRouter.register),
                      ),
                    ],
                  ],
                ),
                
                const SizedBox(height: 24),
                
                // Featured Categories (placeholder)
                Text(
                  'Popular Categories',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
                const SizedBox(height: 16),
                
                SizedBox(
                  height: 120,
                  child: ListView(
                    scrollDirection: Axis.horizontal,
                    children: [
                      _CategoryCard(
                        icon: Icons.restaurant,
                        title: 'Restaurants',
                        onTap: () => context.go('${AppRouter.merchants}?vertical=restaurant'),
                      ),
                      _CategoryCard(
                        icon: Icons.local_grocery_store,
                        title: 'Grocery',
                        onTap: () => context.go('${AppRouter.merchants}?vertical=grocery'),
                      ),
                      _CategoryCard(
                        icon: Icons.local_pharmacy,
                        title: 'Pharmacy',
                        onTap: () => context.go('${AppRouter.merchants}?vertical=pharmacy'),
                      ),
                      _CategoryCard(
                        icon: Icons.local_convenience_store,
                        title: 'Convenience',
                        onTap: () => context.go('${AppRouter.merchants}?vertical=convenience'),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          );
        },
      ),
    );
  }

  void _showTrackOrderDialog(BuildContext context) {
    final tokenController = TextEditingController();
    
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Track Order'),
        content: TextField(
          controller: tokenController,
          decoration: const InputDecoration(
            labelText: 'Tracking Token',
            hintText: 'Enter your tracking token',
            helperText: 'Found in your order confirmation',
          ),
          textInputAction: TextInputAction.go,
          onSubmitted: (value) {
            if (value.trim().isNotEmpty) {
              Navigator.of(context).pop();
              context.push('/guest/orders/track?token=${value.trim()}');
            }
          },
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('Cancel'),
          ),
          ElevatedButton(
            onPressed: () {
              final token = tokenController.text.trim();
              if (token.isNotEmpty) {
                Navigator.of(context).pop();
                context.push('/guest/orders/track?token=$token');
              }
            },
            child: const Text('Track'),
          ),
        ],
      ),
    );
  }
}

class _QuickActionCard extends StatelessWidget {
  final IconData icon;
  final String title;
  final String subtitle;
  final VoidCallback onTap;

  const _QuickActionCard({
    required this.icon,
    required this.title,
    required this.subtitle,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(icon, size: 32, color: Theme.of(context).primaryColor),
              const SizedBox(height: 8),
              Text(
                title,
                style: Theme.of(context).textTheme.titleMedium,
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 4),
              Text(
                subtitle,
                style: Theme.of(context).textTheme.bodySmall,
                textAlign: TextAlign.center,
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _CategoryCard extends StatelessWidget {
  final IconData icon;
  final String title;
  final VoidCallback onTap;

  const _CategoryCard({
    required this.icon,
    required this.title,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 100,
      margin: const EdgeInsets.only(right: 16),
      child: Card(
        child: InkWell(
          onTap: onTap,
          borderRadius: BorderRadius.circular(12),
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(icon, size: 32, color: Theme.of(context).primaryColor),
                const SizedBox(height: 8),
                Text(
                  title,
                  style: Theme.of(context).textTheme.bodyMedium,
                  textAlign: TextAlign.center,
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}