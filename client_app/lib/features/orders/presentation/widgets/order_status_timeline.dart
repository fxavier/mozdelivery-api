import 'package:flutter/material.dart';

import '../../domain/entities/order.dart';

class OrderStatusTimeline extends StatelessWidget {
  final OrderStatus currentStatus;
  final DateTime lastUpdated;

  const OrderStatusTimeline({
    super.key,
    required this.currentStatus,
    required this.lastUpdated,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final statuses = _getOrderStatuses();

    return Card(
      margin: const EdgeInsets.all(16),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Order Status',
              style: theme.textTheme.titleLarge?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'Last updated: ${_formatDateTime(lastUpdated)}',
              style: theme.textTheme.bodySmall?.copyWith(
                color: Colors.grey[600],
              ),
            ),
            const SizedBox(height: 16),
            ...statuses.map((status) => _buildTimelineItem(
              context,
              status,
              _isStatusCompleted(status),
              _isCurrentStatus(status),
            )),
          ],
        ),
      ),
    );
  }

  List<OrderStatus> _getOrderStatuses() {
    return [
      OrderStatus.created,
      OrderStatus.paymentConfirmed,
      OrderStatus.preparing,
      OrderStatus.readyForPickup,
      OrderStatus.pickedUp,
      OrderStatus.outForDelivery,
      OrderStatus.delivered,
    ];
  }

  bool _isStatusCompleted(OrderStatus status) {
    final currentIndex = _getOrderStatuses().indexOf(currentStatus);
    final statusIndex = _getOrderStatuses().indexOf(status);
    return statusIndex <= currentIndex;
  }

  bool _isCurrentStatus(OrderStatus status) {
    return status == currentStatus;
  }

  Widget _buildTimelineItem(
    BuildContext context,
    OrderStatus status,
    bool isCompleted,
    bool isCurrent,
  ) {
    final theme = Theme.of(context);
    final isLast = status == OrderStatus.delivered;

    return Row(
      children: [
        Column(
          children: [
            Container(
              width: 24,
              height: 24,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: isCompleted
                    ? theme.primaryColor
                    : isCurrent
                        ? theme.primaryColor.withValues(alpha: 0.3)
                        : Colors.grey[300],
                border: Border.all(
                  color: isCompleted || isCurrent
                      ? theme.primaryColor
                      : Colors.grey[400]!,
                  width: 2,
                ),
              ),
              child: isCompleted
                  ? Icon(
                      Icons.check,
                      size: 16,
                      color: Colors.white,
                    )
                  : isCurrent
                      ? Container(
                          width: 8,
                          height: 8,
                          decoration: BoxDecoration(
                            shape: BoxShape.circle,
                            color: theme.primaryColor,
                          ),
                        )
                      : null,
            ),
            if (!isLast)
              Container(
                width: 2,
                height: 32,
                color: isCompleted ? theme.primaryColor : Colors.grey[300],
              ),
          ],
        ),
        const SizedBox(width: 16),
        Expanded(
          child: Padding(
            padding: const EdgeInsets.only(bottom: 16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  status.displayName,
                  style: theme.textTheme.bodyLarge?.copyWith(
                    fontWeight: isCurrent ? FontWeight.bold : FontWeight.normal,
                    color: isCompleted || isCurrent
                        ? theme.textTheme.bodyLarge?.color
                        : Colors.grey[600],
                  ),
                ),
                if (isCurrent) ...[
                  const SizedBox(height: 4),
                  Text(
                    _getStatusDescription(status),
                    style: theme.textTheme.bodySmall?.copyWith(
                      color: Colors.grey[600],
                    ),
                  ),
                ],
              ],
            ),
          ),
        ),
      ],
    );
  }

  String _getStatusDescription(OrderStatus status) {
    switch (status) {
      case OrderStatus.created:
        return 'Your order has been placed successfully';
      case OrderStatus.paymentConfirmed:
        return 'Payment confirmed, preparing your order';
      case OrderStatus.preparing:
        return 'The merchant is preparing your order';
      case OrderStatus.readyForPickup:
        return 'Your order is ready for pickup';
      case OrderStatus.pickedUp:
        return 'Courier has picked up your order';
      case OrderStatus.outForDelivery:
        return 'Your order is on the way';
      case OrderStatus.delivered:
        return 'Order delivered successfully';
      case OrderStatus.cancelled:
        return 'Order has been cancelled';
      case OrderStatus.deliveryFailed:
        return 'Delivery attempt failed';
      default:
        return '';
    }
  }

  String _formatDateTime(DateTime dateTime) {
    final now = DateTime.now();
    final difference = now.difference(dateTime);

    if (difference.inMinutes < 1) {
      return 'Just now';
    } else if (difference.inMinutes < 60) {
      return '${difference.inMinutes} minutes ago';
    } else if (difference.inHours < 24) {
      return '${difference.inHours} hours ago';
    } else {
      return '${dateTime.day}/${dateTime.month}/${dateTime.year} at ${dateTime.hour}:${dateTime.minute.toString().padLeft(2, '0')}';
    }
  }
}