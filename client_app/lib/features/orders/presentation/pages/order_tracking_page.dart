import 'package:flutter/material.dart';

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
    return Scaffold(
      appBar: AppBar(
        title: const Text('Track Order'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.track_changes, size: 64, color: Colors.grey),
            const SizedBox(height: 16),
            const Text(
              'Order Tracking',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            if (orderId != null) ...[
              Text(
                'Order ID: $orderId',
                style: const TextStyle(color: Colors.grey),
              ),
            ],
            if (guestToken != null) ...[
              Text(
                'Guest Token: $guestToken',
                style: const TextStyle(color: Colors.grey),
              ),
            ],
            const SizedBox(height: 8),
            const Text(
              'This feature will be implemented in future tasks',
              style: TextStyle(color: Colors.grey),
            ),
          ],
        ),
      ),
    );
  }
}