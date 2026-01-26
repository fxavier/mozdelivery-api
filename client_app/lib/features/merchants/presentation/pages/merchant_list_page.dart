import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';

import '../bloc/merchant_browsing_bloc.dart';
import '../bloc/merchant_browsing_event.dart';
import '../bloc/merchant_browsing_state.dart';
import '../widgets/merchant_card.dart';
import '../widgets/search_filter_bar.dart';
import '../../../../core/di/injection.dart';

class MerchantListPage extends StatelessWidget {
  const MerchantListPage({super.key});

  @override
  Widget build(BuildContext context) {
    // Get initial filters from query parameters
    final uri = GoRouterState.of(context).uri;
    final initialCity = uri.queryParameters['city'];
    final initialVertical = uri.queryParameters['vertical'];

    return BlocProvider(
      create: (context) => getIt<MerchantBrowsingBloc>()
        ..add(LoadMerchants(city: initialCity, vertical: initialVertical)),
      child: const MerchantListView(),
    );
  }
}

class MerchantListView extends StatelessWidget {
  const MerchantListView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Browse Merchants'),
        elevation: 0,
      ),
      body: BlocBuilder<MerchantBrowsingBloc, MerchantBrowsingState>(
        builder: (context, state) {
          return Column(
            children: [
              // Search and Filter Bar
              SearchFilterBar(
                initialQuery: state is MerchantBrowsingLoaded ? state.searchQuery : null,
                selectedCity: state is MerchantBrowsingLoaded ? state.currentCity : null,
                selectedVertical: state is MerchantBrowsingLoaded ? state.currentVertical : null,
                onSearchChanged: (query) {
                  if (query.isEmpty) {
                    context.read<MerchantBrowsingBloc>().add(const ClearSearch());
                  } else {
                    context.read<MerchantBrowsingBloc>().add(SearchMerchants(query: query));
                  }
                },
                onCityChanged: (city) {
                  final currentState = context.read<MerchantBrowsingBloc>().state;
                  final vertical = currentState is MerchantBrowsingLoaded 
                      ? currentState.currentVertical 
                      : null;
                  context.read<MerchantBrowsingBloc>().add(
                    FilterMerchants(city: city, vertical: vertical),
                  );
                },
                onVerticalChanged: (vertical) {
                  final currentState = context.read<MerchantBrowsingBloc>().state;
                  final city = currentState is MerchantBrowsingLoaded 
                      ? currentState.currentCity 
                      : null;
                  context.read<MerchantBrowsingBloc>().add(
                    FilterMerchants(city: city, vertical: vertical),
                  );
                },
                onClearFilters: () {
                  context.read<MerchantBrowsingBloc>().add(
                    const FilterMerchants(city: null, vertical: null),
                  );
                },
              ),

              // Content
              Expanded(
                child: _buildContent(context, state),
              ),
            ],
          );
        },
      ),
    );
  }

  Widget _buildContent(BuildContext context, MerchantBrowsingState state) {
    if (state is MerchantBrowsingLoading) {
      return const Center(
        child: CircularProgressIndicator(),
      );
    }

    if (state is MerchantBrowsingError) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.error_outline,
              size: 64,
              color: Colors.red[300],
            ),
            const SizedBox(height: 16),
            Text(
              'Error Loading Merchants',
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
              onPressed: () {
                context.read<MerchantBrowsingBloc>().add(const RefreshMerchants());
              },
              child: const Text('Retry'),
            ),
          ],
        ),
      );
    }

    if (state is MerchantBrowsingLoaded) {
      if (state.filteredMerchants.isEmpty) {
        return Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                Icons.store_outlined,
                size: 64,
                color: Colors.grey[400],
              ),
              const SizedBox(height: 16),
              Text(
                state.searchQuery?.isNotEmpty == true
                    ? 'No merchants found'
                    : 'No merchants available',
                style: Theme.of(context).textTheme.headlineSmall,
              ),
              const SizedBox(height: 8),
              Text(
                state.searchQuery?.isNotEmpty == true
                    ? 'Try adjusting your search or filters'
                    : 'Check back later for new merchants',
                style: Theme.of(context).textTheme.bodyMedium,
                textAlign: TextAlign.center,
              ),
              if (state.searchQuery?.isNotEmpty == true ||
                  state.currentCity != null ||
                  state.currentVertical != null) ...[
                const SizedBox(height: 16),
                ElevatedButton(
                  onPressed: () {
                    context.read<MerchantBrowsingBloc>().add(const ClearSearch());
                    context.read<MerchantBrowsingBloc>().add(
                      const FilterMerchants(city: null, vertical: null),
                    );
                  },
                  child: const Text('Clear Filters'),
                ),
              ],
            ],
          ),
        );
      }

      return RefreshIndicator(
        onRefresh: () async {
          context.read<MerchantBrowsingBloc>().add(const RefreshMerchants());
        },
        child: Column(
          children: [
            // Results Summary
            if (state.searchQuery?.isNotEmpty == true || 
                state.currentCity != null || 
                state.currentVertical != null)
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(16),
                color: Colors.grey[50],
                child: Text(
                  _buildResultsSummary(state),
                  style: Theme.of(context).textTheme.bodyMedium,
                ),
              ),

            // Merchant List
            Expanded(
              child: ListView.builder(
                itemCount: state.filteredMerchants.length,
                itemBuilder: (context, index) {
                  final merchant = state.filteredMerchants[index];
                  return MerchantCard(merchant: merchant);
                },
              ),
            ),
          ],
        ),
      );
    }

    // Initial state
    return const Center(
      child: CircularProgressIndicator(),
    );
  }

  String _buildResultsSummary(MerchantBrowsingLoaded state) {
    final count = state.filteredMerchants.length;
    final filters = <String>[];
    
    if (state.searchQuery?.isNotEmpty == true) {
      filters.add('search: "${state.searchQuery}"');
    }
    if (state.currentCity != null) {
      filters.add('city: ${state.currentCity}');
    }
    if (state.currentVertical != null) {
      filters.add('category: ${state.currentVertical}');
    }
    
    final filterText = filters.isNotEmpty ? ' (${filters.join(', ')})' : '';
    return '$count merchant${count == 1 ? '' : 's'} found$filterText';
  }
}