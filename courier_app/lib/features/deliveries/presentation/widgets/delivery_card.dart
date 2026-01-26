import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../../domain/entities/delivery.dart';
import '../../../../core/theme/app_theme.dart';

class DeliveryCard extends StatelessWidget {
  final Delivery delivery;
  final VoidCallback? onTap;
  final VoidCallback? onAccept;
  final VoidCallback? onViewRoute;

  const DeliveryCard({
    super.key,
    required this.delivery,
    this.onTap,
    this.onAccept,
    this.onViewRoute,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      elevation: 2,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(8),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'Order #${delivery.orderId.substring(0, 8)}',
                          style: AppTheme.titleMedium.copyWith(
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        const SizedBox(height: 4),
                        Text(
                          delivery.merchantName,
                          style: AppTheme.bodyMedium.copyWith(
                            color: AppTheme.primaryColor,
                          ),
                        ),
                      ],
                    ),
                  ),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                    decoration: BoxDecoration(
                      color: _getStatusColor(delivery.status),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Text(
                      _getStatusText(delivery.status),
                      style: AppTheme.bodySmall.copyWith(
                        color: Colors.white,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 12),
              Row(
                children: [
                  const Icon(Icons.store, size: 16, color: AppTheme.textSecondaryColor),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      delivery.merchantAddress,
                      style: AppTheme.bodySmall,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 4),
              Row(
                children: [
                  const Icon(Icons.location_on, size: 16, color: AppTheme.textSecondaryColor),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      delivery.deliveryAddress,
                      style: AppTheme.bodySmall,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 8),
              Row(
                children: [
                  const Icon(Icons.person, size: 16, color: AppTheme.textSecondaryColor),
                  const SizedBox(width: 8),
                  Text(
                    delivery.customerName,
                    style: AppTheme.bodySmall,
                  ),
                  const Spacer(),
                  Text(
                    '${delivery.currency} ${delivery.totalAmount.toStringAsFixed(2)}',
                    style: AppTheme.titleMedium.copyWith(
                      fontWeight: FontWeight.bold,
                      color: AppTheme.primaryColor,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 8),
              Row(
                children: [
                  const Icon(Icons.access_time, size: 16, color: AppTheme.textSecondaryColor),
                  const SizedBox(width: 8),
                  Text(
                    'Pickup: ${DateFormat('HH:mm').format(delivery.estimatedPickupTime)}',
                    style: AppTheme.bodySmall,
                  ),
                  const SizedBox(width: 16),
                  Text(
                    'Delivery: ${DateFormat('HH:mm').format(delivery.estimatedDeliveryTime)}',
                    style: AppTheme.bodySmall,
                  ),
                ],
              ),
              if (delivery.specialInstructions != null) ...[
                const SizedBox(height: 8),
                Container(
                  padding: const EdgeInsets.all(8),
                  decoration: BoxDecoration(
                    color: Colors.orange.shade50,
                    borderRadius: BorderRadius.circular(4),
                    border: Border.all(color: Colors.orange.shade200),
                  ),
                  child: Row(
                    children: [
                      Icon(Icons.info_outline, size: 16, color: Colors.orange.shade700),
                      const SizedBox(width: 8),
                      Expanded(
                        child: Text(
                          delivery.specialInstructions!,
                          style: AppTheme.bodySmall.copyWith(
                            color: Colors.orange.shade700,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ],
              const SizedBox(height: 12),
              Row(
                children: [
                  if (delivery.status == DeliveryStatus.assigned && onAccept != null)
                    Expanded(
                      child: ElevatedButton.icon(
                        onPressed: onAccept,
                        icon: const Icon(Icons.check, size: 18),
                        label: const Text('Accept'),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: Colors.green,
                          foregroundColor: Colors.white,
                        ),
                      ),
                    ),
                  if (delivery.status != DeliveryStatus.assigned) ...[
                    if (onViewRoute != null)
                      Expanded(
                        child: OutlinedButton.icon(
                          onPressed: onViewRoute,
                          icon: const Icon(Icons.map, size: 18),
                          label: const Text('View Route'),
                        ),
                      ),
                    const SizedBox(width: 8),
                    Expanded(
                      child: ElevatedButton.icon(
                        onPressed: onTap,
                        icon: const Icon(Icons.visibility, size: 18),
                        label: const Text('View Details'),
                      ),
                    ),
                  ],
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Color _getStatusColor(DeliveryStatus status) {
    switch (status) {
      case DeliveryStatus.assigned:
        return Colors.blue;
      case DeliveryStatus.accepted:
        return Colors.orange;
      case DeliveryStatus.pickedUp:
        return Colors.purple;
      case DeliveryStatus.outForDelivery:
        return Colors.indigo;
      case DeliveryStatus.delivered:
        return Colors.green;
      case DeliveryStatus.cancelled:
        return Colors.red;
      case DeliveryStatus.failed:
        return Colors.red.shade700;
    }
  }

  String _getStatusText(DeliveryStatus status) {
    switch (status) {
      case DeliveryStatus.assigned:
        return 'Assigned';
      case DeliveryStatus.accepted:
        return 'Accepted';
      case DeliveryStatus.pickedUp:
        return 'Picked Up';
      case DeliveryStatus.outForDelivery:
        return 'Out for Delivery';
      case DeliveryStatus.delivered:
        return 'Delivered';
      case DeliveryStatus.cancelled:
        return 'Cancelled';
      case DeliveryStatus.failed:
        return 'Failed';
    }
  }
}