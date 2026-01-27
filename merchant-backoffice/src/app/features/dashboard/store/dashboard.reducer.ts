import { createReducer, on } from '@ngrx/store';
import { DashboardState, initialDashboardState } from './dashboard.state';
import * as DashboardActions from './dashboard.actions';

export const dashboardReducer = createReducer(
  initialDashboardState,

  // Dashboard Data
  on(DashboardActions.loadDashboardData, (state) => ({
    ...state,
    loading: true,
    error: null
  })),

  on(DashboardActions.loadDashboardDataSuccess, (state, { data }) => ({
    ...state,
    loading: false,
    dashboardData: data,
    stats: data.stats,
    recentOrders: data.recentOrders,
    recentActivity: data.recentActivity,
    notifications: data.notifications,
    lastUpdated: data.lastUpdated,
    error: null
  })),

  on(DashboardActions.loadDashboardDataFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),

  // Stats
  on(DashboardActions.loadDashboardStats, (state) => ({
    ...state,
    statsLoading: true,
    statsError: null
  })),

  on(DashboardActions.loadDashboardStatsSuccess, (state, { stats }) => ({
    ...state,
    statsLoading: false,
    stats,
    statsError: null
  })),

  on(DashboardActions.loadDashboardStatsFailure, (state, { error }) => ({
    ...state,
    statsLoading: false,
    statsError: error
  })),

  // Recent Orders
  on(DashboardActions.loadRecentOrders, (state) => ({
    ...state,
    ordersLoading: true,
    ordersError: null
  })),

  on(DashboardActions.loadRecentOrdersSuccess, (state, { orders }) => ({
    ...state,
    ordersLoading: false,
    recentOrders: orders,
    ordersError: null
  })),

  on(DashboardActions.loadRecentOrdersFailure, (state, { error }) => ({
    ...state,
    ordersLoading: false,
    ordersError: error
  })),

  // Recent Activity
  on(DashboardActions.loadRecentActivity, (state) => ({
    ...state,
    activityLoading: true,
    activityError: null
  })),

  on(DashboardActions.loadRecentActivitySuccess, (state, { activities }) => ({
    ...state,
    activityLoading: false,
    recentActivity: activities,
    activityError: null
  })),

  on(DashboardActions.loadRecentActivityFailure, (state, { error }) => ({
    ...state,
    activityLoading: false,
    activityError: error
  })),

  // Notifications
  on(DashboardActions.loadNotifications, (state) => ({
    ...state,
    notificationsLoading: true,
    notificationsError: null
  })),

  on(DashboardActions.loadNotificationsSuccess, (state, { notifications }) => ({
    ...state,
    notificationsLoading: false,
    notifications,
    notificationsError: null
  })),

  on(DashboardActions.loadNotificationsFailure, (state, { error }) => ({
    ...state,
    notificationsLoading: false,
    notificationsError: error
  })),

  on(DashboardActions.markNotificationAsReadSuccess, (state, { notificationId }) => ({
    ...state,
    notifications: state.notifications.map(notification =>
      notification.id === notificationId
        ? { ...notification, read: true }
        : notification
    )
  })),

  // Order Status Updates
  on(DashboardActions.updateOrderStatusSuccess, (state, { orderId, status }) => ({
    ...state,
    recentOrders: state.recentOrders.map(order =>
      order.id === orderId
        ? { ...order, status: status as any }
        : order
    )
  })),

  // Real-time Updates
  on(DashboardActions.startRealTimeUpdates, (state) => ({
    ...state,
    realTimeUpdatesActive: true
  })),

  on(DashboardActions.stopRealTimeUpdates, (state) => ({
    ...state,
    realTimeUpdatesActive: false
  })),

  on(DashboardActions.realTimeUpdate, (state, { data }) => ({
    ...state,
    dashboardData: data,
    stats: data.stats,
    recentOrders: data.recentOrders,
    recentActivity: data.recentActivity,
    notifications: data.notifications,
    lastUpdated: data.lastUpdated
  })),

  // UI Actions
  on(DashboardActions.setSelectedTimeRange, (state, { timeRange }) => ({
    ...state,
    selectedTimeRange: timeRange
  })),

  on(DashboardActions.refreshDashboard, (state) => ({
    ...state,
    loading: true,
    error: null
  }))
);