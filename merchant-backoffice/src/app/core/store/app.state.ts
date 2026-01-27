import { AuthState } from './auth/auth.state';
import { DashboardState } from '../../features/dashboard/store/dashboard.state';

export interface AppState {
  auth: AuthState;
  dashboard: DashboardState;
}

export const initialAppState: AppState = {
  auth: {
    user: null,
    token: null,
    isLoading: false,
    error: null
  },
  dashboard: {
    dashboardData: null,
    stats: null,
    recentOrders: [],
    recentActivity: [],
    notifications: [],
    selectedTimeRange: 'today',
    loading: false,
    statsLoading: false,
    ordersLoading: false,
    activityLoading: false,
    notificationsLoading: false,
    error: null,
    statsError: null,
    ordersError: null,
    activityError: null,
    notificationsError: null,
    realTimeUpdatesActive: false,
    lastUpdated: null
  }
};