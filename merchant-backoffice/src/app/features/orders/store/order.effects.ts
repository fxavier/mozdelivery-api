import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { of, timer } from 'rxjs';
import { 
  map, 
  catchError, 
  switchMap, 
  withLatestFrom,
  filter,
  tap
} from 'rxjs/operators';
import { OrderService } from '../services/order.service';
import { OrderActions } from './order.actions';
import { selectOrderFilters, selectCurrentPage, selectPageSize, selectAutoRefreshEnabled } from './order.selectors';

@Injectable()
export class OrderEffects {
  constructor(
    private actions$: Actions,
    private orderService: OrderService,
    private store: Store
  ) {}

  // Load orders
  loadOrders$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OrderActions.loadOrders),
      switchMap(({ page, size, filters }) =>
        this.orderService.getOrders(page, size, filters).pipe(
          map(response => OrderActions.loadOrdersSuccess({ response })),
          catchError(error => of(OrderActions.loadOrdersFailure({ 
            error: error.message || 'Failed to load orders' 
          })))
        )
      )
    )
  );

  // Load single order
  loadOrder$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OrderActions.loadOrder),
      switchMap(({ orderId }) =>
        this.orderService.getOrder(orderId).pipe(
          map(order => OrderActions.loadOrderSuccess({ order })),
          catchError(error => of(OrderActions.loadOrderFailure({ 
            error: error.message || 'Failed to load order' 
          })))
        )
      )
    )
  );

  // Update order status
  updateOrderStatus$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OrderActions.updateOrderStatus),
      switchMap(({ orderId, request }) =>
        this.orderService.updateOrderStatus(orderId, request).pipe(
          map(response => OrderActions.updateOrderStatusSuccess({ response })),
          catchError(error => of(OrderActions.updateOrderStatusFailure({ 
            error: error.message || 'Failed to update order status' 
          })))
        )
      )
    )
  );

  // Cancel order
  cancelOrder$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OrderActions.cancelOrder),
      switchMap(({ orderId, reason }) =>
        this.orderService.cancelOrder(orderId, reason).pipe(
          map(response => OrderActions.cancelOrderSuccess({ response })),
          catchError(error => of(OrderActions.cancelOrderFailure({ 
            error: error.message || 'Failed to cancel order' 
          })))
        )
      )
    )
  );

  // Load analytics
  loadOrderAnalytics$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OrderActions.loadOrderAnalytics),
      switchMap(({ startDate, endDate }) =>
        this.orderService.getOrderAnalytics(startDate, endDate).pipe(
          map(analytics => OrderActions.loadOrderAnalyticsSuccess({ analytics })),
          catchError(error => of(OrderActions.loadOrderAnalyticsFailure({ 
            error: error.message || 'Failed to load analytics' 
          })))
        )
      )
    )
  );

  // Export orders
  exportOrders$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OrderActions.exportOrders),
      switchMap(({ filters }) =>
        this.orderService.exportOrders(filters).pipe(
          tap(blob => {
            // Create download link
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = `orders-${new Date().toISOString().split('T')[0]}.csv`;
            link.click();
            window.URL.revokeObjectURL(url);
          }),
          map(blob => OrderActions.exportOrdersSuccess({ blob })),
          catchError(error => of(OrderActions.exportOrdersFailure({ 
            error: error.message || 'Failed to export orders' 
          })))
        )
      )
    )
  );

  // Load orders needing attention
  loadOrdersNeedingAttention$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OrderActions.loadOrdersNeedingAttention),
      switchMap(() =>
        this.orderService.getOrdersNeedingAttention().pipe(
          map(orders => OrderActions.loadOrdersNeedingAttentionSuccess({ orders })),
          catchError(error => of(OrderActions.loadOrdersNeedingAttentionFailure({ 
            error: error.message || 'Failed to load orders needing attention' 
          })))
        )
      )
    )
  );

  // Bulk update order status
  bulkUpdateOrderStatus$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OrderActions.bulkUpdateOrderStatus),
      switchMap(({ orderIds, status, notes }) =>
        this.orderService.bulkUpdateOrderStatus(orderIds, status, notes).pipe(
          map(result => OrderActions.bulkUpdateOrderStatusSuccess({ result })),
          catchError(error => of(OrderActions.bulkUpdateOrderStatusFailure({ 
            error: error.message || 'Failed to bulk update orders' 
          })))
        )
      )
    )
  );

  // Load order status history
  loadOrderStatusHistory$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OrderActions.loadOrderStatusHistory),
      switchMap(({ orderId }) =>
        this.orderService.getOrderStatusHistory(orderId).pipe(
          map(history => OrderActions.loadOrderStatusHistorySuccess({ orderId, history })),
          catchError(error => of(OrderActions.loadOrderStatusHistoryFailure({ 
            error: error.message || 'Failed to load order status history' 
          })))
        )
      )
    )
  );

  // Add order notes
  addOrderNotes$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OrderActions.addOrderNotes),
      switchMap(({ orderId, notes }) =>
        this.orderService.addOrderNotes(orderId, notes).pipe(
          map(response => OrderActions.addOrderNotesSuccess({ response })),
          catchError(error => of(OrderActions.addOrderNotesFailure({ 
            error: error.message || 'Failed to add order notes' 
          })))
        )
      )
    )
  );

  // Refresh orders when filters change
  refreshOrdersOnFilterChange$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OrderActions.setFilters),
      withLatestFrom(
        this.store.select(selectCurrentPage),
        this.store.select(selectPageSize)
      ),
      map(([action, page, size]) => 
        OrderActions.loadOrders({ page, size, filters: action.filters })
      )
    )
  );

  // Refresh orders when page changes
  refreshOrdersOnPageChange$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OrderActions.setCurrentPage, OrderActions.setPageSize),
      withLatestFrom(
        this.store.select(selectOrderFilters),
        this.store.select(selectCurrentPage),
        this.store.select(selectPageSize)
      ),
      map(([action, filters, page, size]) => 
        OrderActions.loadOrders({ page, size, filters })
      )
    )
  );

  // Manual refresh
  refreshOrders$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OrderActions.refreshOrders),
      withLatestFrom(
        this.store.select(selectOrderFilters),
        this.store.select(selectCurrentPage),
        this.store.select(selectPageSize)
      ),
      map(([action, filters, page, size]) => 
        OrderActions.loadOrders({ page, size, filters })
      )
    )
  );

  // Auto refresh orders
  autoRefreshOrders$ = createEffect(() =>
    timer(0, 30000).pipe( // Refresh every 30 seconds
      withLatestFrom(this.store.select(selectAutoRefreshEnabled)),
      filter(([, enabled]) => enabled),
      withLatestFrom(
        this.store.select(selectOrderFilters),
        this.store.select(selectCurrentPage),
        this.store.select(selectPageSize)
      ),
      map(([, filters, page, size]) => 
        OrderActions.loadOrders({ page, size, filters })
      )
    )
  );

  // Reload orders after successful status update
  reloadAfterStatusUpdate$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        OrderActions.updateOrderStatusSuccess,
        OrderActions.cancelOrderSuccess,
        OrderActions.bulkUpdateOrderStatusSuccess
      ),
      withLatestFrom(
        this.store.select(selectOrderFilters),
        this.store.select(selectCurrentPage),
        this.store.select(selectPageSize)
      ),
      map(([action, filters, page, size]) => 
        OrderActions.loadOrders({ page, size, filters })
      )
    )
  );
}