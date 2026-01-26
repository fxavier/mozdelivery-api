import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';

import '../../data/models/catalog_model.dart';

class CategoryChip extends StatelessWidget {
  final CategoryModel category;
  final bool isSelected;
  final VoidCallback onTap;

  const CategoryChip({
    super.key,
    required this.category,
    required this.isSelected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(right: 12),
      child: FilterChip(
        selected: isSelected,
        onSelected: (_) => onTap(),
        avatar: category.imageUrl != null
            ? CircleAvatar(
                backgroundImage: CachedNetworkImageProvider(category.imageUrl!),
                backgroundColor: Colors.transparent,
              )
            : const Icon(Icons.category, size: 18),
        label: Text(category.name),
        selectedColor: Theme.of(context).primaryColor.withValues(alpha: 0.2),
        checkmarkColor: Theme.of(context).primaryColor,
        backgroundColor: Colors.grey[100],
        side: BorderSide(
          color: isSelected 
              ? Theme.of(context).primaryColor 
              : Colors.grey[300]!,
        ),
      ),
    );
  }
}

class CatalogChip extends StatelessWidget {
  final CatalogModel catalog;
  final bool isSelected;
  final VoidCallback onTap;

  const CatalogChip({
    super.key,
    required this.catalog,
    required this.isSelected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(right: 12),
      child: FilterChip(
        selected: isSelected,
        onSelected: (_) => onTap(),
        label: Text(catalog.name),
        selectedColor: Theme.of(context).primaryColor.withValues(alpha: 0.2),
        checkmarkColor: Theme.of(context).primaryColor,
        backgroundColor: Colors.grey[100],
        side: BorderSide(
          color: isSelected 
              ? Theme.of(context).primaryColor 
              : Colors.grey[300]!,
        ),
      ),
    );
  }
}