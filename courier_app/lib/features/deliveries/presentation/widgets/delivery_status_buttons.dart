import 'package:flutter/material.dart';
import '../../domain/entities/delivery.dart';
import '../../../../core/theme/app_theme.dart';

class DeliveryStatusButtons extends StatelessWidget {
  final Delivery delivery;
  final Function(DeliveryStatus) onStatusUpdate;
  final VoidCallback? onCompleteDelivery;

  const DeliveryStatusButtons({
    super.key,
    required this.delivery,
    required this.onStatusUpdate,
    this.onCompleteDelivery,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text(
          'Update Delivery Status',
          style: AppTheme.titleMedium.copyWith(fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 16),
        _buildStatusButtons(context),
      ],
    );
  }

  Widget _buildStatusButtons(BuildContext context) {
    switch (delivery.status) {
      case DeliveryStatus.assigned:
        return _buildButton(
          context,
          'Accept Delivery',
          Icons.check_circle,
          Colors.green,
          () => onStatusUpdate(DeliveryStatus.accepted),
        );

      case DeliveryStatus.accepted:
        return Column(
          children: [
            _buildButton(
              context,
              'Mark as Picked Up',
              Icons.shopping_bag,
              Colors.orange,
              () => onStatusUpdate(DeliveryStatus.pickedUp),
            ),
            const SizedBox(height: 8),
            _buildButton(
              context,
              'Cancel Delivery',
              Icons.cancel,
              Colors.red,
              () => _showCancelDialog(context),
              isOutlined: true,
            ),
          ],
        );

      case DeliveryStatus.pickedUp:
        return Column(
          children: [
            _buildButton(
              context,
              'Start Delivery',
              Icons.local_shipping,
              Colors.blue,
              () => onStatusUpdate(DeliveryStatus.outForDelivery),
            ),
            const SizedBox(height: 8),
            _buildButton(
              context,
              'Cancel Delivery',
              Icons.cancel,
              Colors.red,
              () => _showCancelDialog(context),
              isOutlined: true,
            ),
          ],
        );

      case DeliveryStatus.outForDelivery:
        return Column(
          children: [
            _buildButton(
              context,
              'Complete Delivery',
              Icons.done_all,
              Colors.green,
              onCompleteDelivery ?? () {},
            ),
            const SizedBox(height: 8),
            _buildButton(
              context,
              'Mark as Failed',
              Icons.error,
              Colors.red,
              () => _showFailureDialog(context),
              isOutlined: true,
            ),
          ],
        );

      case DeliveryStatus.delivered:
      case DeliveryStatus.cancelled:
      case DeliveryStatus.failed:
        return Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: _getStatusColor(delivery.status).withOpacity(0.1),
            borderRadius: BorderRadius.circular(8),
            border: Border.all(color: _getStatusColor(delivery.status)),
          ),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                _getStatusIcon(delivery.status),
                color: _getStatusColor(delivery.status),
              ),
              const SizedBox(width: 8),
              Text(
                'Delivery ${_getStatusText(delivery.status)}',
                style: AppTheme.titleMedium.copyWith(
                  color: _getStatusColor(delivery.status),
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
        );
    }
  }

  Widget _buildButton(
    BuildContext context,
    String text,
    IconData icon,
    Color color,
    VoidCallback onPressed, {
    bool isOutlined = false,
  }) {
    return SizedBox(
      width: double.infinity,
      child: isOutlined
          ? OutlinedButton.icon(
              onPressed: onPressed,
              icon: Icon(icon, size: 20),
              label: Text(text),
              style: OutlinedButton.styleFrom(
                foregroundColor: color,
                side: BorderSide(color: color),
                padding: const EdgeInsets.symmetric(vertical: 12),
              ),
            )
          : ElevatedButton.icon(
              onPressed: onPressed,
              icon: Icon(icon, size: 20),
              label: Text(text),
              style: ElevatedButton.styleFrom(
                backgroundColor: color,
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 12),
              ),
            ),
    );
  }

  void _showCancelDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Cancel Delivery'),
        content: const Text('Are you sure you want to cancel this delivery?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('No'),
          ),
          TextButton(
            onPressed: () {
              Navigator.of(context).pop();
              onStatusUpdate(DeliveryStatus.cancelled);
            },
            child: const Text('Yes, Cancel'),
          ),
        ],
      ),
    );
  }

  void _showFailureDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Mark as Failed'),
        content: const Text('Are you sure you want to mark this delivery as failed?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('No'),
          ),
          TextButton(
            onPressed: () {
              Navigator.of(context).pop();
              onStatusUpdate(DeliveryStatus.failed);
            },
            child: const Text('Yes, Mark Failed'),
          ),
        ],
      ),
    );
  }

  Color _getStatusColor(DeliveryStatus status) {
    switch (status) {
      case DeliveryStatus.delivered:
        return Colors.green;
      case DeliveryStatus.cancelled:
        return Colors.red;
      case DeliveryStatus.failed:
        return Colors.red.shade700;
      default:
        return Colors.grey;
    }
  }

  IconData _getStatusIcon(DeliveryStatus status) {
    switch (status) {
      case DeliveryStatus.delivered:
        return Icons.check_circle;
      case DeliveryStatus.cancelled:
        return Icons.cancel;
      case DeliveryStatus.failed:
        return Icons.error;
      default:
        return Icons.info;
    }
  }

  String _getStatusText(DeliveryStatus status) {
    switch (status) {
      case DeliveryStatus.delivered:
        return 'Completed';
      case DeliveryStatus.cancelled:
        return 'Cancelled';
      case DeliveryStatus.failed:
        return 'Failed';
      default:
        return 'Unknown';
    }
  }
}