import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:geolocator/geolocator.dart';
import '../../domain/entities/delivery.dart';
import '../bloc/delivery_bloc.dart';
import '../bloc/delivery_event.dart';
import '../bloc/delivery_state.dart';
import '../widgets/proof_of_delivery_widget.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/services/image_upload_service.dart';
import '../../../../core/di/injection.dart';

class DeliveryCompletionPage extends StatefulWidget {
  final Delivery delivery;

  const DeliveryCompletionPage({
    super.key,
    required this.delivery,
  });

  @override
  State<DeliveryCompletionPage> createState() => _DeliveryCompletionPageState();
}

class _DeliveryCompletionPageState extends State<DeliveryCompletionPage> {
  final _formKey = GlobalKey<FormState>();
  final _confirmationCodeController = TextEditingController();
  final _notesController = TextEditingController();
  final ImageUploadService _imageUploadService = getIt<ImageUploadService>();
  
  Position? _currentPosition;
  bool _isLoading = false;
  String? _proofOfDeliveryImagePath;

  @override
  void initState() {
    super.initState();
    _getCurrentLocation();
  }

  @override
  void dispose() {
    _confirmationCodeController.dispose();
    _notesController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Complete Delivery'),
      ),
      body: BlocListener<DeliveryBloc, DeliveryState>(
        listener: (context, state) {
          if (state is DeliveryActionLoading) {
            setState(() {
              _isLoading = true;
            });
          } else {
            setState(() {
              _isLoading = false;
            });
            
            if (state is DeliveryActionSuccess) {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(
                  content: Text(state.message),
                  backgroundColor: Colors.green,
                ),
              );
              Navigator.of(context).popUntil((route) => route.isFirst);
            } else if (state is DeliveryActionError) {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(
                  content: Text(state.message),
                  backgroundColor: Colors.red,
                ),
              );
            }
          }
        },
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(16),
          child: Form(
            key: _formKey,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                _buildDeliveryInfo(),
                const SizedBox(height: 24),
                _buildConfirmationCodeField(),
                const SizedBox(height: 16),
                _buildNotesField(),
                const SizedBox(height: 16),
                _buildProofOfDeliveryWidget(),
                const SizedBox(height: 24),
                _buildLocationInfo(),
                const SizedBox(height: 32),
                _buildCompleteButton(),
              ],
            ),
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
            Row(
              children: [
                const Icon(Icons.receipt, size: 16, color: AppTheme.textSecondaryColor),
                const SizedBox(width: 8),
                Text(
                  'Order #${widget.delivery.orderId.substring(0, 8)}',
                  style: AppTheme.bodyMedium,
                ),
              ],
            ),
            const SizedBox(height: 8),
            Row(
              children: [
                const Icon(Icons.person, size: 16, color: AppTheme.textSecondaryColor),
                const SizedBox(width: 8),
                Text(
                  widget.delivery.customerName,
                  style: AppTheme.bodyMedium,
                ),
              ],
            ),
            const SizedBox(height: 8),
            Row(
              children: [
                const Icon(Icons.location_on, size: 16, color: AppTheme.textSecondaryColor),
                const SizedBox(width: 8),
                Expanded(
                  child: Text(
                    widget.delivery.deliveryAddress,
                    style: AppTheme.bodyMedium,
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildConfirmationCodeField() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Delivery Confirmation Code',
          style: AppTheme.titleMedium.copyWith(fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 8),
        Text(
          'Enter the 4-digit confirmation code provided by the customer',
          style: AppTheme.bodySmall.copyWith(color: AppTheme.textSecondaryColor),
        ),
        const SizedBox(height: 12),
        TextFormField(
          controller: _confirmationCodeController,
          decoration: const InputDecoration(
            labelText: 'Confirmation Code',
            hintText: 'Enter 4-digit code',
            prefixIcon: Icon(Icons.lock),
            border: OutlineInputBorder(),
          ),
          keyboardType: TextInputType.number,
          maxLength: 4,
          validator: (value) {
            if (value == null || value.isEmpty) {
              return 'Please enter the confirmation code';
            }
            if (value.length != 4) {
              return 'Confirmation code must be 4 digits';
            }
            if (!RegExp(r'^\d{4}$').hasMatch(value)) {
              return 'Confirmation code must contain only numbers';
            }
            return null;
          },
        ),
      ],
    );
  }

  Widget _buildNotesField() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Delivery Notes (Optional)',
          style: AppTheme.titleMedium.copyWith(fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 8),
        Text(
          'Add any additional notes about the delivery',
          style: AppTheme.bodySmall.copyWith(color: AppTheme.textSecondaryColor),
        ),
        const SizedBox(height: 12),
        TextFormField(
          controller: _notesController,
          decoration: const InputDecoration(
            labelText: 'Notes',
            hintText: 'Enter delivery notes...',
            prefixIcon: Icon(Icons.note),
            border: OutlineInputBorder(),
          ),
          maxLines: 3,
          maxLength: 200,
        ),
      ],
    );
  }

  Widget _buildProofOfDeliveryWidget() {
    return ProofOfDeliveryWidget(
      onProofCaptured: (imagePath) {
        setState(() {
          _proofOfDeliveryImagePath = imagePath;
        });
      },
      initialImagePath: _proofOfDeliveryImagePath,
    );
  }

  Widget _buildLocationInfo() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Current Location',
              style: AppTheme.titleMedium.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 12),
            if (_currentPosition != null) ...[
              Row(
                children: [
                  const Icon(Icons.my_location, size: 16, color: Colors.green),
                  const SizedBox(width: 8),
                  Text(
                    'Lat: ${_currentPosition!.latitude.toStringAsFixed(6)}',
                    style: AppTheme.bodySmall,
                  ),
                ],
              ),
              const SizedBox(height: 4),
              Row(
                children: [
                  const Icon(Icons.my_location, size: 16, color: Colors.green),
                  const SizedBox(width: 8),
                  Text(
                    'Lng: ${_currentPosition!.longitude.toStringAsFixed(6)}',
                    style: AppTheme.bodySmall,
                  ),
                ],
              ),
            ] else ...[
              const Row(
                children: [
                  CircularProgressIndicator(strokeWidth: 2),
                  SizedBox(width: 12),
                  Text('Getting current location...'),
                ],
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildCompleteButton() {
    return SizedBox(
      width: double.infinity,
      child: ElevatedButton.icon(
        onPressed: _isLoading || _currentPosition == null ? null : _completeDelivery,
        icon: _isLoading
            ? const SizedBox(
                width: 20,
                height: 20,
                child: CircularProgressIndicator(strokeWidth: 2),
              )
            : const Icon(Icons.check_circle),
        label: Text(_isLoading ? 'Completing...' : 'Complete Delivery'),
        style: ElevatedButton.styleFrom(
          backgroundColor: Colors.green,
          foregroundColor: Colors.white,
          padding: const EdgeInsets.symmetric(vertical: 16),
        ),
      ),
    );
  }

  void _getCurrentLocation() async {
    try {
      final position = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high,
      );
      if (mounted) {
        setState(() {
          _currentPosition = position;
        });
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Failed to get location: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  void _completeDelivery() async {
    if (_formKey.currentState!.validate() && _currentPosition != null) {
      setState(() {
        _isLoading = true;
      });

      try {
        String? imageUrl;
        
        // Upload proof of delivery image if provided
        if (_proofOfDeliveryImagePath != null) {
          try {
            imageUrl = await _imageUploadService.uploadProofOfDeliveryImage(_proofOfDeliveryImagePath!);
          } catch (e) {
            if (mounted) {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(
                  content: Text('Failed to upload proof of delivery: $e'),
                  backgroundColor: Colors.orange,
                ),
              );
            }
            // Continue with delivery completion even if image upload fails
          }
        }

        // Complete the delivery
        if (mounted) {
          final bloc = context.read<DeliveryBloc>();
          bloc.add(
            CompleteDelivery(
              widget.delivery.deliveryId,
              _confirmationCodeController.text,
              _currentPosition!.latitude,
              _currentPosition!.longitude,
              notes: _notesController.text.isNotEmpty ? _notesController.text : null,
              proofOfDeliveryImageUrl: imageUrl,
            ),
          );
        }
      } catch (e) {
        if (mounted) {
          setState(() {
            _isLoading = false;
          });
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('Failed to complete delivery: $e'),
              backgroundColor: Colors.red,
            ),
          );
        }
      }
    }
  }
}