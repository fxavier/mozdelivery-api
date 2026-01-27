import { Component, OnInit, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';
import { takeUntil, debounceTime, distinctUntilChanged } from 'rxjs/operators';

import { OrderAnalytics, OrderStatus, PaymentMethod } from '../../models/order.model';
import { OrderActions } from '../../store/order.actions';
import {
  selectOrderAnalytics,
  selectIsLoadingAnalytics,
  selectError,
  selectOrdersByStatus,
  selectTodayOrdersCount,
  selectTodayRevenue
} from '../../store/order.selectors';

@Component({
  selector: 'app-order-analytics',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './order-analytics.component.html',
  styleUrl: './order-analytics.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrderAnalyticsComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  // Observables
  analytics$: Observable<OrderAnalytics | null>;
  isLoading$: Observable<boolean>;
  error$: Observable<string | null>;
  ordersByStatus$: Observable<any>;
  todayOrdersCount$: Observable<number>;
  todayRevenue$: Observable<number>;

  // Form
  dateRangeForm: FormGroup;

  // Enums for template
  OrderStatus = OrderStatus;
  PaymentMethod = PaymentMethod;

  // Chart data
  chartData: any = null;

  constructor(
    private store: Store,
    private fb: FormBuilder
  ) {
    this.dateRangeForm = this.createDateRangeForm();
    
    // Initialize observables in constructor
    this.analytics$ = this.store.select(selectOrderAnalytics);
    this.isLoading$ = this.store.select(selectIsLoadingAnalytics);
    this.error$ = this.store.select(selectError);
    this.ordersByStatus$ = this.store.select(selectOrdersByStatus);
    this.todayOrdersCount$ = this.store.select(selectTodayOrdersCount);
    this.todayRevenue$ = this.store.select(selectTodayRevenue);
  }

  ngOnInit(): void {
    this.loadAnalytics();
    this.setupDateRangeSubscription();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private createDateRangeForm(): FormGroup {
    const endDate = new Date();
    const startDate = new Date();
    startDate.setDate(startDate.getDate() - 30); // Default to last 30 days

    return this.fb.group({
      startDate: [startDate.toISOString().split('T')[0]],
      endDate: [endDate.toISOString().split('T')[0]]
    });
  }

  private setupDateRangeSubscription(): void {
    this.dateRangeForm.valueChanges.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(({ startDate, endDate }) => {
      if (startDate && endDate) {
        this.loadAnalytics(new Date(startDate), new Date(endDate));
      }
    });
  }

  private loadAnalytics(startDate?: Date, endDate?: Date): void {
    this.store.dispatch(OrderActions.loadOrderAnalytics({ startDate, endDate }));
  }

  // Quick date range selections
  selectDateRange(days: number): void {
    const endDate = new Date();
    const startDate = new Date();
    startDate.setDate(startDate.getDate() - days);

    this.dateRangeForm.patchValue({
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0]
    });
  }

  // Utility methods
  formatCurrency(amount: number, currency: string = 'MZN'): string {
    return new Intl.NumberFormat('pt-MZ', {
      style: 'currency',
      currency: currency
    }).format(amount);
  }

  formatPercentage(value: number): string {
    return `${(value * 100).toFixed(1)}%`;
  }

  getStatusDisplayName(status: OrderStatus): string {
    const statusNames: { [key in OrderStatus]: string } = {
      [OrderStatus.CREATED]: 'Created',
      [OrderStatus.PAYMENT_PROCESSING]: 'Payment Processing',
      [OrderStatus.PAYMENT_CONFIRMED]: 'Payment Confirmed',
      [OrderStatus.PAYMENT_FAILED]: 'Payment Failed',
      [OrderStatus.PREPARING]: 'Preparing',
      [OrderStatus.READY_FOR_PICKUP]: 'Ready for Pickup',
      [OrderStatus.PICKED_UP]: 'Picked Up',
      [OrderStatus.OUT_FOR_DELIVERY]: 'Out for Delivery',
      [OrderStatus.DELIVERED]: 'Delivered',
      [OrderStatus.CANCELLED]: 'Cancelled',
      [OrderStatus.DELIVERY_FAILED]: 'Delivery Failed'
    };
    return statusNames[status] || status;
  }

  getPaymentMethodDisplayName(method: PaymentMethod): string {
    const methodNames: { [key in PaymentMethod]: string } = {
      [PaymentMethod.MPESA]: 'M-Pesa',
      [PaymentMethod.MULTIBANCO]: 'Multibanco',
      [PaymentMethod.CARD]: 'Card',
      [PaymentMethod.CASH_ON_DELIVERY]: 'Cash on Delivery'
    };
    return methodNames[method] || method;
  }

  getStatusColor(status: OrderStatus): string {
    const colors: { [key in OrderStatus]: string } = {
      [OrderStatus.CREATED]: '#6B7280',
      [OrderStatus.PAYMENT_PROCESSING]: '#F59E0B',
      [OrderStatus.PAYMENT_CONFIRMED]: '#3B82F6',
      [OrderStatus.PAYMENT_FAILED]: '#EF4444',
      [OrderStatus.PREPARING]: '#F97316',
      [OrderStatus.READY_FOR_PICKUP]: '#8B5CF6',
      [OrderStatus.PICKED_UP]: '#6366F1',
      [OrderStatus.OUT_FOR_DELIVERY]: '#06B6D4',
      [OrderStatus.DELIVERED]: '#10B981',
      [OrderStatus.CANCELLED]: '#EF4444',
      [OrderStatus.DELIVERY_FAILED]: '#DC2626'
    };
    return colors[status] || '#6B7280';
  }

  // Chart helpers
  prepareChartData(analytics: OrderAnalytics): any {
    if (!analytics) return null;

    return {
      dailyOrders: {
        labels: analytics.dailyOrders.map(d => new Date(d.date).toLocaleDateString()),
        datasets: [
          {
            label: 'Orders',
            data: analytics.dailyOrders.map(d => d.orderCount),
            borderColor: '#3B82F6',
            backgroundColor: 'rgba(59, 130, 246, 0.1)',
            tension: 0.4
          },
          {
            label: 'Revenue',
            data: analytics.dailyOrders.map(d => d.revenue),
            borderColor: '#10B981',
            backgroundColor: 'rgba(16, 185, 129, 0.1)',
            tension: 0.4,
            yAxisID: 'y1'
          }
        ]
      },
      statusDistribution: {
        labels: Object.keys(analytics.ordersByStatus).map(status => 
          this.getStatusDisplayName(status as OrderStatus)
        ),
        datasets: [{
          data: Object.values(analytics.ordersByStatus),
          backgroundColor: Object.keys(analytics.ordersByStatus).map(status => 
            this.getStatusColor(status as OrderStatus)
          )
        }]
      },
      paymentMethods: {
        labels: Object.keys(analytics.ordersByPaymentMethod).map(method => 
          this.getPaymentMethodDisplayName(method as PaymentMethod)
        ),
        datasets: [{
          data: Object.values(analytics.ordersByPaymentMethod),
          backgroundColor: ['#3B82F6', '#10B981', '#F59E0B', '#EF4444']
        }]
      }
    };
  }

  // Export analytics
  exportAnalytics(): void {
    this.analytics$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(analytics => {
      if (analytics) {
        const data = this.prepareExportData(analytics);
        this.downloadCSV(data, 'order-analytics.csv');
      }
    });
  }

  private prepareExportData(analytics: OrderAnalytics): string {
    const headers = [
      'Metric',
      'Value'
    ];

    const rows = [
      ['Total Orders', analytics.totalOrders.toString()],
      ['Total Revenue', analytics.totalRevenue.toString()],
      ['Average Order Value', analytics.averageOrderValue.toString()],
      ['Total Customers', analytics.customerMetrics.totalCustomers.toString()],
      ['New Customers', analytics.customerMetrics.newCustomers.toString()],
      ['Returning Customers', analytics.customerMetrics.returningCustomers.toString()],
      ...Object.entries(analytics.ordersByStatus).map(([status, count]) => [
        `Orders - ${this.getStatusDisplayName(status as OrderStatus)}`,
        count.toString()
      ]),
      ...Object.entries(analytics.ordersByPaymentMethod).map(([method, count]) => [
        `Payment - ${this.getPaymentMethodDisplayName(method as PaymentMethod)}`,
        count.toString()
      ])
    ];

    return [headers, ...rows]
      .map(row => row.map(cell => `"${cell}"`).join(','))
      .join('\n');
  }

  private downloadCSV(data: string, filename: string): void {
    const blob = new Blob([data], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', filename);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
}