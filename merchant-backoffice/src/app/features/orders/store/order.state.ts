import { Order, OrderFilters, OrderAnalytics } from '../models/order.model';

export interface OrderState {
  // Data
  orders: Order[];
  selectedOrder: Order | null;
  orderStatusHistory: { [orderId: string]: any[] };
  analytics: OrderAnalytics | null;
  ordersNeedingAttention: Order[];
  
  // Pagination
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
  
  // Filters and search
  filters: OrderFilters;
  
  // UI state
  isLoading: boolean;
  isLoadingOrder: boolean;
  isLoadingAnalytics: boolean;
  isUpdatingStatus: boolean;
  isExporting: boolean;
  error: string | null;
  
  // Selection
  selectedOrderIds: string[];
  
  // Real-time updates
  autoRefreshEnabled: boolean;
  lastUpdated: Date | null;
}

export const initialOrderState: OrderState = {
  // Data
  orders: [],
  selectedOrder: null,
  orderStatusHistory: {},
  analytics: null,
  ordersNeedingAttention: [],
  
  // Pagination
  totalElements: 0,
  totalPages: 0,
  currentPage: 0,
  pageSize: 20,
  
  // Filters and search
  filters: {},
  
  // UI state
  isLoading: false,
  isLoadingOrder: false,
  isLoadingAnalytics: false,
  isUpdatingStatus: false,
  isExporting: false,
  error: null,
  
  // Selection
  selectedOrderIds: [],
  
  // Real-time updates
  autoRefreshEnabled: false,
  lastUpdated: null
};