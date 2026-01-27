import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { AppState } from '../../../../core/store/app.state';
import { OrderSummary, OrderStatus } from '../../models/dashboard.model';
import { selectRecentOrders, selectOrdersLoading, selectOrdersError } from '../../store/dashboard.selectors';
import * as DashboardActions from '../../store/dashboard.actions';

@Component({
  selector: 'app-order-queue',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-queue.component.html',
  styleUrl: './order-queue.component.css'
})
export class OrderQueueComponent implements OnInit, OnDestroy {
  private readonly store = inject(Store<AppState>);
  private readonly destroy$ = new Subject<void>();

  orders$: Observable<OrderSummary[]>;
  loading$: Observable<boolean>;
  error$: Observable<string | null>;

  readonly OrderStatus = OrderStatus;

  constructor() {
    this.orders$ = this.store.select(selectRecentOrders);
    this.loading$ = this.store.select(selectOrdersLoading);
    this.error$ = this.store.select(selectOrdersError);
  }

  ngOnInit(): void {
    this.store.dispatch(DashboardActions.loadRecentOrders());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onUpdateOrderStatus(orderId: string, status: OrderStatus): void {
    this.store.dispatch(DashboardActions.updateOrderStatus({ 
      orderId, 
      status: status.toString() 
    }));
  }

  onRefreshOrders(): void {
    this.store.dispatch(DashboardActions.loadRecentOrders());
  }

  getStatusColor(status: OrderStatus): string {
    switch (status) {
      case OrderStatus.CREATED:
      case OrderStatus.PAYMENT_PROCESSING:
        return 'bg-yellow-100 text-yellow-800';
      case OrderStatus.PAYMENT_CONFIRMED:
      case OrderStatus.PREPARING:
        return 'bg-blue-100 text-blue-800';
      case OrderStatus.READY_FOR_PICKUP:
      case OrderStatus.PICKED_UP:
        return 'bg-purple-100 text-purple-800';
      case OrderStatus.OUT_FOR_DELIVERY:
        return 'bg-indigo-100 text-indigo-800';
      case OrderStatus.DELIVERED:
        return 'bg-green-100 text-green-800';
      case OrderStatus.CANCELLED:
      case OrderStatus.DELIVERY_FAILED:
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  getStatusDisplayName(status: OrderStatus): string {
    switch (status) {
      case OrderStatus.CREATED:
        return 'New Order';
      case OrderStatus.PAYMENT_PROCESSING:
        return 'Processing Payment';
      case OrderStatus.PAYMENT_CONFIRMED:
        return 'Payment Confirmed';
      case OrderStatus.PREPARING:
        return 'Preparing';
      case OrderStatus.READY_FOR_PICKUP:
        return 'Ready for Pickup';
      case OrderStatus.PICKED_UP:
        return 'Picked Up';
      case OrderStatus.OUT_FOR_DELIVERY:
        return 'Out for Delivery';
      case OrderStatus.DELIVERED:
        return 'Delivered';
      case OrderStatus.CANCELLED:
        return 'Cancelled';
      case OrderStatus.DELIVERY_FAILED:
        return 'Delivery Failed';
      default:
        return status;
    }
  }

  canUpdateStatus(currentStatus: OrderStatus): boolean {
    return [
      OrderStatus.PAYMENT_CONFIRMED,
      OrderStatus.PREPARING,
      OrderStatus.READY_FOR_PICKUP
    ].includes(currentStatus);
  }

  getNextStatus(currentStatus: OrderStatus): OrderStatus | null {
    switch (currentStatus) {
      case OrderStatus.PAYMENT_CONFIRMED:
        return OrderStatus.PREPARING;
      case OrderStatus.PREPARING:
        return OrderStatus.READY_FOR_PICKUP;
      case OrderStatus.READY_FOR_PICKUP:
        return OrderStatus.PICKED_UP;
      default:
        return null;
    }
  }

  formatCurrency(amount: number, currency: string): string {
    return new Intl.NumberFormat('pt-MZ', {
      style: 'currency',
      currency: currency,
      minimumFractionDigits: 2
    }).format(amount);
  }

  formatTime(date: Date): string {
    return new Intl.DateTimeFormat('pt-MZ', {
      hour: '2-digit',
      minute: '2-digit'
    }).format(new Date(date));
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

  trackByOrderId(index: number, order: OrderSummary): string {
    return order.id;
  }
}