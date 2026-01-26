import '../../../merchants/data/models/merchant_model.dart';
import '../../../merchants/data/models/catalog_model.dart';

abstract class MerchantRepository {
  Future<List<MerchantModel>> getMerchants({
    String? city,
    String? vertical,
  });

  Future<MerchantModel> getMerchant(String merchantId);

  Future<List<CatalogModel>> getMerchantCatalogs(String merchantId);

  Future<CatalogModel> getCatalog(String catalogId);

  Future<List<CategoryModel>> getCatalogCategories(String catalogId);

  Future<CategoryModel> getCategory(String categoryId);

  Future<List<ProductModel>> getCategoryProducts(String categoryId);

  Future<ProductModel> getProduct(String productId);

  Future<List<MerchantModel>> searchMerchants({
    required String query,
    String? city,
    String? vertical,
  });

  Future<List<ProductModel>> searchProducts({
    required String query,
    String? merchantId,
    String? categoryId,
  });
}