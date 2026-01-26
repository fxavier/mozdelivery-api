import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';

import '../bloc/cart_bloc.dart';
import '../bloc/cart_state.dart';
import '../bloc/guest_checkout_bloc.dart';
import '../bloc/guest_checkout_state.dart';
import '../bloc/guest_checkout_event.dart';
import '../widgets/delivery_address_form.dart';
import '../widgets/contact_info_form.dart';
import '../widgets/payment_method_selector.dart';
import '../widgets/order_summary_widget.dart';
import '../../data/models/order_models.dart';
import '../../domain/entities/order.dart';

class GuestCheckoutPage extends StatefulWidget {
  const GuestCheckoutPage({super.key});

  @override
  State<GuestCheckoutPage> createState() => _GuestCheckoutPageState();
}

class _GuestCheckoutPageState extends State<GuestCheckoutPage> {
  final PageController _pageController = PageController();
  int _currentStep = 0;

  // Form data
  DeliveryAddress? _deliveryAddress;
  GuestInfo? _guestInfo;
  String _paymentMethod = 'cash';

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Checkout'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => _handleBackPress(),
        ),
      ),
      body: BlocListener<GuestCheckoutBloc, GuestCheckoutState>(
        listener: (context, state) {
          if (state is GuestOrderCreated) {
            // Navigate to order confirmation page
            context.pushReplacement('/order-confirmation', extra: state.orderResponse);
          } else if (state is GuestCheckoutError) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Text(state.message),
                backgroundColor: Theme.of(context).colorScheme.error,
              ),
            );
          }
        },
        child: BlocBuilder<CartBloc, CartState>(
          builder: (context, cartState) {
            if (cartState is! CartLoaded || !cartState.hasCart) {
              return const Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(Icons.shopping_cart_outlined, size: 64, color: Colors.grey),
                    SizedBox(height: 16),
                    Text('Your cart is empty'),
                    SizedBox(height: 8),
                    Text('Add some items to proceed with checkout'),
                  ],
                ),
              );
            }

            return Column(
              children: [
                // Progress indicator
                Container(
                  padding: const EdgeInsets.all(16),
                  child: Row(
                    children: [
                      _buildStepIndicator(0, 'Address', _currentStep >= 0),
                      Expanded(child: Container(height: 2, color: _currentStep >= 1 ? Theme.of(context).colorScheme.primary : Colors.grey.shade300)),
                      _buildStepIndicator(1, 'Contact', _currentStep >= 1),
                      Expanded(child: Container(height: 2, color: _currentStep >= 2 ? Theme.of(context).colorScheme.primary : Colors.grey.shade300)),
                      _buildStepIndicator(2, 'Payment', _currentStep >= 2),
                      Expanded(child: Container(height: 2, color: _currentStep >= 3 ? Theme.of(context).colorScheme.primary : Colors.grey.shade300)),
                      _buildStepIndicator(3, 'Review', _currentStep >= 3),
                    ],
                  ),
                ),
                // Page content
                Expanded(
                  child: PageView(
                    controller: _pageController,
                    onPageChanged: (index) => setState(() => _currentStep = index),
                    children: [
                      DeliveryAddressForm(
                        initialAddress: _deliveryAddress,
                        onAddressChanged: (address) => _deliveryAddress = address,
                      ),
                      ContactInfoForm(
                        initialInfo: _guestInfo,
                        onInfoChanged: (info) => _guestInfo = info,
                      ),
                      PaymentMethodSelector(
                        selectedMethod: _paymentMethod,
                        onMethodChanged: (method) => setState(() => _paymentMethod = method),
                      ),
                      OrderSummaryWidget(
                        cart: cartState.currentCart,
                        deliveryAddress: _deliveryAddress,
                        guestInfo: _guestInfo,
                        paymentMethod: _paymentMethod,
                      ),
                    ],
                  ),
                ),
                // Navigation buttons
                Container(
                  padding: const EdgeInsets.all(16),
                  child: Row(
                    children: [
                      if (_currentStep > 0)
                        Expanded(
                          child: OutlinedButton(
                            onPressed: _previousStep,
                            child: const Text('Back'),
                          ),
                        ),
                      if (_currentStep > 0) const SizedBox(width: 16),
                      Expanded(
                        child: BlocBuilder<GuestCheckoutBloc, GuestCheckoutState>(
                          builder: (context, state) {
                            final isLoading = state is GuestCheckoutLoading;
                            return ElevatedButton(
                              onPressed: isLoading ? null : _handleNextStep,
                              child: isLoading
                                  ? const SizedBox(
                                      width: 20,
                                      height: 20,
                                      child: CircularProgressIndicator(strokeWidth: 2),
                                    )
                                  : Text(_currentStep == 3 ? 'Place Order' : 'Next'),
                            );
                          },
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            );
          },
        ),
      ),
    );
  }

  Widget _buildStepIndicator(int step, String label, bool isActive) {
    return Column(
      children: [
        Container(
          width: 32,
          height: 32,
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            color: isActive ? Theme.of(context).colorScheme.primary : Colors.grey.shade300,
          ),
          child: Center(
            child: Text(
              '${step + 1}',
              style: TextStyle(
                color: isActive ? Colors.white : Colors.grey.shade600,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
        ),
        const SizedBox(height: 4),
        Text(
          label,
          style: TextStyle(
            fontSize: 12,
            color: isActive ? Theme.of(context).colorScheme.primary : Colors.grey.shade600,
          ),
        ),
      ],
    );
  }

  void _handleBackPress() {
    if (_currentStep > 0) {
      _previousStep();
    } else {
      context.pop();
    }
  }

  void _previousStep() {
    if (_currentStep > 0) {
      _pageController.previousPage(
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeInOut,
      );
    }
  }

  void _handleNextStep() {
    if (_currentStep < 3) {
      if (_validateCurrentStep()) {
        _pageController.nextPage(
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeInOut,
        );
      }
    } else {
      _placeOrder();
    }
  }

  bool _validateCurrentStep() {
    switch (_currentStep) {
      case 0:
        if (_deliveryAddress == null) {
          _showError('Please enter your delivery address');
          return false;
        }
        return true;
      case 1:
        if (_guestInfo == null) {
          _showError('Please enter your contact information');
          return false;
        }
        return true;
      case 2:
        return true; // Payment method is always selected
      case 3:
        return true; // Review step
      default:
        return false;
    }
  }

  void _placeOrder() {
    final cartState = context.read<CartBloc>().state;
    if (cartState is! CartLoaded || !cartState.hasCart) {
      _showError('Your cart is empty');
      return;
    }

    if (_deliveryAddress == null || _guestInfo == null) {
      _showError('Please complete all required information');
      return;
    }

    final cart = cartState.currentCart;
    final request = GuestOrderRequest(
      merchantId: cart.merchantId,
      items: cart.items.map((item) => OrderItemRequest(
        productId: item.productId,
        quantity: item.quantity,
        modifiers: item.modifiers?.map((mod) => SelectedModifierRequest(
          modifierId: mod.modifierId,
          optionId: mod.optionId,
        )).toList(),
        notes: item.notes,
      )).toList(),
      deliveryAddress: DeliveryAddressRequest(
        street: _deliveryAddress!.street,
        city: _deliveryAddress!.city,
        state: _deliveryAddress!.state,
        postalCode: _deliveryAddress!.postalCode,
        country: _deliveryAddress!.country,
        additionalInfo: _deliveryAddress!.additionalInfo,
        latitude: _deliveryAddress!.latitude,
        longitude: _deliveryAddress!.longitude,
      ),
      guestInfo: GuestInfoRequest(
        contactPhone: _guestInfo!.contactPhone,
        contactEmail: _guestInfo!.contactEmail,
        contactName: _guestInfo!.contactName,
      ),
      paymentInfo: PaymentInfoRequest(method: _paymentMethod),
    );

    context.read<GuestCheckoutBloc>().add(CreateGuestOrder(request: request));
  }

  void _showError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: Theme.of(context).colorScheme.error,
      ),
    );
  }
}