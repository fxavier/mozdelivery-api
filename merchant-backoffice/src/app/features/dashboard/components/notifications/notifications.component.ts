import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { AppState } from '../../../../core/store/app.state';
import { DashboardNotification, NotificationType } from '../../models/dashboard.model';
import { 
  selectNotifications, 
  selectUnreadNotifications, 
  selectNotificationsLoading, 
  selectNotificationsError 
} from '../../store/dashboard.selectors';
import * as DashboardActions from '../../store/dashboard.actions';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.css'
})
export class NotificationsComponent implements OnInit, OnDestroy {
  private readonly store = inject(Store<AppState>);
  private readonly destroy$ = new Subject<void>();

  notifications$: Observable<DashboardNotification[]>;
  unreadNotifications$: Observable<DashboardNotification[]>;
  loading$: Observable<boolean>;
  error$: Observable<string | null>;

  showAll = false;
  readonly NotificationType = NotificationType;

  constructor() {
    this.notifications$ = this.store.select(selectNotifications);
    this.unreadNotifications$ = this.store.select(selectUnreadNotifications);
    this.loading$ = this.store.select(selectNotificationsLoading);
    this.error$ = this.store.select(selectNotificationsError);
  }

  ngOnInit(): void {
    this.store.dispatch(DashboardActions.loadNotifications());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onMarkAsRead(notificationId: string): void {
    this.store.dispatch(DashboardActions.markNotificationAsRead({ notificationId }));
  }

  onToggleShowAll(): void {
    this.showAll = !this.showAll;
  }

  onRefreshNotifications(): void {
    this.store.dispatch(DashboardActions.loadNotifications());
  }

  getNotificationIcon(type: NotificationType): string {
    switch (type) {
      case NotificationType.ORDER_RECEIVED:
        return 'M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z';
      case NotificationType.PAYMENT_CONFIRMED:
        return 'M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1';
      case NotificationType.INVENTORY_ALERT:
        return 'M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z';
      case NotificationType.SYSTEM_UPDATE:
        return 'M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z';
      default:
        return 'M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z';
    }
  }

  getNotificationColor(type: NotificationType): string {
    switch (type) {
      case NotificationType.ORDER_RECEIVED:
        return 'bg-blue-100 text-blue-600';
      case NotificationType.PAYMENT_CONFIRMED:
        return 'bg-green-100 text-green-600';
      case NotificationType.INVENTORY_ALERT:
        return 'bg-yellow-100 text-yellow-600';
      case NotificationType.SYSTEM_UPDATE:
        return 'bg-purple-100 text-purple-600';
      default:
        return 'bg-gray-100 text-gray-600';
    }
  }

  getTimeAgo(date: Date): string {
    const now = new Date();
    const diffInMinutes = Math.floor((now.getTime() - new Date(date).getTime()) / (1000 * 60));
    
    if (diffInMinutes < 1) {
      return 'Just now';
    } else if (diffInMinutes < 60) {
      return `${diffInMinutes}m ago`;
    } else if (diffInMinutes < 1440) {
      const hours = Math.floor(diffInMinutes / 60);
      return `${hours}h ago`;
    } else {
      const days = Math.floor(diffInMinutes / 1440);
      return `${days}d ago`;
    }
  }

  trackByNotificationId(index: number, notification: DashboardNotification): string {
    return notification.id;
  }
}