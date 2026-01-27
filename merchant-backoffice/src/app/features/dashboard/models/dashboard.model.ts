export interface DashboardStats {
  totalOrders: number;
  revenue: number;
  activeProducts: number;
  pendingOrders: number;
  todayOrders: number;
  weeklyRevenue: number;
  monthlyRevenue: number;
}

export interface OrderSummary {
  id: string;
  customerName: string;
  status: OrderStatus;
  total: number;
  currency: string;
  createdAt: Date;
  items: OrderItem[];
}

export interface OrderItem {
  productId: string;
  productName: string;
  quantity: number;
  price: number;
}

export enum OrderStatus {
  CREATED = 'CREATED',
  PAYMENT_PROCESSING = 'PAYMENT_PROCESSING',
  PAYMENT_CONFIRMED = 'PAYMENT_CONFIRMED',
  PREPARING = 'PREPARING',
  READY_FOR_PICKUP = 'READY_FOR_PICKUP',
  PICKED_UP = 'PICKED_UP',
  OUT_FOR_DELIVERY = 'OUT_FOR_DELIVERY',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED',
  DELIVERY_FAILED = 'DELIVERY_FAILED'
}

export interface RecentActivity {
  id: string;
  type: ActivityType;
  message: string;
  timestamp: Date;
  orderId?: string;
  productId?: string;
}

export enum ActivityType {
  ORDER_COMPLETED = 'ORDER_COMPLETED',
  ORDER_CREATED = 'ORDER_CREATED',
  ORDER_CANCELLED = 'ORDER_CANCELLED',
  PRODUCT_ADDED = 'PRODUCT_ADDED',
  PRODUCT_UPDATED = 'PRODUCT_UPDATED',
  INVENTORY_LOW = 'INVENTORY_LOW'
}

export interface DashboardNotification {
  id: string;
  type: NotificationType;
  title: string;
  message: string;
  timestamp: Date;
  read: boolean;
  actionUrl?: string;
}

export enum NotificationType {
  ORDER_RECEIVED = 'ORDER_RECEIVED',
  PAYMENT_CONFIRMED = 'PAYMENT_CONFIRMED',
  INVENTORY_ALERT = 'INVENTORY_ALERT',
  SYSTEM_UPDATE = 'SYSTEM_UPDATE'
}

export interface DashboardData {
  stats: DashboardStats;
  recentOrders: OrderSummary[];
  recentActivity: RecentActivity[];
  notifications: DashboardNotification[];
  lastUpdated: Date;
}