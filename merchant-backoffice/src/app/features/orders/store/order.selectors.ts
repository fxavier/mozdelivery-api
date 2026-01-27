import { createFeatureSelector, createSelector } from '@ngrx/store';
import { OrderState } from './order.state';
import { OrderStatus } from '../models/order.model';

export const selectOrderState = createFeatureSelector<OrderState>('orders');

// Basic selectors
export const selectOrders = createSelector(
  selectOrderState,
  (state) => state.orders
);

export const selectSelectedOrder = createSelector(
  selectOrderState,
  (state) => state.selectedOrder
);

export const selectOrderAnalytics = createSelector(
  selectOrderState,
  (state) => state.analytics
);

export const selectOrdersNeedingAttention = createSelector(
  selectOrderState,
  (state) => state.ordersNeedingAttention
);

// Pagination selectors
export const selectTotalElements = createSelector(
  selectOrderState,
  (state) => state.totalElements
);

export const selectTotalPages = createSelector(
  selectOrderState,
  (state) => state.totalPages
);

export const selectCurrentPage = createSelector(
  selectOrderState,
  (state) => state.currentPage
);

export const selectPageSize = createSelector(
  selectOrderState,
  (state) => state.pageSize
);

export const selectPaginationInfo = createSelector(
  selectTotalElements,
  selectTotalPages,
  selectCurrentPage,
  selectPageSize,
  (totalElements, totalPages, currentPage, pageSize) => ({
    totalElements,
    totalPages,
    currentPage,
    pageSize,
    hasNext: currentPage < totalPages - 1,
    hasPrevious: currentPage > 0
  })
);

// Filter selectors
export const selectOrderFilters = createSelector(
  selectOrderState,
  (state) => state.filters
);

// Loading state selectors
export const selectIsLoading = createSelector(
  selectOrderState,
  (state) => state.isLoading
);

export const selectIsLoadingOrder = createSelector(
  selectOrderState,
  (state) => state.isLoadingOrder
);

export const selectIsLoadingAnalytics = createSelector(
  selectOrderState,
  (state) => state.isLoadingAnalytics
);

export const selectIsUpdatingStatus = createSelector(
  selectOrderState,
  (state) => state.isUpdatingStatus
);

export const selectIsExporting = createSelector(
  selectOrderState,
  (state) => state.isExporting
);

export const selectError = createSelector(
  selectOrderState,
  (state) => state.error
);

// Selection selectors
export const selectSelectedOrderIds = createSelector(
  selectOrderState,
  (state) => state.selectedOrderIds
);

export const selectSelectedOrdersCount = createSelector(
  selectSelectedOrderIds,
  (selectedIds) => selectedIds.length
);

export const selectIsAllOrdersSelected = createSelector(
  selectOrders,
  selectSelectedOrderIds,
  (orders, selectedIds) => orders.length > 0 && orders.every(order => selectedIds.includes(order.id))
);

export const selectIsSomeOrdersSelected = createSelector(
  selectSelectedOrderIds,
  (selectedIds) => selectedIds.length > 0
);

// Auto refresh selector
export const selectAutoRefreshEnabled = createSelector(
  selectOrderState,
  (state) => state.autoRefreshEnabled
);

export const selectLastUpdated = createSelector(
  selectOrderState,
  (state) => state.lastUpdated
);

// Order status history selector
export const selectOrderStatusHistory = createSelector(
  selectOrderState,
  (state) => state.orderStatusHistory
);

export const selectOrderStatusHistoryById = (orderId: string) => createSelector(
  selectOrderStatusHistory,
  (history) => history[orderId] || []
);

// Computed selectors
export const selectOrdersByStatus = createSelector(
  selectOrders,
  (orders) => {
    const ordersByStatus: { [key in OrderStatus]: number } = {
      [OrderStatus.CREATED]: 0,
      [OrderStatus.PAYMENT_PROCESSING]: 0,
      [OrderStatus.PAYMENT_CONFIRMED]: 0,
      [OrderStatus.PAYMENT_FAILED]: 0,
      [OrderStatus.PREPARING]: 0,
      [OrderStatus.READY_FOR_PICKUP]: 0,
      [OrderStatus.PICKED_UP]: 0,
      [OrderStatus.OUT_FOR_DELIVERY]: 0,
      [OrderStatus.DELIVERED]: 0,
      [OrderStatus.CANCELLED]: 0,
      [OrderStatus.DELIVERY_FAILED]: 0
    };

    orders.forEach(order => {
      ordersByStatus[order.status]++;
    });

    return ordersByStatus;
  }
);

export const selectPendingOrdersCount = createSelector(
  selectOrders,
  (orders) => orders.filter(order => 
    [OrderStatus.CREATED, OrderStatus.PAYMENT_CONFIRMED, OrderStatus.PREPARING].includes(order.status)
  ).length
);

export const selectActiveOrdersCount = createSelector(
  selectOrders,
  (orders) => orders.filter(order => 
    [OrderStatus.READY_FOR_PICKUP, OrderStatus.PICKED_UP, OrderStatus.OUT_FOR_DELIVERY].includes(order.status)
  ).length
);

export const selectTodayOrdersCount = createSelector(
  selectOrders,
  (orders) => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    return orders.filter(order => {
      const orderDate = new Date(order.createdAt);
      orderDate.setHours(0, 0, 0, 0);
      return orderDate.getTime() === today.getTime();
    }).length;
  }
);

export const selectTodayRevenue = createSelector(
  selectOrders,
  (orders) => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    return orders
      .filter(order => {
        const orderDate = new Date(order.createdAt);
        orderDate.setHours(0, 0, 0, 0);
        return orderDate.getTime() === today.getTime() && order.status === OrderStatus.DELIVERED;
      })
      .reduce((total, order) => total + order.totalAmount, 0);
  }
);

// Search and filter helpers
export const selectFilteredOrders = createSelector(
  selectOrders,
  selectOrderFilters,
  (orders, filters) => {
    let filtered = [...orders];

    if (filters.status && filters.status.length > 0) {
      filtered = filtered.filter(order => filters.status!.includes(order.status));
    }

    if (filters.searchTerm) {
      const searchTerm = filters.searchTerm.toLowerCase();
      filtered = filtered.filter(order => 
        order.id.toLowerCase().includes(searchTerm) ||
        (order.guestInfo?.contactName?.toLowerCase().includes(searchTerm)) ||
        order.deliveryAddress.street.toLowerCase().includes(searchTerm)
      );
    }

    if (filters.dateRange) {
      filtered = filtered.filter(order => {
        const orderDate = new Date(order.createdAt);
        return orderDate >= filters.dateRange!.startDate && orderDate <= filters.dateRange!.endDate;
      });
    }

    if (filters.paymentMethod && filters.paymentMethod.length > 0) {
      filtered = filtered.filter(order => 
        filters.paymentMethod!.includes(order.paymentInfo.method)
      );
    }

    if (filters.minAmount !== undefined) {
      filtered = filtered.filter(order => order.totalAmount >= filters.minAmount!);
    }

    if (filters.maxAmount !== undefined) {
      filtered = filtered.filter(order => order.totalAmount <= filters.maxAmount!);
    }

    return filtered;
  }
);