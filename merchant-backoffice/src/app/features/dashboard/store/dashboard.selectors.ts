import { createFeatureSelector, createSelector } from '@ngrx/store';
import { DashboardState } from './dashboard.state';

export const selectDashboardState = createFeatureSelector<DashboardState>('dashboard');

// Data Selectors
export const selectDashboardData = createSelector(
  selectDashboardState,
  (state) => state.dashboardData
);

export const selectDashboardStats = createSelector(
  selectDashboardState,
  (state) => state.stats
);

export const selectRecentOrders = createSelector(
  selectDashboardState,
  (state) => state.recentOrders
);

export const selectRecentActivity = createSelector(
  selectDashboardState,
  (state) => state.recentActivity
);

export const selectNotifications = createSelector(
  selectDashboardState,
  (state) => state.notifications
);

export const selectUnreadNotifications = createSelector(
  selectNotifications,
  (notifications) => notifications.filter(n => !n.read)
);

export const selectUnreadNotificationCount = createSelector(
  selectUnreadNotifications,
  (notifications) => notifications.length
);

// UI State Selectors
export const selectSelectedTimeRange = createSelector(
  selectDashboardState,
  (state) => state.selectedTimeRange
);

// Loading State Selectors
export const selectDashboardLoading = createSelector(
  selectDashboardState,
  (state) => state.loading
);

export const selectStatsLoading = createSelector(
  selectDashboardState,
  (state) => state.statsLoading
);

export const selectOrdersLoading = createSelector(
  selectDashboardState,
  (state) => state.ordersLoading
);

export const selectActivityLoading = createSelector(
  selectDashboardState,
  (state) => state.activityLoading
);

export const selectNotificationsLoading = createSelector(
  selectDashboardState,
  (state) => state.notificationsLoading
);

export const selectAnyLoading = createSelector(
  selectDashboardState,
  (state) => state.loading || state.statsLoading || state.ordersLoading || 
           state.activityLoading || state.notificationsLoading
);

// Error State Selectors
export const selectDashboardError = createSelector(
  selectDashboardState,
  (state) => state.error
);

export const selectStatsError = createSelector(
  selectDashboardState,
  (state) => state.statsError
);

export const selectOrdersError = createSelector(
  selectDashboardState,
  (state) => state.ordersError
);

export const selectActivityError = createSelector(
  selectDashboardState,
  (state) => state.activityError
);

export const selectNotificationsError = createSelector(
  selectDashboardState,
  (state) => state.notificationsError
);

// Real-time Updates
export const selectRealTimeUpdatesActive = createSelector(
  selectDashboardState,
  (state) => state.realTimeUpdatesActive
);

export const selectLastUpdated = createSelector(
  selectDashboardState,
  (state) => state.lastUpdated
);

// Computed Selectors
export const selectPendingOrdersCount = createSelector(
  selectRecentOrders,
  (orders) => orders.filter(order => 
    order.status === 'CREATED' || 
    order.status === 'PAYMENT_PROCESSING' || 
    order.status === 'PAYMENT_CONFIRMED' ||
    order.status === 'PREPARING'
  ).length
);

export const selectTodayRevenue = createSelector(
  selectDashboardStats,
  (stats) => stats?.todayOrders || 0
);

export const selectRecentOrdersByStatus = createSelector(
  selectRecentOrders,
  (orders) => {
    const grouped = orders.reduce((acc, order) => {
      const status = order.status;
      if (!acc[status]) {
        acc[status] = [];
      }
      acc[status].push(order);
      return acc;
    }, {} as Record<string, typeof orders>);
    
    return grouped;
  }
);

export const selectDashboardSummary = createSelector(
  selectDashboardStats,
  selectRecentOrders,
  selectUnreadNotificationCount,
  (stats, orders, unreadCount) => ({
    stats,
    pendingOrdersCount: orders.filter(o => 
      ['CREATED', 'PAYMENT_PROCESSING', 'PAYMENT_CONFIRMED', 'PREPARING'].includes(o.status)
    ).length,
    unreadNotificationCount: unreadCount,
    hasData: !!stats
  })
);