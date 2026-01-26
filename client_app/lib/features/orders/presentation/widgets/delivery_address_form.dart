import 'package:flutter/material.dart';
import '../../domain/entities/order.dart';

class DeliveryAddressForm extends StatefulWidget {
  final DeliveryAddress? initialAddress;
  final Function(DeliveryAddress?) onAddressChanged;

  const DeliveryAddressForm({
    super.key,
    this.initialAddress,
    required this.onAddressChanged,
  });

  @override
  State<DeliveryAddressForm> createState() => _DeliveryAddressFormState();
}

class _DeliveryAddressFormState extends State<DeliveryAddressForm> {
  final _formKey = GlobalKey<FormState>();
  late final TextEditingController _streetController;
  late final TextEditingController _cityController;
  late final TextEditingController _stateController;
  late final TextEditingController _postalCodeController;
  late final TextEditingController _countryController;
  late final TextEditingController _additionalInfoController;

  @override
  void initState() {
    super.initState();
    _streetController = TextEditingController(text: widget.initialAddress?.street ?? '');
    _cityController = TextEditingController(text: widget.initialAddress?.city ?? 'Maputo');
    _stateController = TextEditingController(text: widget.initialAddress?.state ?? '');
    _postalCodeController = TextEditingController(text: widget.initialAddress?.postalCode ?? '');
    _countryController = TextEditingController(text: widget.initialAddress?.country ?? 'Mozambique');
    _additionalInfoController = TextEditingController(text: widget.initialAddress?.additionalInfo ?? '');

    // Add listeners to update the address when fields change
    _streetController.addListener(_updateAddress);
    _cityController.addListener(_updateAddress);
    _stateController.addListener(_updateAddress);
    _postalCodeController.addListener(_updateAddress);
    _countryController.addListener(_updateAddress);
    _additionalInfoController.addListener(_updateAddress);
  }

  @override
  void dispose() {
    _streetController.dispose();
    _cityController.dispose();
    _stateController.dispose();
    _postalCodeController.dispose();
    _countryController.dispose();
    _additionalInfoController.dispose();
    super.dispose();
  }

  void _updateAddress() {
    if (_formKey.currentState?.validate() ?? false) {
      final address = DeliveryAddress(
        street: _streetController.text.trim(),
        city: _cityController.text.trim(),
        state: _stateController.text.trim().isEmpty ? null : _stateController.text.trim(),
        postalCode: _postalCodeController.text.trim().isEmpty ? null : _postalCodeController.text.trim(),
        country: _countryController.text.trim().isEmpty ? null : _countryController.text.trim(),
        additionalInfo: _additionalInfoController.text.trim().isEmpty ? null : _additionalInfoController.text.trim(),
      );
      widget.onAddressChanged(address);
    } else {
      widget.onAddressChanged(null);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Form(
        key: _formKey,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Delivery Address',
              style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'Where should we deliver your order?',
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: Theme.of(context).colorScheme.onSurfaceVariant,
              ),
            ),
            const SizedBox(height: 24),
            Expanded(
              child: SingleChildScrollView(
                child: Column(
                  children: [
                    TextFormField(
                      controller: _streetController,
                      decoration: const InputDecoration(
                        labelText: 'Street Address *',
                        hintText: 'Enter your street address',
                        prefixIcon: Icon(Icons.location_on),
                      ),
                      validator: (value) {
                        if (value == null || value.trim().isEmpty) {
                          return 'Street address is required';
                        }
                        return null;
                      },
                      textInputAction: TextInputAction.next,
                    ),
                    const SizedBox(height: 16),
                    Row(
                      children: [
                        Expanded(
                          flex: 2,
                          child: TextFormField(
                            controller: _cityController,
                            decoration: const InputDecoration(
                              labelText: 'City *',
                              hintText: 'City',
                              prefixIcon: Icon(Icons.location_city),
                            ),
                            validator: (value) {
                              if (value == null || value.trim().isEmpty) {
                                return 'City is required';
                              }
                              return null;
                            },
                            textInputAction: TextInputAction.next,
                          ),
                        ),
                        const SizedBox(width: 16),
                        Expanded(
                          child: TextFormField(
                            controller: _stateController,
                            decoration: const InputDecoration(
                              labelText: 'State/Province',
                              hintText: 'State',
                            ),
                            textInputAction: TextInputAction.next,
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 16),
                    Row(
                      children: [
                        Expanded(
                          child: TextFormField(
                            controller: _postalCodeController,
                            decoration: const InputDecoration(
                              labelText: 'Postal Code',
                              hintText: 'Postal Code',
                              prefixIcon: Icon(Icons.markunread_mailbox),
                            ),
                            textInputAction: TextInputAction.next,
                          ),
                        ),
                        const SizedBox(width: 16),
                        Expanded(
                          flex: 2,
                          child: TextFormField(
                            controller: _countryController,
                            decoration: const InputDecoration(
                              labelText: 'Country',
                              hintText: 'Country',
                              prefixIcon: Icon(Icons.flag),
                            ),
                            textInputAction: TextInputAction.next,
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 16),
                    TextFormField(
                      controller: _additionalInfoController,
                      decoration: const InputDecoration(
                        labelText: 'Additional Information',
                        hintText: 'Apartment, suite, floor, building, etc.',
                        prefixIcon: Icon(Icons.info_outline),
                      ),
                      maxLines: 2,
                      textInputAction: TextInputAction.done,
                    ),
                    const SizedBox(height: 24),
                    Container(
                      padding: const EdgeInsets.all(16),
                      decoration: BoxDecoration(
                        color: Theme.of(context).colorScheme.surfaceContainerHighest.withValues(alpha: 0.5),
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: Row(
                        children: [
                          Icon(
                            Icons.info,
                            color: Theme.of(context).colorScheme.primary,
                          ),
                          const SizedBox(width: 12),
                          Expanded(
                            child: Text(
                              'Make sure your address is accurate to ensure smooth delivery.',
                              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                                color: Theme.of(context).colorScheme.onSurfaceVariant,
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}