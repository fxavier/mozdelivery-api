import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  Order,
  OrderListResponse,
  OrderFilters,
  UpdateOrderStatusRequest,
  OrderAnalytics,
  OrderResponse
} from '../models/order.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/merchants`;

  constructor(private http: HttpClient) {}

  /**
   * Get orders for the current merchant with pagination and filtering
   */
  getOrders(
    page = 0,
    size = 20,
    filters?: OrderFilters
  ): Observable<OrderListResponse> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (filters) {
      if (filters.status && filters.status.length > 0) {
        params = params.set('status', filters.status.join(','));
      }
      if (filters.searchTerm) {
        params = params.set('search', filters.searchTerm);
      }
      if (filters.dateRange) {
        params = params.set('startDate', filters.dateRange.startDate.toISOString());
        params = params.set('endDate', filters.dateRange.endDate.toISOString());
      }
      if (filters.paymentMethod && filters.paymentMethod.length > 0) {
        params = params.set('paymentMethod', filters.paymentMethod.join(','));
      }
      if (filters.minAmount !== undefined) {
        params = params.set('minAmount', filters.minAmount.toString());
      }
      if (filters.maxAmount !== undefined) {
        params = params.set('maxAmount', filters.maxAmount.toString());
      }
    }

    return this.http.get<OrderListResponse>(`${this.apiUrl}/orders`, { params });
  }

  /**
   * Get a specific order by ID
   */
  getOrder(orderId: string): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/orders/${orderId}`);
  }

  /**
   * Update order status
   */
  updateOrderStatus(
    orderId: string,
    request: UpdateOrderStatusRequest
  ): Observable<OrderResponse> {
    return this.http.patch<OrderResponse>(
      `${this.apiUrl}/orders/${orderId}/status`,
      request
    );
  }

  /**
   * Cancel an order
   */
  cancelOrder(orderId: string, reason: string): Observable<OrderResponse> {
    return this.http.patch<OrderResponse>(
      `${this.apiUrl}/orders/${orderId}/cancel`,
      { reason }
    );
  }

  /**
   * Get order analytics for the current merchant
   */
  getOrderAnalytics(
    startDate?: Date,
    endDate?: Date
  ): Observable<OrderAnalytics> {
    let params = new HttpParams();
    
    if (startDate) {
      params = params.set('startDate', startDate.toISOString());
    }
    if (endDate) {
      params = params.set('endDate', endDate.toISOString());
    }

    return this.http.get<OrderAnalytics>(`${this.apiUrl}/orders/analytics`, { params });
  }

  /**
   * Export orders to CSV
   */
  exportOrders(filters?: OrderFilters): Observable<Blob> {
    let params = new HttpParams();

    if (filters) {
      if (filters.status && filters.status.length > 0) {
        params = params.set('status', filters.status.join(','));
      }
      if (filters.dateRange) {
        params = params.set('startDate', filters.dateRange.startDate.toISOString());
        params = params.set('endDate', filters.dateRange.endDate.toISOString());
      }
      if (filters.paymentMethod && filters.paymentMethod.length > 0) {
        params = params.set('paymentMethod', filters.paymentMethod.join(','));
      }
    }

    return this.http.get(`${this.apiUrl}/orders/export`, {
      params,
      responseType: 'blob'
    });
  }

  /**
   * Get orders that need attention (pending, preparing, etc.)
   */
  getOrdersNeedingAttention(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/orders/attention`);
  }

  /**
   * Bulk update order statuses
   */
  bulkUpdateOrderStatus(
    orderIds: string[],
    status: string,
    notes?: string
  ): Observable<{ success: number; errors: string[] }> {
    return this.http.patch<{ success: number; errors: string[] }>(
      `${this.apiUrl}/orders/bulk-status`,
      {
        orderIds,
        status,
        notes
      }
    );
  }

  /**
   * Get order status history
   */
  getOrderStatusHistory(orderId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/orders/${orderId}/status-history`);
  }

  /**
   * Add notes to an order
   */
  addOrderNotes(orderId: string, notes: string): Observable<OrderResponse> {
    return this.http.patch<OrderResponse>(
      `${this.apiUrl}/orders/${orderId}/notes`,
      { notes }
    );
  }

  /**
   * Get real-time order updates via Server-Sent Events
   */
  getOrderUpdates(): Observable<any> {
    return new Observable(observer => {
      const eventSource = new EventSource(`${this.apiUrl}/orders/updates`);
      
      eventSource.onmessage = event => {
        observer.next(JSON.parse(event.data));
      };
      
      eventSource.onerror = error => {
        observer.error(error);
      };
      
      return () => {
        eventSource.close();
      };
    });
  }
}