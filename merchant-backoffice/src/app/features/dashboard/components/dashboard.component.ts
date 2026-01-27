import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { AppState } from '../../../core/store/app.state';
import { selectCurrentUser } from '../../../core/store/auth/auth.selectors';
import { User } from '../../../shared/models/user.model';
import { DashboardStats, DashboardData } from '../models/dashboard.model';
import { 
  selectDashboardStats, 
  selectDashboardLoading, 
  selectUnreadNotificationCount,
  selectLastUpdated,
  selectRealTimeUpdatesActive
} from '../store/dashboard.selectors';
import * as DashboardActions from '../store/dashboard.actions';
import { OrderQueueComponent } from './order-queue/order-queue.component';
import { NotificationsComponent } from './notifications/notifications.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    OrderQueueComponent,
    NotificationsComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit, OnDestroy {
  private readonly store = inject(Store<AppState>);
  private readonly destroy$ = new Subject<void>();
  
  currentUser$: Observable<User | null>;
  dashboardStats$: Observable<DashboardStats | null>;
  loading$: Observable<boolean>;
  unreadNotificationCount$: Observable<number>;
  lastUpdated$: Observable<Date | null>;
  realTimeUpdatesActive$: Observable<boolean>;
  
  constructor() {
    this.currentUser$ = this.store.select(selectCurrentUser);
    this.dashboardStats$ = this.store.select(selectDashboardStats);
    this.loading$ = this.store.select(selectDashboardLoading);
    this.unreadNotificationCount$ = this.store.select(selectUnreadNotificationCount);
    this.lastUpdated$ = this.store.select(selectLastUpdated);
    this.realTimeUpdatesActive$ = this.store.select(selectRealTimeUpdatesActive);
  }
  
  ngOnInit(): void {
    // Load initial dashboard data
    this.store.dispatch(DashboardActions.loadDashboardData());
  }

  ngOnDestroy(): void {
    // Stop real-time updates when component is destroyed
    this.store.dispatch(DashboardActions.stopRealTimeUpdates());
    this.destroy$.next();
    this.destroy$.complete();
  }

  onRefreshDashboard(): void {
    this.store.dispatch(DashboardActions.refreshDashboard());
  }

  formatCurrency(amount: number | undefined, currency: string = 'MZN'): string {
    if (amount === undefined) return '0.00';
    return new Intl.NumberFormat('pt-MZ', {
      style: 'currency',
      currency: currency,
      minimumFractionDigits: 2
    }).format(amount);
  }

  formatNumber(value: number | undefined): string {
    if (value === undefined) return '0';
    return new Intl.NumberFormat('pt-MZ').format(value);
  }

  getLastUpdatedText(lastUpdated: Date | null): string {
    if (!lastUpdated) return 'Never';
    
    const now = new Date();
    const diffInMinutes = Math.floor((now.getTime() - lastUpdated.getTime()) / (1000 * 60));
    
    if (diffInMinutes < 1) {
      return 'Just now';
    } else if (diffInMinutes < 60) {
      return `${diffInMinutes}m ago`;
    } else {
      const hours = Math.floor(diffInMinutes / 60);
      return `${hours}h ago`;
    }
  }
}