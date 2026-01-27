import { Component, OnInit, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';
import { takeUntil, filter, switchMap } from 'rxjs/operators';

import { Order, OrderStatus, PaymentMethod, UpdateOrderStatusRequest } from '../../models/order.model';
import { OrderActions } from '../../store/order.actions';
import {
  selectSelectedOrder,
  selectIsLoadingOrder,
  selectIsUpdatingStatus,
  selectError,
  selectOrderStatusHistoryById
} from '../../store/order.selectors';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './order-detail.component.html',
  styleUrl: './order-detail.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrderDetailComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  private orderId!: string;

  // Observables
  order$: Observable<Order | null>;
  isLoading$: Observable<boolean>;
  isUpdatingStatus$: Observable<boolean>;
  error$: Observable<string | null>;
  statusHistory$!: Observable<any[]>;

  // Forms
  statusUpdateForm: FormGroup;
  notesForm: FormGroup;
  cancelForm: FormGroup;

  // Enums for template
  OrderStatus = OrderStatus;
  PaymentMethod = PaymentMethod;

  // UI state
  showStatusHistory = false;
  showCancelModal = false;
  showNotesModal = false;

  constructor(
    private store: Store,
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.statusUpdateForm = this.createStatusUpdateForm();
    this.notesForm = this.createNotesForm();
    this.cancelForm = this.createCancelForm();
    
    // Initialize observables in constructor
    this.order$ = this.store.select(selectSelectedOrder);
    this.isLoading$ = this.store.select(selectIsLoadingOrder);
    this.isUpdatingStatus$ = this.store.select(selectIsUpdatingStatus);
    this.error$ = this.store.select(selectError);
  }

  ngOnInit(): void {
    this.route.params.pipe(
      takeUntil(this.destroy$)
    ).subscribe(params => {
      this.orderId = params['id'];
      this.loadOrder();
      this.statusHistory$ = this.store.select(selectOrderStatusHistoryById(this.orderId));
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private createStatusUpdateForm(): FormGroup {
    return this.fb.group({
      status: ['', Validators.required],
      notes: [''],
      estimatedDeliveryTime: ['']
    });
  }

  private createNotesForm(): FormGroup {
    return this.fb.group({
      notes: ['', Validators.required]
    });
  }

  private createCancelForm(): FormGroup {
    return this.fb.group({
      reason: ['', Validators.required]
    });
  }

  private loadOrder(): void {
    this.store.dispatch(OrderActions.loadOrder({ orderId: this.orderId }));
    this.store.dispatch(OrderActions.loadOrderStatusHistory({ orderId: this.orderId }));
  }

  // Navigation
  goBack(): void {
    this.router.navigate(['/orders']);
  }

  // Status management
  updateOrderStatus(): void {
    if (this.statusUpdateForm.valid) {
      const formValue = this.statusUpdateForm.value;
      const request: UpdateOrderStatusRequest = {
        status: formValue.status,
        notes: formValue.notes || undefined,
        estimatedDeliveryTime: formValue.estimatedDeliveryTime ? 
          new Date(formValue.estimatedDeliveryTime) : undefined
      };

      this.store.dispatch(OrderActions.updateOrderStatus({
        orderId: this.orderId,
        request
      }));

      this.statusUpdateForm.reset();
    }
  }

  // Quick status updates
  markAsPreparing(): void {
    this.store.dispatch(OrderActions.updateOrderStatus({
      orderId: this.orderId,
      request: { status: OrderStatus.PREPARING }
    }));
  }

  markAsReady(): void {
    this.store.dispatch(OrderActions.updateOrderStatus({
      orderId: this.orderId,
      request: { status: OrderStatus.READY_FOR_PICKUP }
    }));
  }

  markAsPickedUp(): void {
    this.store.dispatch(OrderActions.updateOrderStatus({
      orderId: this.orderId,
      request: { status: OrderStatus.PICKED_UP }
    }));
  }

  // Cancel order
  openCancelModal(): void {
    this.showCancelModal = true;
  }

  closeCancelModal(): void {
    this.showCancelModal = false;
    this.cancelForm.reset();
  }

  cancelOrder(): void {
    if (this.cancelForm.valid) {
      this.store.dispatch(OrderActions.cancelOrder({
        orderId: this.orderId,
        reason: this.cancelForm.value.reason
      }));
      this.closeCancelModal();
    }
  }

  // Notes management
  openNotesModal(): void {
    this.showNotesModal = true;
  }

  closeNotesModal(): void {
    this.showNotesModal = false;
    this.notesForm.reset();
  }

  addNotes(): void {
    if (this.notesForm.valid) {
      this.store.dispatch(OrderActions.addOrderNotes({
        orderId: this.orderId,
        notes: this.notesForm.value.notes
      }));
      this.closeNotesModal();
    }
  }

  // Status history
  toggleStatusHistory(): void {
    this.showStatusHistory = !this.showStatusHistory;
  }

  // Utility methods
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

  canUpdateStatus(order: Order): boolean {
    return ![OrderStatus.DELIVERED, OrderStatus.CANCELLED].includes(order.status);
  }

  canCancel(order: Order): boolean {
    return ![OrderStatus.DELIVERED, OrderStatus.CANCELLED, OrderStatus.OUT_FOR_DELIVERY].includes(order.status);
  }

  getAvailableStatusTransitions(currentStatus: OrderStatus): OrderStatus[] {
    const transitions: { [key in OrderStatus]: OrderStatus[] } = {
      [OrderStatus.CREATED]: [OrderStatus.PAYMENT_PROCESSING, OrderStatus.CANCELLED],
      [OrderStatus.PAYMENT_PROCESSING]: [OrderStatus.PAYMENT_CONFIRMED, OrderStatus.PAYMENT_FAILED],
      [OrderStatus.PAYMENT_CONFIRMED]: [OrderStatus.PREPARING, OrderStatus.CANCELLED],
      [OrderStatus.PAYMENT_FAILED]: [OrderStatus.CANCELLED],
      [OrderStatus.PREPARING]: [OrderStatus.READY_FOR_PICKUP, OrderStatus.CANCELLED],
      [OrderStatus.READY_FOR_PICKUP]: [OrderStatus.PICKED_UP, OrderStatus.CANCELLED],
      [OrderStatus.PICKED_UP]: [OrderStatus.OUT_FOR_DELIVERY],
      [OrderStatus.OUT_FOR_DELIVERY]: [OrderStatus.DELIVERED, OrderStatus.DELIVERY_FAILED],
      [OrderStatus.DELIVERED]: [],
      [OrderStatus.CANCELLED]: [],
      [OrderStatus.DELIVERY_FAILED]: [OrderStatus.OUT_FOR_DELIVERY, OrderStatus.CANCELLED]
    };
    return transitions[currentStatus] || [];
  }
}