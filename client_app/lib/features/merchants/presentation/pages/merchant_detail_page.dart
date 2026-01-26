import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:cached_network_image/cached_network_image.dart';

import '../bloc/merchant_detail_bloc.dart';
import '../bloc/merchant_detail_event.dart';
import '../bloc/merchant_detail_state.dart';
import '../widgets/product_card.dart';
import '../widgets/category_chip.dart';
import '../../data/models/merchant_model.dart';
import '../../../../core/di/injection.dart';

class MerchantDetailPage extends StatelessWidget {
  final String merchantId;

  const MerchantDetailPage({
    super.key,
    required this.merchantId,
  });

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (context) => getIt<MerchantDetailBloc>()
        ..add(LoadMerchantDetail(merchantId: merchantId)),
      child: const MerchantDetailView(),
    );
  }
}

class MerchantDetailView extends StatefulWidget {
  const MerchantDetailView({super.key});

  @override
  State<MerchantDetailView> createState() => _MerchantDetailViewState();
}

class _MerchantDetailViewState extends State<MerchantDetailView> {
  final TextEditingController _searchController = TextEditingController();

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return BlocBuilder<MerchantDetailBloc, MerchantDetailState>(
      builder: (context, state) {
        if (state is MerchantDetailLoading) {
          return Scaffold(
            appBar: AppBar(title: const Text('Loading...')),
            body: const Center(child: CircularProgressIndicator()),
          );
        }

        if (state is MerchantDetailError) {
          return Scaffold(
            appBar: AppBar(title: const Text('Error')),
            body: Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.error_outline, size: 64, color: Colors.red[300]),
                  const SizedBox(height: 16),
                  Text(
                    'Error Loading Merchant',
                    style: Theme.of(context).textTheme.headlineSmall,
                  ),
                  const SizedBox(height: 8),
                  Text(
                    state.message,
                    style: Theme.of(context).textTheme.bodyMedium,
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 16),
                  ElevatedButton(
                    onPressed: () => Navigator.of(context).pop(),
                    child: const Text('Go Back'),
                  ),
                ],
              ),
            ),
          );
        }

        if (state is MerchantDetailLoaded) {
          return Scaffold(
            appBar: AppBar(
              title: Text(state.merchant.displayName),
              elevation: 0,
            ),
            body: CustomScrollView(
              slivers: [
                // Merchant Header
                SliverToBoxAdapter(
                  child: _buildMerchantHeader(context, state.merchant),
                ),

                // Catalogs
                if (state.catalogs.isNotEmpty)
                  SliverToBoxAdapter(
                    child: _buildCatalogSection(context, state),
                  ),

                // Categories
                if (state.categories.isNotEmpty)
                  SliverToBoxAdapter(
                    child: _buildCategorySection(context, state),
                  ),

                // Product Search
                if (state.selectedCategoryId != null)
                  SliverToBoxAdapter(
                    child: _buildProductSearch(context, state),
                  ),

                // Products
                if (state.filteredProducts.isNotEmpty)
                  SliverList(
                    delegate: SliverChildBuilderDelegate(
                      (context, index) {
                        final product = state.filteredProducts[index];
                        return ProductCard(
                          product: product,
                          onTap: () => _showProductDetails(context, product),
                          onAddToCart: () => _addToCart(context, product),
                        );
                      },
                      childCount: state.filteredProducts.length,
                    ),
                  ),

                // Empty State
                if (state.selectedCategoryId != null && state.filteredProducts.isEmpty)
                  SliverToBoxAdapter(
                    child: _buildEmptyProductsState(context, state),
                  ),
              ],
            ),
          );
        }

        return const Scaffold(
          body: Center(child: CircularProgressIndicator()),
        );
      },
    );
  }

  Widget _buildMerchantHeader(BuildContext context, MerchantModel merchant) {
    return Container(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Merchant Image
              ClipRRect(
                borderRadius: BorderRadius.circular(12),
                child: SizedBox(
                  width: 100,
                  height: 100,
                  child: merchant.imageUrl != null
                      ? CachedNetworkImage(
                          imageUrl: merchant.imageUrl!,
                          fit: BoxFit.cover,
                          placeholder: (context, url) => Container(
                            color: Colors.grey[200],
                            child: const Icon(Icons.store, color: Colors.grey),
                          ),
                          errorWidget: (context, url, error) => Container(
                            color: Colors.grey[200],
                            child: const Icon(Icons.store, color: Colors.grey),
                          ),
                        )
                      : Container(
                          color: Colors.grey[200],
                          child: const Icon(Icons.store, color: Colors.grey, size: 40),
                        ),
                ),
              ),

              const SizedBox(width: 16),

              // Merchant Info
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      merchant.displayName,
                      style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    
                    const SizedBox(height: 4),
                    
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      decoration: BoxDecoration(
                        color: Theme.of(context).primaryColor.withValues(alpha: 0.1),
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Text(
                        MerchantVertical.fromString(merchant.vertical)?.displayName ?? 
                        merchant.vertical.toUpperCase(),
                        style: TextStyle(
                          color: Theme.of(context).primaryColor,
                          fontSize: 12,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ),

                    const SizedBox(height: 8),

                    if (merchant.description != null)
                      Text(
                        merchant.description!,
                        style: Theme.of(context).textTheme.bodyMedium,
                        maxLines: 3,
                        overflow: TextOverflow.ellipsis,
                      ),
                  ],
                ),
              ),
            ],
          ),

          const SizedBox(height: 16),

          // Status and Info Row
          Row(
            children: [
              if (merchant.isOpen != null)
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                  decoration: BoxDecoration(
                    color: merchant.isOpen! ? Colors.green : Colors.red,
                    borderRadius: BorderRadius.circular(16),
                  ),
                  child: Text(
                    merchant.isOpen! ? 'Open' : 'Closed',
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 12,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),

              const SizedBox(width: 16),

              if (merchant.rating != null) ...[
                Icon(Icons.star, color: Colors.amber, size: 16),
                const SizedBox(width: 4),
                Text(
                  merchant.rating!.toStringAsFixed(1),
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    fontWeight: FontWeight.w500,
                  ),
                ),
                if (merchant.reviewCount != null) ...[
                  const SizedBox(width: 4),
                  Text(
                    '(${merchant.reviewCount})',
                    style: Theme.of(context).textTheme.bodySmall,
                  ),
                ],
              ],

              const Spacer(),

              if (merchant.estimatedDeliveryTime != null) ...[
                Icon(Icons.access_time, color: Colors.grey, size: 16),
                const SizedBox(width: 4),
                Text(
                  '${merchant.estimatedDeliveryTime} min',
                  style: Theme.of(context).textTheme.bodySmall,
                ),
              ],
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildCatalogSection(BuildContext context, MerchantDetailLoaded state) {
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Text(
              'Catalogs',
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
          const SizedBox(height: 8),
          SizedBox(
            height: 50,
            child: ListView.builder(
              scrollDirection: Axis.horizontal,
              padding: const EdgeInsets.symmetric(horizontal: 16),
              itemCount: state.catalogs.length,
              itemBuilder: (context, index) {
                final catalog = state.catalogs[index];
                return CatalogChip(
                  catalog: catalog,
                  isSelected: catalog.id == state.selectedCatalogId,
                  onTap: () {
                    context.read<MerchantDetailBloc>().add(
                      LoadCatalogCategories(catalogId: catalog.id),
                    );
                  },
                );
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildCategorySection(BuildContext context, MerchantDetailLoaded state) {
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Text(
              'Categories',
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
          const SizedBox(height: 8),
          SizedBox(
            height: 50,
            child: ListView.builder(
              scrollDirection: Axis.horizontal,
              padding: const EdgeInsets.symmetric(horizontal: 16),
              itemCount: state.categories.length,
              itemBuilder: (context, index) {
                final category = state.categories[index];
                return CategoryChip(
                  category: category,
                  isSelected: category.id == state.selectedCategoryId,
                  onTap: () {
                    context.read<MerchantDetailBloc>().add(
                      LoadCategoryProducts(categoryId: category.id),
                    );
                  },
                );
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildProductSearch(BuildContext context, MerchantDetailLoaded state) {
    return Container(
      padding: const EdgeInsets.all(16),
      child: TextField(
        controller: _searchController,
        decoration: InputDecoration(
          hintText: 'Search products...',
          prefixIcon: const Icon(Icons.search),
          suffixIcon: _searchController.text.isNotEmpty
              ? IconButton(
                  icon: const Icon(Icons.clear),
                  onPressed: () {
                    _searchController.clear();
                    context.read<MerchantDetailBloc>().add(const ClearProductSearch());
                  },
                )
              : null,
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          filled: true,
          fillColor: Colors.grey[50],
        ),
        onChanged: (query) {
          if (query.isEmpty) {
            context.read<MerchantDetailBloc>().add(const ClearProductSearch());
          } else {
            context.read<MerchantDetailBloc>().add(SearchProducts(query: query));
          }
        },
      ),
    );
  }

  Widget _buildEmptyProductsState(BuildContext context, MerchantDetailLoaded state) {
    return Container(
      padding: const EdgeInsets.all(32),
      child: Column(
        children: [
          Icon(
            Icons.inventory_2_outlined,
            size: 64,
            color: Colors.grey[400],
          ),
          const SizedBox(height: 16),
          Text(
            state.productSearchQuery?.isNotEmpty == true
                ? 'No products found'
                : 'No products available',
            style: Theme.of(context).textTheme.titleMedium,
          ),
          const SizedBox(height: 8),
          Text(
            state.productSearchQuery?.isNotEmpty == true
                ? 'Try adjusting your search'
                : 'This category is currently empty',
            style: Theme.of(context).textTheme.bodyMedium,
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }

  void _showProductDetails(BuildContext context, product) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (context) => DraggableScrollableSheet(
        initialChildSize: 0.7,
        maxChildSize: 0.9,
        minChildSize: 0.5,
        builder: (context, scrollController) => Container(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Center(
                child: Container(
                  width: 40,
                  height: 4,
                  decoration: BoxDecoration(
                    color: Colors.grey[300],
                    borderRadius: BorderRadius.circular(2),
                  ),
                ),
              ),
              const SizedBox(height: 16),
              Text(
                product.name,
                style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 8),
              Text(
                '${product.currency} ${product.price.toStringAsFixed(2)}',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  color: Theme.of(context).primaryColor,
                  fontWeight: FontWeight.bold,
                ),
              ),
              if (product.description != null) ...[
                const SizedBox(height: 16),
                Text(
                  product.description!,
                  style: Theme.of(context).textTheme.bodyMedium,
                ),
              ],
              const SizedBox(height: 16),
              ElevatedButton(
                onPressed: () {
                  Navigator.of(context).pop();
                  _addToCart(context, product);
                },
                child: const Text('Add to Cart'),
              ),
            ],
          ),
        ),
      ),
    );
  }

  void _addToCart(BuildContext context, product) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('${product.name} added to cart'),
        action: SnackBarAction(
          label: 'View Cart',
          onPressed: () {
            // TODO: Navigate to cart
          },
        ),
      ),
    );
  }
}