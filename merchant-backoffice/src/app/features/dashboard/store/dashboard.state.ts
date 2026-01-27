import { DashboardData, DashboardStats, OrderSummary, RecentActivity, DashboardNotification } from '../models/dashboard.model';

export interface DashboardState {
  // Data
  dashboardData: DashboardData | null;
  stats: DashboardStats | null;
  recentOrders: OrderSummary[];
  recentActivity: RecentActivity[];
  notifications: DashboardNotification[];
  
  // UI State
  selectedTimeRange: 'today' | 'week' | 'month' | 'year';
  
  // Loading States
  loading: boolean;
  statsLoading: boolean;
  ordersLoading: boolean;
  activityLoading: boolean;
  notificationsLoading: boolean;
  
  // Error States
  error: string | null;
  statsError: string | null;
  ordersError: string | null;
  activityError: string | null;
  notificationsError: string | null;
  
  // Real-time Updates
  realTimeUpdatesActive: boolean;
  lastUpdated: Date | null;
}

export const initialDashboardState: DashboardState = {
  // Data
  dashboardData: null,
  stats: null,
  recentOrders: [],
  recentActivity: [],
  notifications: [],
  
  // UI State
  selectedTimeRange: 'today',
  
  // Loading States
  loading: false,
  statsLoading: false,
  ordersLoading: false,
  activityLoading: false,
  notificationsLoading: false,
  
  // Error States
  error: null,
  statsError: null,
  ordersError: null,
  activityError: null,
  notificationsError: null,
  
  // Real-time Updates
  realTimeUpdatesActive: false,
  lastUpdated: null
};