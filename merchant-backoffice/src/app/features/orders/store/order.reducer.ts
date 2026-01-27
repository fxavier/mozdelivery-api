import { createReducer, on } from '@ngrx/store';
import { OrderActions } from './order.actions';
import { OrderState, initialOrderState } from './order.state';

export const orderReducer = createReducer(
  initialOrderState,

  // Load orders
  on(OrderActions.loadOrders, (state, { page, size }) => ({
    ...state,
    isLoading: true,
    error: null,
    currentPage: page ?? state.currentPage,
    pageSize: size ?? state.pageSize
  })),

  on(OrderActions.loadOrdersSuccess, (state, { response }) => ({
    ...state,
    orders: response.content,
    totalElements: response.totalElements,
    totalPages: response.totalPages,
    currentPage: response.currentPage,
    pageSize: response.size,
    isLoading: false,
    error: null,
    lastUpdated: new Date()
  })),

  on(OrderActions.loadOrdersFailure, (state, { error }) => ({
    ...state,
    isLoading: false,
    error
  })),

  // Load single order
  on(OrderActions.loadOrder, (state) => ({
    ...state,
    isLoadingOrder: true,
    error: null
  })),

  on(OrderActions.loadOrderSuccess, (state, { order }) => ({
    ...state,
    selectedOrder: order,
    isLoadingOrder: false,
    error: null,
    // Update order in list if it exists
    orders: state.orders.map(o => o.id === order.id ? order : o)
  })),

  on(OrderActions.loadOrderFailure, (state, { error }) => ({
    ...state,
    isLoadingOrder: false,
    error
  })),

  // Update order status
  on(OrderActions.updateOrderStatus, (state) => ({
    ...state,
    isUpdatingStatus: true,
    error: null
  })),

  on(OrderActions.updateOrderStatusSuccess, (state, { response }) => ({
    ...state,
    isUpdatingStatus: false,
    error: null,
    // Update order in list and selected order
    orders: state.orders.map(o => o.id === response.order.id ? response.order : o),
    selectedOrder: state.selectedOrder?.id === response.order.id ? response.order : state.selectedOrder
  })),

  on(OrderActions.updateOrderStatusFailure, (state, { error }) => ({
    ...state,
    isUpdatingStatus: false,
    error
  })),

  // Cancel order
  on(OrderActions.cancelOrder, (state) => ({
    ...state,
    isUpdatingStatus: true,
    error: null
  })),

  on(OrderActions.cancelOrderSuccess, (state, { response }) => ({
    ...state,
    isUpdatingStatus: false,
    error: null,
    orders: state.orders.map(o => o.id === response.order.id ? response.order : o),
    selectedOrder: state.selectedOrder?.id === response.order.id ? response.order : state.selectedOrder
  })),

  on(OrderActions.cancelOrderFailure, (state, { error }) => ({
    ...state,
    isUpdatingStatus: false,
    error
  })),

  // Load analytics
  on(OrderActions.loadOrderAnalytics, (state) => ({
    ...state,
    isLoadingAnalytics: true,
    error: null
  })),

  on(OrderActions.loadOrderAnalyticsSuccess, (state, { analytics }) => ({
    ...state,
    analytics,
    isLoadingAnalytics: false,
    error: null
  })),

  on(OrderActions.loadOrderAnalyticsFailure, (state, { error }) => ({
    ...state,
    isLoadingAnalytics: false,
    error
  })),

  // Export orders
  on(OrderActions.exportOrders, (state) => ({
    ...state,
    isExporting: true,
    error: null
  })),

  on(OrderActions.exportOrdersSuccess, (state) => ({
    ...state,
    isExporting: false,
    error: null
  })),

  on(OrderActions.exportOrdersFailure, (state, { error }) => ({
    ...state,
    isExporting: false,
    error
  })),

  // Load orders needing attention
  on(OrderActions.loadOrdersNeedingAttention, (state) => ({
    ...state,
    error: null
  })),

  on(OrderActions.loadOrdersNeedingAttentionSuccess, (state, { orders }) => ({
    ...state,
    ordersNeedingAttention: orders,
    error: null
  })),

  on(OrderActions.loadOrdersNeedingAttentionFailure, (state, { error }) => ({
    ...state,
    error
  })),

  // Bulk operations
  on(OrderActions.bulkUpdateOrderStatus, (state) => ({
    ...state,
    isUpdatingStatus: true,
    error: null
  })),

  on(OrderActions.bulkUpdateOrderStatusSuccess, (state) => ({
    ...state,
    isUpdatingStatus: false,
    error: null,
    selectedOrderIds: [] // Clear selection after bulk update
  })),

  on(OrderActions.bulkUpdateOrderStatusFailure, (state, { error }) => ({
    ...state,
    isUpdatingStatus: false,
    error
  })),

  // Order status history
  on(OrderActions.loadOrderStatusHistorySuccess, (state, { orderId, history }) => ({
    ...state,
    orderStatusHistory: {
      ...state.orderStatusHistory,
      [orderId]: history
    }
  })),

  // Add notes
  on(OrderActions.addOrderNotesSuccess, (state, { response }) => ({
    ...state,
    orders: state.orders.map(o => o.id === response.order.id ? response.order : o),
    selectedOrder: state.selectedOrder?.id === response.order.id ? response.order : state.selectedOrder
  })),

  // Real-time updates
  on(OrderActions.orderUpdated, (state, { order }) => ({
    ...state,
    orders: state.orders.map(o => o.id === order.id ? order : o),
    selectedOrder: state.selectedOrder?.id === order.id ? order : state.selectedOrder,
    lastUpdated: new Date()
  })),

  // UI state management
  on(OrderActions.setSelectedOrder, (state, { orderId }) => ({
    ...state,
    selectedOrder: orderId ? state.orders.find(o => o.id === orderId) || null : null
  })),

  on(OrderActions.setFilters, (state, { filters }) => ({
    ...state,
    filters,
    currentPage: 0 // Reset to first page when filters change
  })),

  on(OrderActions.clearFilters, (state) => ({
    ...state,
    filters: {},
    currentPage: 0
  })),

  on(OrderActions.setLoading, (state, { loading }) => ({
    ...state,
    isLoading: loading
  })),

  on(OrderActions.clearError, (state) => ({
    ...state,
    error: null
  })),

  on(OrderActions.setCurrentPage, (state, { page }) => ({
    ...state,
    currentPage: page
  })),

  on(OrderActions.setPageSize, (state, { size }) => ({
    ...state,
    pageSize: size,
    currentPage: 0 // Reset to first page when page size changes
  })),

  // Selection management
  on(OrderActions.selectOrders, (state, { orderIds }) => ({
    ...state,
    selectedOrderIds: [...new Set([...state.selectedOrderIds, ...orderIds])]
  })),

  on(OrderActions.deselectOrders, (state, { orderIds }) => ({
    ...state,
    selectedOrderIds: state.selectedOrderIds.filter(id => !orderIds.includes(id))
  })),

  on(OrderActions.selectAllOrders, (state) => ({
    ...state,
    selectedOrderIds: state.orders.map(order => order.id)
  })),

  on(OrderActions.clearSelection, (state) => ({
    ...state,
    selectedOrderIds: []
  })),

  // Auto refresh
  on(OrderActions.autoRefreshToggle, (state, { enabled }) => ({
    ...state,
    autoRefreshEnabled: enabled
  }))
);