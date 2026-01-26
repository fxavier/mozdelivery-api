import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../../../../core/di/injection.dart';
import '../../domain/entities/order.dart';
import '../bloc/order_tracking_bloc.dart';
import '../bloc/order_tracking_event.dart';
import '../bloc/order_tracking_state.dart';
import '../widgets/order_status_timeline.dart';
import '../widgets/delivery_confirmation_card.dart';
import '../widgets/order_details_card.dart';

class OrderTrackingPage extends StatelessWidget {
  final String? orderId;
  final String? guestToken;

  const OrderTrackingPage({
    super.key,
    this.orderId,
    this.guestToken,
  });

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (context) => getIt<OrderTrackingBloc>()
        ..add(LoadOrderTracking(
          orderId: orderId,
          guestToken: guestToken,
        )),
      child: _OrderTrackingView(
        orderId: orderId,
        guestToken: guestToken,
      ),
    );
  }
}

class _OrderTrackingView extends StatefulWidget {
  final String? orderId;
  final String? guestToken;

  const _OrderTrackingView({
    this.orderId,
    this.guestToken,
  });

  @override
  State<_OrderTrackingView> createState() => _OrderTrackingViewState();
}

class _OrderTrackingViewState extends State<_OrderTrackingView>
    with WidgetsBindingObserver {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    final bloc = context.read<OrderTrackingBloc>();
    
    if (state == AppLifecycleState.resumed) {
      // App came to foreground, start real-time updates
      bloc.add(const StartRealTimeUpdates());
    } else if (state == AppLifecycleState.paused) {
      // App went to background, stop real-time updates to save battery
      bloc.add(const StopRealTimeUpdates());
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Track Order'),
        actions: [
          BlocBuilder<OrderTrackingBloc, OrderTrackingState>(
            builder: (context, state) {
              if (state is OrderTrackingLoaded) {
                return IconButton(
                  onPressed: () {
                    context.read<OrderTrackingBloc>().add(const RefreshOrderStatus());
                  },
                  icon: const Icon(Icons.refresh),
                  tooltip: 'Refresh',
                );
              }
              return const SizedBox.shrink();
            },
          ),
        ],
      ),
      body: BlocConsumer<OrderTrackingBloc, OrderTrackingState>(
        listener: (context, state) {
          if (state is OrderTrackingError) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Text(state.message),
                backgroundColor: Colors.red,
                action: SnackBarAction(
                  label: 'Retry',
                  textColor: Colors.white,
                  onPressed: () {
                    context.read<OrderTrackingBloc>().add(LoadOrderTracking(
                      orderId: widget.orderId,
                      guestToken: widget.guestToken,
                    ));
                  },
                ),
              ),
            );
          } else if (state is DeliveryCodeResent) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Text(state.message),
                backgroundColor: Colors.green,
              ),
            );
          }
        },
        builder: (context, state) {
          if (state is OrderTrackingLoading) {
            return const Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  CircularProgressIndicator(),
                  SizedBox(height: 16),
                  Text('Loading order details...'),
                ],
              ),
            );
          }

          if (state is OrderTrackingError) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(
                    Icons.error_outline,
                    size: 64,
                    color: Colors.red[300],
                  ),
                  const SizedBox(height: 16),
                  Text(
                    'Unable to load order',
                    style: Theme.of(context).textTheme.headlineSmall,
                  ),
                  const SizedBox(height: 8),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 32),
                    child: Text(
                      state.message,
                      textAlign: TextAlign.center,
                      style: TextStyle(color: Colors.grey[600]),
                    ),
                  ),
                  const SizedBox(height: 24),
                  ElevatedButton(
                    onPressed: () {
                      context.read<OrderTrackingBloc>().add(LoadOrderTracking(
                        orderId: widget.orderId,
                        guestToken: widget.guestToken,
                      ));
                    },
                    child: const Text('Try Again'),
                  ),
                ],
              ),
            );
          }

          if (state is OrderTrackingLoaded || 
              state is DeliveryCodeResending || 
              state is DeliveryCodeResent) {
            final orderData = state is OrderTrackingLoaded
                ? state.orderData
                : state is DeliveryCodeResending
                    ? state.orderData
                    : (state as DeliveryCodeResent).orderData;

            final isRealTimeActive = state is OrderTrackingLoaded 
                ? state.isRealTimeActive 
                : false;

            final lastUpdated = state is OrderTrackingLoaded
                ? state.lastUpdated
                : DateTime.now();

            final isResending = state is DeliveryCodeResending;

            // Start real-time updates when order is loaded
            if (state is OrderTrackingLoaded && !isRealTimeActive) {
              WidgetsBinding.instance.addPostFrameCallback((_) {
                context.read<OrderTrackingBloc>().add(const StartRealTimeUpdates());
              });
            }

            return RefreshIndicator(
              onRefresh: () async {
                context.read<OrderTrackingBloc>().add(const RefreshOrderStatus());
                // Wait a bit for the refresh to complete
                await Future.delayed(const Duration(milliseconds: 500));
              },
              child: SingleChildScrollView(
                physics: const AlwaysScrollableScrollPhysics(),
                child: Column(
                  children: [
                    // Real-time indicator
                    if (isRealTimeActive)
                      Container(
                        width: double.infinity,
                        padding: const EdgeInsets.symmetric(
                          horizontal: 16,
                          vertical: 8,
                        ),
                        color: Colors.green.withValues(alpha: 0.1),
                        child: Row(
                          children: [
                            Icon(
                              Icons.wifi,
                              size: 16,
                              color: Colors.green[700],
                            ),
                            const SizedBox(width: 8),
                            Text(
                              'Real-time updates active',
                              style: TextStyle(
                                color: Colors.green[700],
                                fontSize: 12,
                              ),
                            ),
                          ],
                        ),
                      ),

                    // Order status timeline
                    OrderStatusTimeline(
                      currentStatus: (state is OrderTrackingLoaded ? state : null)?.orderStatus ?? 
                          OrderStatus.fromString(orderData.status),
                      lastUpdated: lastUpdated,
                    ),

                    // Delivery confirmation code (if available)
                    if ((state is OrderTrackingLoaded ? state.showDeliveryCode : false) && 
                        orderData.deliveryConfirmationCode != null)
                      DeliveryConfirmationCard(
                        deliveryCode: orderData.deliveryConfirmationCode!,
                        onResendCode: () {
                          context.read<OrderTrackingBloc>().add(const ResendDeliveryCode());
                        },
                        isResending: isResending,
                      ),

                    // Order details
                    OrderDetailsCard(orderData: orderData),

                    // Bottom padding
                    const SizedBox(height: 16),
                  ],
                ),
              ),
            );
          }

          return const Center(
            child: Text('Something went wrong'),
          );
        },
      ),
    );
  }
}