import 'dart:io';
import 'package:dio/dio.dart';

class ImageUploadService {
  final Dio _dio;

  ImageUploadService(this._dio);

  /// Uploads an image file and returns the URL
  Future<String> uploadProofOfDeliveryImage(String imagePath) async {
    try {
      final file = File(imagePath);
      if (!file.existsSync()) {
        throw Exception('Image file does not exist');
      }

      final fileName = 'proof_${DateTime.now().millisecondsSinceEpoch}.${_getFileExtension(imagePath)}';
      
      final formData = FormData.fromMap({
        'file': await MultipartFile.fromFile(
          imagePath,
          filename: fileName,
        ),
        'type': 'proof_of_delivery',
      });

      final response = await _dio.post(
        '/api/v1/uploads/proof-of-delivery',
        data: formData,
        options: Options(
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        ),
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        return response.data['url'] as String;
      } else {
        throw Exception('Failed to upload image: ${response.statusMessage}');
      }
    } catch (e) {
      if (e is DioException) {
        throw Exception('Network error during image upload: ${e.message}');
      }
      throw Exception('Failed to upload image: $e');
    }
  }

  String _getFileExtension(String path) {
    return path.split('.').last.toLowerCase();
  }
}