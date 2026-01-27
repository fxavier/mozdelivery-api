import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, interval, switchMap, startWith, catchError, of } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { DashboardData, DashboardStats, OrderSummary, RecentActivity, DashboardNotification } from '../models/dashboard.model';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/v1/merchants`;

  /**
   * Get dashboard statistics for the current merchant
   */
  getDashboardStats(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${this.baseUrl}/dashboard/stats`).pipe(
      catchError(error => {
        console.error('Error fetching dashboard stats:', error);
        return of(this.getMockStats());
      })
    );
  }

  /**
   * Get recent orders for the current merchant
   */
  getRecentOrders(limit: number = 10): Observable<OrderSummary[]> {
    return this.http.get<OrderSummary[]>(`${this.baseUrl}/orders/recent?limit=${limit}`).pipe(
      catchError(error => {
        console.error('Error fetching recent orders:', error);
        return of(this.getMockRecentOrders());
      })
    );
  }

  /**
   * Get recent activity for the current merchant
   */
  getRecentActivity(limit: number = 10): Observable<RecentActivity[]> {
    return this.http.get<RecentActivity[]>(`${this.baseUrl}/dashboard/activity?limit=${limit}`).pipe(
      catchError(error => {
        console.error('Error fetching recent activity:', error);
        return of(this.getMockRecentActivity());
      })
    );
  }

  /**
   * Get notifications for the current merchant
   */
  getNotifications(limit: number = 5): Observable<DashboardNotification[]> {
    return this.http.get<DashboardNotification[]>(`${this.baseUrl}/notifications?limit=${limit}`).pipe(
      catchError(error => {
        console.error('Error fetching notifications:', error);
        return of(this.getMockNotifications());
      })
    );
  }

  /**
   * Get complete dashboard data
   */
  getDashboardData(): Observable<DashboardData> {
    return this.http.get<DashboardData>(`${this.baseUrl}/dashboard`).pipe(
      catchError(error => {
        console.error('Error fetching dashboard data:', error);
        return of(this.getMockDashboardData());
      })
    );
  }

  /**
   * Get real-time dashboard updates (polling every 30 seconds)
   */
  getDashboardUpdates(): Observable<DashboardData> {
    return interval(30000).pipe(
      startWith(0),
      switchMap(() => this.getDashboardData())
    );
  }

  /**
   * Mark notification as read
   */
  markNotificationAsRead(notificationId: string): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/notifications/${notificationId}/read`, {});
  }

  /**
   * Update order status
   */
  updateOrderStatus(orderId: string, status: string): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/orders/${orderId}/status`, { status });
  }

  // Mock data methods for development/fallback
  private getMockStats(): DashboardStats {
    return {
      totalOrders: 1234,
      revenue: 45678.50,
      activeProducts: 89,
      pendingOrders: 23,
      todayOrders: 45,
      weeklyRevenue: 12500.75,
      monthlyRevenue: 45678.50
    };
  }

  private getMockRecentOrders(): OrderSummary[] {
    return [
      {
        id: 'ORD-001',
        customerName: 'João Silva',
        status: 'PREPARING' as any,
        total: 125.50,
        currency: 'MZN',
        createdAt: new Date(Date.now() - 300000), // 5 minutes ago
        items: [
          { productId: 'P1', productName: 'Chicken Burger', quantity: 2, price: 45.00 },
          { productId: 'P2', productName: 'French Fries', quantity: 1, price: 35.50 }
        ]
      },
      {
        id: 'ORD-002',
        customerName: 'Maria Santos',
        status: 'PAYMENT_CONFIRMED' as any,
        total: 89.25,
        currency: 'MZN',
        createdAt: new Date(Date.now() - 600000), // 10 minutes ago
        items: [
          { productId: 'P3', productName: 'Pizza Margherita', quantity: 1, price: 89.25 }
        ]
      }
    ];
  }

  private getMockRecentActivity(): RecentActivity[] {
    return [
      {
        id: 'ACT-001',
        type: 'ORDER_COMPLETED' as any,
        message: 'Order #ORD-001 was completed',
        timestamp: new Date(Date.now() - 120000), // 2 minutes ago
        orderId: 'ORD-001'
      },
      {
        id: 'ACT-002',
        type: 'PRODUCT_ADDED' as any,
        message: 'New product "Chicken Burger" was added',
        timestamp: new Date(Date.now() - 900000), // 15 minutes ago
        productId: 'P1'
      }
    ];
  }

  private getMockNotifications(): DashboardNotification[] {
    return [
      {
        id: 'NOT-001',
        type: 'ORDER_RECEIVED' as any,
        title: 'New Order Received',
        message: 'You have a new order from João Silva',
        timestamp: new Date(Date.now() - 300000),
        read: false,
        actionUrl: '/orders/ORD-001'
      }
    ];
  }

  private getMockDashboardData(): DashboardData {
    return {
      stats: this.getMockStats(),
      recentOrders: this.getMockRecentOrders(),
      recentActivity: this.getMockRecentActivity(),
      notifications: this.getMockNotifications(),
      lastUpdated: new Date()
    };
  }
}