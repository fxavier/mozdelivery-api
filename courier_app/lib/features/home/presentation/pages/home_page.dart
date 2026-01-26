import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../../../../core/theme/app_theme.dart';
import '../../../auth/presentation/bloc/auth_bloc.dart';
import '../../../auth/presentation/bloc/auth_event.dart';
import '../../../deliveries/presentation/pages/deliveries_page.dart';
import '../../../profile/presentation/pages/profile_page.dart';
import '../../../location/presentation/pages/location_page.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  int _currentIndex = 0;

  final List<Widget> _pages = [
    const DashboardPage(),
    const DeliveriesPage(),
    const LocationPage(),
    const ProfilePage(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: IndexedStack(
        index: _currentIndex,
        children: _pages,
      ),
      bottomNavigationBar: BottomNavigationBar(
        type: BottomNavigationBarType.fixed,
        currentIndex: _currentIndex,
        onTap: (index) {
          setState(() {
            _currentIndex = index;
          });
        },
        items: const [
          BottomNavigationBarItem(
            icon: Icon(Icons.dashboard_outlined),
            activeIcon: Icon(Icons.dashboard),
            label: 'Dashboard',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.delivery_dining_outlined),
            activeIcon: Icon(Icons.delivery_dining),
            label: 'Deliveries',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.location_on_outlined),
            activeIcon: Icon(Icons.location_on),
            label: 'Location',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.person_outlined),
            activeIcon: Icon(Icons.person),
            label: 'Profile',
          ),
        ],
      ),
    );
  }
}

class DashboardPage extends StatelessWidget {
  const DashboardPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Dashboard'),
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications_outlined),
            onPressed: () {
              // TODO: Navigate to notifications
            },
          ),
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () {
              context.read<AuthBloc>().add(const AuthLogoutRequested());
            },
          ),
        ],
      ),
      body: const SingleChildScrollView(
        padding: EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Welcome Card
            Card(
              child: Padding(
                padding: EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'Welcome Back!',
                      style: AppTheme.titleLarge,
                    ),
                    SizedBox(height: 8),
                    Text(
                      'Ready to start delivering?',
                      style: AppTheme.bodyMedium,
                    ),
                  ],
                ),
              ),
            ),
            SizedBox(height: 16),
            // Stats Cards
            Row(
              children: [
                Expanded(
                  child: _StatsCard(
                    title: 'Today',
                    value: '0',
                    subtitle: 'Deliveries',
                    icon: Icons.delivery_dining,
                    color: AppTheme.primaryColor,
                  ),
                ),
                SizedBox(width: 16),
                Expanded(
                  child: _StatsCard(
                    title: 'Earnings',
                    value: '0 MZN',
                    subtitle: 'Today',
                    icon: Icons.monetization_on,
                    color: AppTheme.successColor,
                  ),
                ),
              ],
            ),
            SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: _StatsCard(
                    title: 'Rating',
                    value: '0.0',
                    subtitle: 'Average',
                    icon: Icons.star,
                    color: AppTheme.warningColor,
                  ),
                ),
                SizedBox(width: 16),
                Expanded(
                  child: _StatsCard(
                    title: 'Status',
                    value: 'Offline',
                    subtitle: 'Availability',
                    icon: Icons.circle,
                    color: AppTheme.errorColor,
                  ),
                ),
              ],
            ),
            SizedBox(height: 24),
            // Quick Actions
            Text(
              'Quick Actions',
              style: AppTheme.titleMedium,
            ),
            SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: _ActionCard(
                    title: 'Go Online',
                    subtitle: 'Start receiving deliveries',
                    icon: Icons.play_circle_outline,
                    color: AppTheme.successColor,
                    onTap: () {
                      // TODO: Go online
                    },
                  ),
                ),
                SizedBox(width: 16),
                Expanded(
                  child: _ActionCard(
                    title: 'View Earnings',
                    subtitle: 'Check your earnings',
                    icon: Icons.account_balance_wallet,
                    color: AppTheme.primaryColor,
                    onTap: () {
                      // TODO: View earnings
                    },
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class _StatsCard extends StatelessWidget {
  final String title;
  final String value;
  final String subtitle;
  final IconData icon;
  final Color color;

  const _StatsCard({
    required this.title,
    required this.value,
    required this.subtitle,
    required this.icon,
    required this.color,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(icon, color: color, size: 20),
                const SizedBox(width: 8),
                Text(title, style: AppTheme.labelMedium),
              ],
            ),
            const SizedBox(height: 8),
            Text(value, style: AppTheme.titleLarge),
            Text(subtitle, style: AppTheme.bodySmall),
          ],
        ),
      ),
    );
  }
}

class _ActionCard extends StatelessWidget {
  final String title;
  final String subtitle;
  final IconData icon;
  final Color color;
  final VoidCallback onTap;

  const _ActionCard({
    required this.title,
    required this.subtitle,
    required this.icon,
    required this.color,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Icon(icon, color: color, size: 32),
              const SizedBox(height: 12),
              Text(title, style: AppTheme.titleSmall),
              const SizedBox(height: 4),
              Text(subtitle, style: AppTheme.bodySmall),
            ],
          ),
        ),
      ),
    );
  }
}