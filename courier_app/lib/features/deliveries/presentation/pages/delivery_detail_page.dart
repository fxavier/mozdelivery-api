import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:intl/intl.dart';
import 'package:url_launcher/url_launcher.dart';
import '../../domain/entities/delivery.dart';
import '../bloc/delivery_bloc.dart';
import '../bloc/delivery_event.dart';
import '../bloc/delivery_state.dart';
import '../widgets/delivery_status_buttons.dart';
import '../../../../core/theme/app_theme.dart';
import 'delivery_completion_page.dart';
import 'delivery_route_page.dart';

class DeliveryDetailPage extends StatelessWidget {
  final Delivery delivery;

  const DeliveryDetailPage({
    super.key,
    required this.delivery,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Order #${delivery.orderId.substring(0, 8)}'),
        actions: [
          IconButton(
            icon: const Icon(Icons.phone),
            onPressed: () => _makePhoneCall(delivery.customerPhone),
          ),
          IconButton(
            icon: const Icon(Icons.map),
            onPressed: () => _navigateToRoute(context),
          ),
        ],
      ),
      body: BlocListener<DeliveryBloc, DeliveryState>(
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
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildDeliveryInfo(),
              const SizedBox(height: 24),
              _buildMerchantInfo(),
              const SizedBox(height: 24),
              _buildCustomerInfo(),
              const SizedBox(height: 24),
              _buildOrderItems(),
              const SizedBox(height: 24),
              _buildStatusButtons(context),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildDeliveryInfo() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Delivery Information',
              style: AppTheme.titleMedium.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 12),
            _buildInfoRow(Icons.receipt, 'Order ID', delivery.orderId),
            _buildInfoRow(Icons.local_shipping, 'Delivery ID', delivery.deliveryId),
            _buildInfoRow(
              Icons.access_time,
              'Pickup Time',
              DateFormat('MMM dd, yyyy HH:mm').format(delivery.estimatedPickupTime),
            ),
            _buildInfoRow(
              Icons.schedule,
              'Delivery Time',
              DateFormat('MMM dd, yyyy HH:mm').format(delivery.estimatedDeliveryTime),
            ),
            _buildInfoRow(
              Icons.attach_money,
              'Total Amount',
              '${delivery.currency} ${delivery.totalAmount.toStringAsFixed(2)}',
            ),
            if (delivery.specialInstructions != null)
              _buildInfoRow(Icons.info, 'Special Instructions', delivery.specialInstructions!),
          ],
        ),
      ),
    );
  }

  Widget _buildMerchantInfo() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Pickup Location',
              style: AppTheme.titleMedium.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 12),
            _buildInfoRow(Icons.store, 'Merchant', delivery.merchantName),
            _buildInfoRow(Icons.location_on, 'Address', delivery.merchantAddress),
          ],
        ),
      ),
    );
  }

  Widget _buildCustomerInfo() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Delivery Location',
              style: AppTheme.titleMedium.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 12),
            _buildInfoRow(Icons.person, 'Customer', delivery.customerName),
            _buildInfoRow(Icons.phone, 'Phone', delivery.customerPhone),
            _buildInfoRow(Icons.location_on, 'Address', delivery.deliveryAddress),
          ],
        ),
      ),
    );
  }

  Widget _buildOrderItems() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Order Items',
              style: AppTheme.titleMedium.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 12),
            ...delivery.items.map((item) => _buildOrderItem(item)),
          ],
        ),
      ),
    );
  }

  Widget _buildOrderItem(OrderItem item) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        children: [
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  item.productName,
                  style: AppTheme.bodyMedium.copyWith(fontWeight: FontWeight.w500),
                ),
                if (item.specialInstructions != null)
                  Text(
                    item.specialInstructions!,
                    style: AppTheme.bodySmall.copyWith(
                      color: AppTheme.textSecondaryColor,
                      fontStyle: FontStyle.italic,
                    ),
                  ),
              ],
            ),
          ),
          Text(
            'x${item.quantity}',
            style: AppTheme.bodyMedium,
          ),
          const SizedBox(width: 16),
          Text(
            '${delivery.currency} ${item.totalPrice.toStringAsFixed(2)}',
            style: AppTheme.bodyMedium.copyWith(fontWeight: FontWeight.w500),
          ),
        ],
      ),
    );
  }

  Widget _buildStatusButtons(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: DeliveryStatusButtons(
          delivery: delivery,
          onStatusUpdate: (status) {
            context.read<DeliveryBloc>().add(
                  UpdateDeliveryStatus(delivery.deliveryId, status),
                );
          },
          onCompleteDelivery: () => _navigateToCompletion(context),
        ),
      ),
    );
  }

  Widget _buildInfoRow(IconData icon, String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(icon, size: 16, color: AppTheme.textSecondaryColor),
          const SizedBox(width: 8),
          SizedBox(
            width: 100,
            child: Text(
              label,
              style: AppTheme.bodySmall.copyWith(
                color: AppTheme.textSecondaryColor,
              ),
            ),
          ),
          Expanded(
            child: Text(
              value,
              style: AppTheme.bodySmall,
            ),
          ),
        ],
      ),
    );
  }

  void _makePhoneCall(String phoneNumber) async {
    final Uri phoneUri = Uri(scheme: 'tel', path: phoneNumber);
    if (await canLaunchUrl(phoneUri)) {
      await launchUrl(phoneUri);
    }
  }

  void _navigateToRoute(BuildContext context) {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => DeliveryRoutePage(delivery: delivery),
      ),
    );
  }

  void _navigateToCompletion(BuildContext context) {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => DeliveryCompletionPage(delivery: delivery),
      ),
    );
  }
}