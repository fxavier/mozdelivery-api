import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { of, EMPTY, timer } from 'rxjs';
import { map, exhaustMap, catchError, switchMap, takeUntil, tap } from 'rxjs/operators';

import { DashboardService } from '../services/dashboard.service';
import * as DashboardActions from './dashboard.actions';
import { AppState } from '../../../core/store/app.state';

@Injectable()
export class DashboardEffects {
  private readonly actions$ = inject(Actions);
  private readonly dashboardService = inject(DashboardService);
  private readonly store = inject(Store<AppState>);

  // Load Dashboard Data
  loadDashboardData$ = createEffect(() =>
    this.actions$.pipe(
      ofType(DashboardActions.loadDashboardData),
      exhaustMap(() =>
        this.dashboardService.getDashboardData().pipe(
          map(data => DashboardActions.loadDashboardDataSuccess({ data })),
          catchError(error => of(DashboardActions.loadDashboardDataFailure({ 
            error: error.message || 'Failed to load dashboard data' 
          })))
        )
      )
    )
  );

  // Load Dashboard Stats
  loadDashboardStats$ = createEffect(() =>
    this.actions$.pipe(
      ofType(DashboardActions.loadDashboardStats),
      exhaustMap(() =>
        this.dashboardService.getDashboardStats().pipe(
          map(stats => DashboardActions.loadDashboardStatsSuccess({ stats })),
          catchError(error => of(DashboardActions.loadDashboardStatsFailure({ 
            error: error.message || 'Failed to load dashboard stats' 
          })))
        )
      )
    )
  );

  // Load Recent Orders
  loadRecentOrders$ = createEffect(() =>
    this.actions$.pipe(
      ofType(DashboardActions.loadRecentOrders),
      exhaustMap(() =>
        this.dashboardService.getRecentOrders().pipe(
          map(orders => DashboardActions.loadRecentOrdersSuccess({ orders })),
          catchError(error => of(DashboardActions.loadRecentOrdersFailure({ 
            error: error.message || 'Failed to load recent orders' 
          })))
        )
      )
    )
  );

  // Load Recent Activity
  loadRecentActivity$ = createEffect(() =>
    this.actions$.pipe(
      ofType(DashboardActions.loadRecentActivity),
      exhaustMap(() =>
        this.dashboardService.getRecentActivity().pipe(
          map(activities => DashboardActions.loadRecentActivitySuccess({ activities })),
          catchError(error => of(DashboardActions.loadRecentActivityFailure({ 
            error: error.message || 'Failed to load recent activity' 
          })))
        )
      )
    )
  );

  // Load Notifications
  loadNotifications$ = createEffect(() =>
    this.actions$.pipe(
      ofType(DashboardActions.loadNotifications),
      exhaustMap(() =>
        this.dashboardService.getNotifications().pipe(
          map(notifications => DashboardActions.loadNotificationsSuccess({ notifications })),
          catchError(error => of(DashboardActions.loadNotificationsFailure({ 
            error: error.message || 'Failed to load notifications' 
          })))
        )
      )
    )
  );

  // Mark Notification as Read
  markNotificationAsRead$ = createEffect(() =>
    this.actions$.pipe(
      ofType(DashboardActions.markNotificationAsRead),
      exhaustMap(({ notificationId }) =>
        this.dashboardService.markNotificationAsRead(notificationId).pipe(
          map(() => DashboardActions.markNotificationAsReadSuccess({ notificationId })),
          catchError(error => {
            console.error('Failed to mark notification as read:', error);
            return EMPTY;
          })
        )
      )
    )
  );

  // Update Order Status
  updateOrderStatus$ = createEffect(() =>
    this.actions$.pipe(
      ofType(DashboardActions.updateOrderStatus),
      exhaustMap(({ orderId, status }) =>
        this.dashboardService.updateOrderStatus(orderId, status).pipe(
          map(() => DashboardActions.updateOrderStatusSuccess({ orderId, status })),
          catchError(error => of(DashboardActions.updateOrderStatusFailure({ 
            error: error.message || 'Failed to update order status' 
          })))
        )
      )
    )
  );

  // Start Real-time Updates
  startRealTimeUpdates$ = createEffect(() =>
    this.actions$.pipe(
      ofType(DashboardActions.startRealTimeUpdates),
      switchMap(() =>
        timer(0, 30000).pipe( // Poll every 30 seconds
          switchMap(() =>
            this.dashboardService.getDashboardData().pipe(
              map(data => DashboardActions.realTimeUpdate({ data })),
              catchError(error => {
                console.error('Real-time update failed:', error);
                return EMPTY;
              })
            )
          ),
          takeUntil(this.actions$.pipe(ofType(DashboardActions.stopRealTimeUpdates)))
        )
      )
    )
  );

  // Refresh Dashboard
  refreshDashboard$ = createEffect(() =>
    this.actions$.pipe(
      ofType(DashboardActions.refreshDashboard),
      map(() => DashboardActions.loadDashboardData())
    )
  );

  // Auto-load dashboard data on successful stats load
  autoLoadRelatedData$ = createEffect(() =>
    this.actions$.pipe(
      ofType(DashboardActions.loadDashboardDataSuccess),
      tap(() => {
        // Start real-time updates after initial load
        this.store.dispatch(DashboardActions.startRealTimeUpdates());
      })
    ), { dispatch: false }
  );
}