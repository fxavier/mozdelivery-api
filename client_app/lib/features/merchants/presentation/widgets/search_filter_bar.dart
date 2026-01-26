import 'package:flutter/material.dart';

import '../../data/models/merchant_model.dart';

class SearchFilterBar extends StatefulWidget {
  final String? initialQuery;
  final String? selectedCity;
  final String? selectedVertical;
  final Function(String) onSearchChanged;
  final Function(String?) onCityChanged;
  final Function(String?) onVerticalChanged;
  final VoidCallback? onClearFilters;

  const SearchFilterBar({
    super.key,
    this.initialQuery,
    this.selectedCity,
    this.selectedVertical,
    required this.onSearchChanged,
    required this.onCityChanged,
    required this.onVerticalChanged,
    this.onClearFilters,
  });

  @override
  State<SearchFilterBar> createState() => _SearchFilterBarState();
}

class _SearchFilterBarState extends State<SearchFilterBar> {
  late TextEditingController _searchController;
  bool _showFilters = false;

  @override
  void initState() {
    super.initState();
    _searchController = TextEditingController(text: widget.initialQuery);
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        // Search Bar
        Container(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              Expanded(
                child: TextField(
                  controller: _searchController,
                  decoration: InputDecoration(
                    hintText: 'Search merchants...',
                    prefixIcon: const Icon(Icons.search),
                    suffixIcon: _searchController.text.isNotEmpty
                        ? IconButton(
                            icon: const Icon(Icons.clear),
                            onPressed: () {
                              _searchController.clear();
                              widget.onSearchChanged('');
                            },
                          )
                        : null,
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                    filled: true,
                    fillColor: Colors.grey[50],
                  ),
                  onChanged: widget.onSearchChanged,
                ),
              ),
              const SizedBox(width: 8),
              IconButton(
                icon: Icon(
                  _showFilters ? Icons.filter_list : Icons.filter_list_outlined,
                  color: _showFilters ? Theme.of(context).primaryColor : null,
                ),
                onPressed: () {
                  setState(() {
                    _showFilters = !_showFilters;
                  });
                },
              ),
            ],
          ),
        ),

        // Filters
        if (_showFilters)
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            decoration: BoxDecoration(
              color: Colors.grey[50],
              border: Border(
                top: BorderSide(color: Colors.grey[200]!),
              ),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Text(
                      'Filters',
                      style: Theme.of(context).textTheme.titleSmall?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const Spacer(),
                    if (widget.selectedCity != null || widget.selectedVertical != null)
                      TextButton(
                        onPressed: widget.onClearFilters,
                        child: const Text('Clear All'),
                      ),
                  ],
                ),
                
                const SizedBox(height: 8),
                
                // City Filter
                Row(
                  children: [
                    Expanded(
                      child: DropdownButtonFormField<String>(
                        value: widget.selectedCity,
                        decoration: InputDecoration(
                          labelText: 'City',
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(8),
                          ),
                          contentPadding: const EdgeInsets.symmetric(
                            horizontal: 12,
                            vertical: 8,
                          ),
                        ),
                        items: _getCityOptions(),
                        onChanged: widget.onCityChanged,
                      ),
                    ),
                    const SizedBox(width: 16),
                    Expanded(
                      child: DropdownButtonFormField<String>(
                        value: widget.selectedVertical,
                        decoration: InputDecoration(
                          labelText: 'Category',
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(8),
                          ),
                          contentPadding: const EdgeInsets.symmetric(
                            horizontal: 12,
                            vertical: 8,
                          ),
                        ),
                        items: _getVerticalOptions(),
                        onChanged: widget.onVerticalChanged,
                      ),
                    ),
                  ],
                ),
                
                const SizedBox(height: 8),
              ],
            ),
          ),
      ],
    );
  }

  List<DropdownMenuItem<String>> _getCityOptions() {
    // In a real app, this would come from an API or configuration
    final cities = ['Maputo', 'Beira', 'Nampula', 'Matola', 'Tete'];
    
    return [
      const DropdownMenuItem<String>(
        value: null,
        child: Text('All Cities'),
      ),
      ...cities.map((city) => DropdownMenuItem<String>(
        value: city,
        child: Text(city),
      )),
    ];
  }

  List<DropdownMenuItem<String>> _getVerticalOptions() {
    return [
      const DropdownMenuItem<String>(
        value: null,
        child: Text('All Categories'),
      ),
      ...MerchantVertical.values.map((vertical) => DropdownMenuItem<String>(
        value: vertical.name,
        child: Text(vertical.displayName),
      )),
    ];
  }
}