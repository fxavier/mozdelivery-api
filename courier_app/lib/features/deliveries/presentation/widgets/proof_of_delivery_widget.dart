import 'dart:io';
import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:signature/signature.dart';
import 'package:path_provider/path_provider.dart';
import '../../../../core/theme/app_theme.dart';

enum ProofOfDeliveryType { photo, signature }

class ProofOfDeliveryWidget extends StatefulWidget {
  final Function(String? imagePath) onProofCaptured;
  final String? initialImagePath;

  const ProofOfDeliveryWidget({
    super.key,
    required this.onProofCaptured,
    this.initialImagePath,
  });

  @override
  State<ProofOfDeliveryWidget> createState() => _ProofOfDeliveryWidgetState();
}

class _ProofOfDeliveryWidgetState extends State<ProofOfDeliveryWidget> {
  final ImagePicker _picker = ImagePicker();
  final SignatureController _signatureController = SignatureController(
    penStrokeWidth: 2,
    penColor: Colors.black,
    exportBackgroundColor: Colors.white,
  );

  ProofOfDeliveryType _selectedType = ProofOfDeliveryType.photo;
  String? _capturedImagePath;
  bool _isSignatureEmpty = true;

  @override
  void initState() {
    super.initState();
    _capturedImagePath = widget.initialImagePath;
    _signatureController.addListener(_onSignatureChanged);
  }

  @override
  void dispose() {
    _signatureController.removeListener(_onSignatureChanged);
    _signatureController.dispose();
    super.dispose();
  }

  void _onSignatureChanged() {
    setState(() {
      _isSignatureEmpty = _signatureController.isEmpty;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Proof of Delivery',
              style: AppTheme.titleMedium.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            Text(
              'Capture a photo or get customer signature as proof of delivery',
              style: AppTheme.bodySmall.copyWith(color: AppTheme.textSecondaryColor),
            ),
            const SizedBox(height: 16),
            _buildTypeSelector(),
            const SizedBox(height: 16),
            if (_selectedType == ProofOfDeliveryType.photo) ...[
              _buildPhotoCapture(),
            ] else ...[
              _buildSignatureCapture(),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildTypeSelector() {
    return Row(
      children: [
        Expanded(
          child: RadioListTile<ProofOfDeliveryType>(
            title: const Text('Photo'),
            value: ProofOfDeliveryType.photo,
            groupValue: _selectedType,
            onChanged: (value) {
              setState(() {
                _selectedType = value!;
                if (_selectedType == ProofOfDeliveryType.signature) {
                  _capturedImagePath = null;
                  widget.onProofCaptured(null);
                }
              });
            },
            dense: true,
            contentPadding: EdgeInsets.zero,
          ),
        ),
        Expanded(
          child: RadioListTile<ProofOfDeliveryType>(
            title: const Text('Signature'),
            value: ProofOfDeliveryType.signature,
            groupValue: _selectedType,
            onChanged: (value) {
              setState(() {
                _selectedType = value!;
                if (_selectedType == ProofOfDeliveryType.photo) {
                  _signatureController.clear();
                }
              });
            },
            dense: true,
            contentPadding: EdgeInsets.zero,
          ),
        ),
      ],
    );
  }

  Widget _buildPhotoCapture() {
    return Column(
      children: [
        if (_capturedImagePath != null) ...[
          Container(
            height: 200,
            width: double.infinity,
            decoration: BoxDecoration(
              border: Border.all(color: Colors.grey.shade300),
              borderRadius: BorderRadius.circular(8),
            ),
            child: ClipRRect(
              borderRadius: BorderRadius.circular(8),
              child: Image.file(
                File(_capturedImagePath!),
                fit: BoxFit.cover,
              ),
            ),
          ),
          const SizedBox(height: 12),
        ],
        Row(
          children: [
            Expanded(
              child: OutlinedButton.icon(
                onPressed: () => _capturePhoto(ImageSource.camera),
                icon: const Icon(Icons.camera_alt),
                label: const Text('Take Photo'),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: OutlinedButton.icon(
                onPressed: () => _capturePhoto(ImageSource.gallery),
                icon: const Icon(Icons.photo_library),
                label: const Text('From Gallery'),
              ),
            ),
          ],
        ),
        if (_capturedImagePath != null) ...[
          const SizedBox(height: 8),
          TextButton.icon(
            onPressed: _removePhoto,
            icon: const Icon(Icons.delete, color: Colors.red),
            label: const Text('Remove Photo', style: TextStyle(color: Colors.red)),
          ),
        ],
      ],
    );
  }

  Widget _buildSignatureCapture() {
    return Column(
      children: [
        Container(
          height: 200,
          width: double.infinity,
          decoration: BoxDecoration(
            border: Border.all(color: Colors.grey.shade300),
            borderRadius: BorderRadius.circular(8),
          ),
          child: ClipRRect(
            borderRadius: BorderRadius.circular(8),
            child: Signature(
              controller: _signatureController,
              backgroundColor: Colors.white,
            ),
          ),
        ),
        const SizedBox(height: 12),
        Row(
          children: [
            Expanded(
              child: OutlinedButton.icon(
                onPressed: _signatureController.clear,
                icon: const Icon(Icons.clear),
                label: const Text('Clear'),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: ElevatedButton.icon(
                onPressed: _isSignatureEmpty ? null : _saveSignature,
                icon: const Icon(Icons.save),
                label: const Text('Save Signature'),
              ),
            ),
          ],
        ),
      ],
    );
  }

  Future<void> _capturePhoto(ImageSource source) async {
    try {
      final XFile? image = await _picker.pickImage(
        source: source,
        maxWidth: 1024,
        maxHeight: 1024,
        imageQuality: 85,
      );

      if (image != null) {
        setState(() {
          _capturedImagePath = image.path;
        });
        widget.onProofCaptured(image.path);
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Failed to capture photo: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  void _removePhoto() {
    setState(() {
      _capturedImagePath = null;
    });
    widget.onProofCaptured(null);
  }

  Future<void> _saveSignature() async {
    try {
      if (_signatureController.isNotEmpty) {
        final Uint8List? signature = await _signatureController.toPngBytes();
        if (signature != null) {
          final directory = await getTemporaryDirectory();
          final file = File('${directory.path}/signature_${DateTime.now().millisecondsSinceEpoch}.png');
          await file.writeAsBytes(signature);
          
          setState(() {
            _capturedImagePath = file.path;
          });
          widget.onProofCaptured(file.path);

          if (mounted) {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(
                content: Text('Signature saved successfully'),
                backgroundColor: Colors.green,
              ),
            );
          }
        }
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Failed to save signature: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }
}