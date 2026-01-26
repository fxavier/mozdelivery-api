import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../bloc/delivery_bloc.dart';
import '../bloc/delivery_event.dart';
import '../bloc/delivery_state.dart';
import '../widgets/delivery_card.dart';
import 'delivery_detail_page.dart';
import 'delivery_route_page.dart';
import '../../../../core/theme/app_theme.dart';

class DeliveriesPage extends StatefulWidget {
  const DeliveriesPage({super.key});

  @override
  State<DeliveriesPage> createState() => _DeliveriesPageState();
}

class _DeliveriesPageState extends State<DeliveriesPage> {
  @override
  void initState() {
    super.initState();
    context.read<DeliveryBloc>().add(LoadAssignedDeliveries());
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Deliveries'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () {
              context.read<DeliveryBloc>().add(RefreshDeliveries());
            },
          ),
        ],
      ),
      body: BlocConsumer<DeliveryBloc, DeliveryState>(
        listener: (context, state) {
          if (state is DeliveryActionSuccess) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Text(state.message),
                backgroundColor: Colors.green,
              ),
            );
          } else if (state is DeliveryActionError) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Text(state.message),
                backgroundColor: Colors.red,
              ),
            );
          }
        },
        builder: (context, state) {
          if (state is DeliveryLoading) {
            return const Center(
              child: CircularProgressIndicator(),
            );
          }

          if (state is DeliveryError) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(
                    Icons.error_outline,
                    size: 64,
                    color: Colors.red,
                  ),
                  const SizedBox(height: 16),
                  Text(
                    'Error loading deliveries',
                    style: AppTheme.titleMedium,
                  ),
                  const SizedBox(height: 8),
                  Text(
                    state.message,
                    style: AppTheme.bodyMedium,
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 16),
                  ElevatedButton(
                    onPressed: () {
                      context.read<DeliveryBloc>().add(LoadAssignedDeliveries());
                    },
                    child: const Text('Retry'),
                  ),
                ],
              ),
            );
          }

          if (state is DeliveryLoaded) {
            if (state.deliveries.isEmpty) {
              return const Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(
                      Icons.delivery_dining_outlined,
                      size: 64,
                      color: AppTheme.textSecondaryColor,
                    ),
                    SizedBox(height: 16),
                    Text(
                      'No deliveries available',
                      style: AppTheme.titleMedium,
                    ),
                    SizedBox(height: 8),
                    Text(
                      'Go online to start receiving delivery requests',
                      style: AppTheme.bodyMedium,
                      textAlign: TextAlign.center,
                    ),
                  ],
                ),
              );
            }

            return RefreshIndicator(
              onRefresh: () async {
                context.read<DeliveryBloc>().add(RefreshDeliveries());
              },
              child: ListView.builder(
                itemCount: state.deliveries.length,
                itemBuilder: (context, index) {
                  final delivery = state.deliveries[index];
                  return DeliveryCard(
                    delivery: delivery,
                    onTap: () => _navigateToDeliveryDetail(delivery),
                    onAccept: () => _acceptDelivery(delivery.deliveryId),
                    onViewRoute: () => _navigateToRoute(delivery),
                  );
                },
              ),
            );
          }

          return const Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(
                  Icons.delivery_dining_outlined,
                  size: 64,
                  color: AppTheme.textSecondaryColor,
                ),
                SizedBox(height: 16),
                Text(
                  'No deliveries available',
                  style: AppTheme.titleMedium,
                ),
                SizedBox(height: 8),
                Text(
                  'Go online to start receiving delivery requests',
                  style: AppTheme.bodyMedium,
                  textAlign: TextAlign.center,
                ),
              ],
            ),
          );
        },
      ),
    );
  }

  void _navigateToDeliveryDetail(delivery) {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => DeliveryDetailPage(delivery: delivery),
      ),
    );
  }

  void _acceptDelivery(String deliveryId) {
    context.read<DeliveryBloc>().add(AcceptDelivery(deliveryId));
  }

  void _navigateToRoute(delivery) {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => DeliveryRoutePage(delivery: delivery),
      ),
    );
  }
}