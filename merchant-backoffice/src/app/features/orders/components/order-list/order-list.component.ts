import { Component, OnInit, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';
import { takeUntil, debounceTime, distinctUntilChanged } from 'rxjs/operators';

import { Order, OrderStatus, OrderFilters, PaymentMethod } from '../../models/order.model';
import { OrderActions } from '../../store/order.actions';
import {
  selectOrders,
  selectIsLoading,
  selectError,
  selectPaginationInfo,
  selectOrderFilters,
  selectSelectedOrderIds,
  selectIsAllOrdersSelected,
  selectIsSomeOrdersSelected,
  selectAutoRefreshEnabled,
  selectLastUpdated
} from '../../store/order.selectors';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './order-list.component.html',
  styleUrl: './order-list.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrderListComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  // Observables
  orders$: Observable<Order[]>;
  isLoading$: Observable<boolean>;
  error$: Observable<string | null>;
  paginationInfo$: Observable<any>;
  selectedOrderIds$: Observable<string[]>;
  isAllSelected$: Observable<boolean>;
  isSomeSelected$: Observable<boolean>;
  autoRefreshEnabled$: Observable<boolean>;
  lastUpdated$: Observable<Date | null>;

  // Form
  filterForm: FormGroup;

  // Enums for template
  OrderStatus = OrderStatus;
  PaymentMethod = PaymentMethod;

  // UI state
  showFilters = false;
  showBulkActions = false;

  constructor(
    private store: Store,
    private fb: FormBuilder
  ) {
    this.filterForm = this.createFilterForm();
    
    // Initialize observables in constructor
    this.orders$ = this.store.select(selectOrders);
    this.isLoading$ = this.store.select(selectIsLoading);
    this.error$ = this.store.select(selectError);
    this.paginationInfo$ = this.store.select(selectPaginationInfo);
    this.selectedOrderIds$ = this.store.select(selectSelectedOrderIds);
    this.isAllSelected$ = this.store.select(selectIsAllOrdersSelected);
    this.isSomeSelected$ = this.store.select(selectIsSomeOrdersSelected);
    this.autoRefreshEnabled$ = this.store.select(selectAutoRefreshEnabled);
    this.lastUpdated$ = this.store.select(selectLastUpdated);
  }

  ngOnInit(): void {
    this.loadOrders();
    this.setupFilterSubscription();
    this.loadCurrentFilters();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private createFilterForm(): FormGroup {
    return this.fb.group({
      status: [[]],
      searchTerm: [''],
      dateRange: this.fb.group({
        startDate: [''],
        endDate: ['']
      }),
      paymentMethod: [[]],
      minAmount: [''],
      maxAmount: ['']
    });
  }

  private setupFilterSubscription(): void {
    this.filterForm.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(filters => {
      this.applyFilters(filters);
    });
  }

  private loadCurrentFilters(): void {
    this.store.select(selectOrderFilters).pipe(
      takeUntil(this.destroy$)
    ).subscribe(filters => {
      this.filterForm.patchValue(filters, { emitEvent: false });
    });
  }

  private loadOrders(): void {
    this.store.dispatch(OrderActions.loadOrders({}));
  }

  // Filter methods
  applyFilters(filters: any): void {
    const cleanFilters: OrderFilters = {};

    if (filters.status && filters.status.length > 0) {
      cleanFilters.status = filters.status;
    }

    if (filters.searchTerm?.trim()) {
      cleanFilters.searchTerm = filters.searchTerm.trim();
    }

    if (filters.dateRange?.startDate && filters.dateRange?.endDate) {
      cleanFilters.dateRange = {
        startDate: new Date(filters.dateRange.startDate),
        endDate: new Date(filters.dateRange.endDate)
      };
    }

    if (filters.paymentMethod && filters.paymentMethod.length > 0) {
      cleanFilters.paymentMethod = filters.paymentMethod;
    }

    if (filters.minAmount) {
      cleanFilters.minAmount = parseFloat(filters.minAmount);
    }

    if (filters.maxAmount) {
      cleanFilters.maxAmount = parseFloat(filters.maxAmount);
    }

    this.store.dispatch(OrderActions.setFilters({ filters: cleanFilters }));
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.store.dispatch(OrderActions.clearFilters());
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  // Pagination methods
  onPageChange(page: number): void {
    this.store.dispatch(OrderActions.setCurrentPage({ page }));
  }

  onPageSizeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const size = parseInt(target.value, 10);
    this.store.dispatch(OrderActions.setPageSize({ size }));
  }

  // Selection methods
  onOrderSelect(orderId: string, selected: boolean): void {
    if (selected) {
      this.store.dispatch(OrderActions.selectOrders({ orderIds: [orderId] }));
    } else {
      this.store.dispatch(OrderActions.deselectOrders({ orderIds: [orderId] }));
    }
  }

  onSelectAll(selected: boolean): void {
    if (selected) {
      this.store.dispatch(OrderActions.selectAllOrders());
    } else {
      this.store.dispatch(OrderActions.clearSelection());
    }
  }

  // Order actions
  viewOrder(orderId: string): void {
    this.store.dispatch(OrderActions.setSelectedOrder({ orderId }));
    // Navigate to order detail - implement routing
  }

  updateOrderStatus(orderId: string, status: OrderStatus): void {
    this.store.dispatch(OrderActions.updateOrderStatus({
      orderId,
      request: { status }
    }));
  }

  cancelOrder(orderId: string, reason: string): void {
    this.store.dispatch(OrderActions.cancelOrder({ orderId, reason }));
  }

  // Bulk actions
  toggleBulkActions(): void {
    this.showBulkActions = !this.showBulkActions;
  }

  bulkUpdateStatus(status: string): void {
    this.selectedOrderIds$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(orderIds => {
      if (orderIds.length > 0) {
        this.store.dispatch(OrderActions.bulkUpdateOrderStatus({
          orderIds,
          status
        }));
      }
    });
  }

  // Export
  exportOrders(): void {
    this.store.dispatch(OrderActions.exportOrders({}));
  }

  // Refresh
  refreshOrders(): void {
    this.store.dispatch(OrderActions.refreshOrders());
  }

  toggleAutoRefresh(): void {
    this.autoRefreshEnabled$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(enabled => {
      this.store.dispatch(OrderActions.autoRefreshToggle({ enabled: !enabled }));
    });
  }

  // Utility methods
  Math = Math; // Expose Math to template
  
  getStatusClass(status: OrderStatus): string {
    const statusClasses: { [key in OrderStatus]: string } = {
      [OrderStatus.CREATED]: 'bg-gray-100 text-gray-800',
      [OrderStatus.PAYMENT_PROCESSING]: 'bg-yellow-100 text-yellow-800',
      [OrderStatus.PAYMENT_CONFIRMED]: 'bg-blue-100 text-blue-800',
      [OrderStatus.PAYMENT_FAILED]: 'bg-red-100 text-red-800',
      [OrderStatus.PREPARING]: 'bg-orange-100 text-orange-800',
      [OrderStatus.READY_FOR_PICKUP]: 'bg-purple-100 text-purple-800',
      [OrderStatus.PICKED_UP]: 'bg-indigo-100 text-indigo-800',
      [OrderStatus.OUT_FOR_DELIVERY]: 'bg-blue-100 text-blue-800',
      [OrderStatus.DELIVERED]: 'bg-green-100 text-green-800',
      [OrderStatus.CANCELLED]: 'bg-red-100 text-red-800',
      [OrderStatus.DELIVERY_FAILED]: 'bg-red-100 text-red-800'
    };
    return statusClasses[status] || 'bg-gray-100 text-gray-800';
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

  formatCurrency(amount: number, currency: string): string {
    return new Intl.NumberFormat('pt-MZ', {
      style: 'currency',
      currency: currency
    }).format(amount);
  }

  getCustomerName(order: Order): string {
    return order.guestInfo?.contactName || 'Registered Customer';
  }

  trackByOrderId(index: number, order: Order): string {
    return order.id;
  }
}