import 'package:flutter/material.dart';

class MerchantListPage extends StatelessWidget {
  const MerchantListPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Merchants'),
      ),
      body: const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.store, size: 64, color: Colors.grey),
            SizedBox(height: 16),
            Text(
              'Merchant List',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 8),
            Text(
              'This feature will be implemented in future tasks',
              style: TextStyle(color: Colors.grey),
            ),
          ],
        ),
      ),
    );
  }
}