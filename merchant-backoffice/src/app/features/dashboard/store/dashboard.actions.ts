import { createAction, props } from '@ngrx/store';
import { DashboardData, DashboardStats, OrderSummary, RecentActivity, DashboardNotification } from '../models/dashboard.model';

// Dashboard Data Actions
export const loadDashboardData = createAction('[Dashboard] Load Dashboard Data');

export const loadDashboardDataSuccess = createAction(
  '[Dashboard] Load Dashboard Data Success',
  props<{ data: DashboardData }>()
);

export const loadDashboardDataFailure = createAction(
  '[Dashboard] Load Dashboard Data Failure',
  props<{ error: string }>()
);

// Stats Actions
export const loadDashboardStats = createAction('[Dashboard] Load Dashboard Stats');

export const loadDashboardStatsSuccess = createAction(
  '[Dashboard] Load Dashboard Stats Success',
  props<{ stats: DashboardStats }>()
);

export const loadDashboardStatsFailure = createAction(
  '[Dashboard] Load Dashboard Stats Failure',
  props<{ error: string }>()
);

// Recent Orders Actions
export const loadRecentOrders = createAction('[Dashboard] Load Recent Orders');

export const loadRecentOrdersSuccess = createAction(
  '[Dashboard] Load Recent Orders Success',
  props<{ orders: OrderSummary[] }>()
);

export const loadRecentOrdersFailure = createAction(
  '[Dashboard] Load Recent Orders Failure',
  props<{ error: string }>()
);

// Recent Activity Actions
export const loadRecentActivity = createAction('[Dashboard] Load Recent Activity');

export const loadRecentActivitySuccess = createAction(
  '[Dashboard] Load Recent Activity Success',
  props<{ activities: RecentActivity[] }>()
);

export const loadRecentActivityFailure = createAction(
  '[Dashboard] Load Recent Activity Failure',
  props<{ error: string }>()
);

// Notifications Actions
export const loadNotifications = createAction('[Dashboard] Load Notifications');

export const loadNotificationsSuccess = createAction(
  '[Dashboard] Load Notifications Success',
  props<{ notifications: DashboardNotification[] }>()
);

export const loadNotificationsFailure = createAction(
  '[Dashboard] Load Notifications Failure',
  props<{ error: string }>()
);

export const markNotificationAsRead = createAction(
  '[Dashboard] Mark Notification As Read',
  props<{ notificationId: string }>()
);

export const markNotificationAsReadSuccess = createAction(
  '[Dashboard] Mark Notification As Read Success',
  props<{ notificationId: string }>()
);

// Order Status Actions
export const updateOrderStatus = createAction(
  '[Dashboard] Update Order Status',
  props<{ orderId: string; status: string }>()
);

export const updateOrderStatusSuccess = createAction(
  '[Dashboard] Update Order Status Success',
  props<{ orderId: string; status: string }>()
);

export const updateOrderStatusFailure = createAction(
  '[Dashboard] Update Order Status Failure',
  props<{ error: string }>()
);

// Real-time Updates
export const startRealTimeUpdates = createAction('[Dashboard] Start Real Time Updates');

export const stopRealTimeUpdates = createAction('[Dashboard] Stop Real Time Updates');

export const realTimeUpdate = createAction(
  '[Dashboard] Real Time Update',
  props<{ data: DashboardData }>()
);

// UI Actions
export const setSelectedTimeRange = createAction(
  '[Dashboard] Set Selected Time Range',
  props<{ timeRange: 'today' | 'week' | 'month' | 'year' }>()
);

export const refreshDashboard = createAction('[Dashboard] Refresh Dashboard');