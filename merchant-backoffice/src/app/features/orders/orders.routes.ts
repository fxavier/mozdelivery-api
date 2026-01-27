import { Routes } from '@angular/router';

export const ordersRoutes: Routes = [
  {
    path: '',
    redirectTo: 'list',
    pathMatch: 'full'
  },
  {
    path: 'list',
    loadComponent: () => import('./components/order-list/order-list.component').then(c => c.OrderListComponent),
    title: 'Orders - Merchant Backoffice'
  },
  {
    path: 'analytics',
    loadComponent: () => import('./components/order-analytics/order-analytics.component').then(c => c.OrderAnalyticsComponent),
    title: 'Order Analytics - Merchant Backoffice'
  },
  {
    path: ':id',
    loadComponent: () => import('./components/order-detail/order-detail.component').then(c => c.OrderDetailComponent),
    title: 'Order Details - Merchant Backoffice'
  }
];