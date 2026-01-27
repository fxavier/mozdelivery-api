import { createActionGroup, emptyProps, props } from '@ngrx/store';
import {
  Order,
  OrderListResponse,
  OrderFilters,
  UpdateOrderStatusRequest,
  OrderAnalytics,
  OrderResponse
} from '../models/order.model';

export const OrderActions = createActionGroup({
  source: 'Order',
  events: {
    // Load orders
    'Load Orders': props<{ page?: number; size?: number; filters?: OrderFilters }>(),
    'Load Orders Success': props<{ response: OrderListResponse }>(),
    'Load Orders Failure': props<{ error: string }>(),

    // Load single order
    'Load Order': props<{ orderId: string }>(),
    'Load Order Success': props<{ order: Order }>(),
    'Load Order Failure': props<{ error: string }>(),

    // Update order status
    'Update Order Status': props<{ orderId: string; request: UpdateOrderStatusRequest }>(),
    'Update Order Status Success': props<{ response: OrderResponse }>(),
    'Update Order Status Failure': props<{ error: string }>(),

    // Cancel order
    'Cancel Order': props<{ orderId: string; reason: string }>(),
    'Cancel Order Success': props<{ response: OrderResponse }>(),
    'Cancel Order Failure': props<{ error: string }>(),

    // Load analytics
    'Load Order Analytics': props<{ startDate?: Date; endDate?: Date }>(),
    'Load Order Analytics Success': props<{ analytics: OrderAnalytics }>(),
    'Load Order Analytics Failure': props<{ error: string }>(),

    // Export orders
    'Export Orders': props<{ filters?: OrderFilters }>(),
    'Export Orders Success': props<{ blob: Blob }>(),
    'Export Orders Failure': props<{ error: string }>(),

    // Load orders needing attention
    'Load Orders Needing Attention': emptyProps(),
    'Load Orders Needing Attention Success': props<{ orders: Order[] }>(),
    'Load Orders Needing Attention Failure': props<{ error: string }>(),

    // Bulk operations
    'Bulk Update Order Status': props<{ orderIds: string[]; status: string; notes?: string }>(),
    'Bulk Update Order Status Success': props<{ result: { success: number; errors: string[] } }>(),
    'Bulk Update Order Status Failure': props<{ error: string }>(),

    // Order status history
    'Load Order Status History': props<{ orderId: string }>(),
    'Load Order Status History Success': props<{ orderId: string; history: any[] }>(),
    'Load Order Status History Failure': props<{ error: string }>(),

    // Add notes
    'Add Order Notes': props<{ orderId: string; notes: string }>(),
    'Add Order Notes Success': props<{ response: OrderResponse }>(),
    'Add Order Notes Failure': props<{ error: string }>(),

    // Real-time updates
    'Order Updated': props<{ order: Order }>(),
    'Order Status Changed': props<{ orderId: string; oldStatus: string; newStatus: string }>(),

    // UI state management
    'Set Selected Order': props<{ orderId: string | null }>(),
    'Set Filters': props<{ filters: OrderFilters }>(),
    'Clear Filters': emptyProps(),
    'Set Loading': props<{ loading: boolean }>(),
    'Clear Error': emptyProps(),
    'Set Current Page': props<{ page: number }>(),
    'Set Page Size': props<{ size: number }>(),

    // Selection management
    'Select Orders': props<{ orderIds: string[] }>(),
    'Deselect Orders': props<{ orderIds: string[] }>(),
    'Select All Orders': emptyProps(),
    'Clear Selection': emptyProps(),

    // Refresh
    'Refresh Orders': emptyProps(),
    'Auto Refresh Toggle': props<{ enabled: boolean }>()
  }
});