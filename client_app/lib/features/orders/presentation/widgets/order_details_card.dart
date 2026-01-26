import 'package:flutter/material.dart';

import '../../data/models/order_models.dart';

class OrderDetailsCard extends StatelessWidget {
  final GuestTrackingResponse orderData;

  const OrderDetailsCard({
    super.key,
    required this.orderData,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Card(
      margin: const EdgeInsets.all(16),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Order Details',
              style: theme.textTheme.titleLarge?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            _buildDetailRow('Order ID', orderData.orderId),
            _buildDetailRow('Merchant', orderData.merchantName),
            _buildDetailRow('Total Amount', '${orderData.totalAmount.toStringAsFixed(2)} ${orderData.currency}'),
            _buildDetailRow('Order Date', _formatDateTime(orderData.createdAt)),
            const SizedBox(height: 16),
            Text(
              'Items',
              style: theme.textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            ...orderData.items.map((item) => _buildItemRow(context, item)),
            const SizedBox(height: 16),
            Text(
              'Delivery Address',
              style: theme.textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              _formatAddress(orderData.deliveryAddress),
              style: theme.textTheme.bodyMedium,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildDetailRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 8),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 120,
            child: Text(
              '$label:',
              style: const TextStyle(
                fontWeight: FontWeight.w500,
                color: Colors.grey,
              ),
            ),
          ),
          Expanded(
            child: Text(
              value,
              style: const TextStyle(fontWeight: FontWeight.w500),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildItemRow(BuildContext context, OrderItemResponse item) {
    final theme = Theme.of(context);
    final itemTotal = item.price * item.quantity;
    final modifierTotal = item.modifiers?.fold(0.0, (sum, mod) => sum + (mod.priceAdjustment ?? 0.0)) ?? 0.0;
    final totalPrice = itemTotal + modifierTotal;

    return Container(
      margin: const EdgeInsets.only(bottom: 8),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.grey[50],
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: Colors.grey[200]!),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Expanded(
                child: Text(
                  item.productName,
                  style: theme.textTheme.bodyMedium?.copyWith(
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ),
              Text(
                '${totalPrice.toStringAsFixed(2)} ${orderData.currency}',
                style: theme.textTheme.bodyMedium?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          const SizedBox(height: 4),
          Text(
            'Quantity: ${item.quantity} × ${item.price.toStringAsFixed(2)} ${orderData.currency}',
            style: theme.textTheme.bodySmall?.copyWith(
              color: Colors.grey[600],
            ),
          ),
          if (item.modifiers != null && item.modifiers!.isNotEmpty) ...[
            const SizedBox(height: 4),
            ...item.modifiers!.map((modifier) => Text(
              '+ ${modifier.optionName}${modifier.priceAdjustment != null ? ' (+${modifier.priceAdjustment!.toStringAsFixed(2)} ${orderData.currency})' : ''}',
              style: theme.textTheme.bodySmall?.copyWith(
                color: Colors.grey[600],
                fontStyle: FontStyle.italic,
              ),
            )),
          ],
          if (item.notes != null && item.notes!.isNotEmpty) ...[
            const SizedBox(height: 4),
            Text(
              'Note: ${item.notes}',
              style: theme.textTheme.bodySmall?.copyWith(
                color: Colors.grey[600],
                fontStyle: FontStyle.italic,
              ),
            ),
          ],
        ],
      ),
    );
  }

  String _formatAddress(DeliveryAddressResponse address) {
    final parts = <String>[
      address.street,
      if (address.additionalInfo != null && address.additionalInfo!.isNotEmpty)
        address.additionalInfo!,
      address.city,
      if (address.state != null && address.state!.isNotEmpty) address.state!,
      if (address.postalCode != null && address.postalCode!.isNotEmpty)
        address.postalCode!,
      if (address.country != null && address.country!.isNotEmpty) address.country!,
    ];
    return parts.join(', ');
  }

  String _formatDateTime(DateTime dateTime) {
    return '${dateTime.day}/${dateTime.month}/${dateTime.year} at ${dateTime.hour}:${dateTime.minute.toString().padLeft(2, '0')}';
  }
}