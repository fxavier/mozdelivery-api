import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class GuestTrackingInput extends StatefulWidget {
  const GuestTrackingInput({super.key});

  @override
  State<GuestTrackingInput> createState() => _GuestTrackingInputState();
}

class _GuestTrackingInputState extends State<GuestTrackingInput> {
  final _tokenController = TextEditingController();
  final _formKey = GlobalKey<FormState>();

  @override
  void dispose() {
    _tokenController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Card(
      margin: const EdgeInsets.all(16),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Icon(
                    Icons.search,
                    color: theme.primaryColor,
                  ),
                  const SizedBox(width: 8),
                  Text(
                    'Track Your Order',
                    style: theme.textTheme.titleLarge?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              Text(
                'Enter your tracking token to view order status and details.',
                style: theme.textTheme.bodyMedium?.copyWith(
                  color: Colors.grey[600],
                ),
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _tokenController,
                decoration: const InputDecoration(
                  labelText: 'Tracking Token',
                  hintText: 'Enter your tracking token',
                  prefixIcon: Icon(Icons.confirmation_number),
                  border: OutlineInputBorder(),
                ),
                validator: (value) {
                  if (value == null || value.trim().isEmpty) {
                    return 'Please enter a tracking token';
                  }
                  if (value.trim().length < 10) {
                    return 'Tracking token seems too short';
                  }
                  return null;
                },
                textInputAction: TextInputAction.go,
                onFieldSubmitted: (_) => _trackOrder(),
              ),
              const SizedBox(height: 16),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: _trackOrder,
                  child: const Text('Track Order'),
                ),
              ),
              const SizedBox(height: 8),
              Text(
                'You can find your tracking token in the order confirmation email or SMS.',
                style: theme.textTheme.bodySmall?.copyWith(
                  color: Colors.grey[500],
                  fontStyle: FontStyle.italic,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  void _trackOrder() {
    if (_formKey.currentState?.validate() ?? false) {
      final token = _tokenController.text.trim();
      context.push('/guest/orders/track?token=$token');
    }
  }
}