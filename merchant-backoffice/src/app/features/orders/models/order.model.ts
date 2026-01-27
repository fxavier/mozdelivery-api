export interface Order {
  id: string;
  merchantId: string;
  clientId?: string;
  guestInfo?: GuestInfo;
  items: OrderItem[];
  deliveryAddress: DeliveryAddress;
  status: OrderStatus;
  paymentInfo: PaymentInfo;
  totalAmount: number;
  currency: string;
  deliveryCode?: string;
  estimatedDeliveryTime?: Date;
  actualDeliveryTime?: Date;
  createdAt: Date;
  updatedAt: Date;
  notes?: string;
  cancellationReason?: string;
}

export interface GuestInfo {
  contactPhone: string;
  contactEmail: string;
  contactName: string;
  trackingToken: string;
}

export interface OrderItem {
  id: string;
  productId: string;
  productName: string;
  productImage?: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  modifiers?: ProductModifier[];
  specialInstructions?: string;
}

export interface ProductModifier {
  id: string;
  name: string;
  price: number;
  selected: boolean;
}

export interface DeliveryAddress {
  street: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  coordinates?: {
    latitude: number;
    longitude: number;
  };
  deliveryInstructions?: string;
}

export interface PaymentInfo {
  method: PaymentMethod;
  status: PaymentStatus;
  transactionId?: string;
  amount: number;
  currency: string;
  processedAt?: Date;
}

export enum PaymentMethod {
  MPESA = 'MPESA',
  MULTIBANCO = 'MULTIBANCO',
  CARD = 'CARD',
  CASH_ON_DELIVERY = 'CASH_ON_DELIVERY'
}

export enum PaymentStatus {
  PENDING = 'PENDING',
  PROCESSING = 'PROCESSING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
  REFUNDED = 'REFUNDED'
}

export enum OrderStatus {
  CREATED = 'CREATED',
  PAYMENT_PROCESSING = 'PAYMENT_PROCESSING',
  PAYMENT_CONFIRMED = 'PAYMENT_CONFIRMED',
  PAYMENT_FAILED = 'PAYMENT_FAILED',
  PREPARING = 'PREPARING',
  READY_FOR_PICKUP = 'READY_FOR_PICKUP',
  PICKED_UP = 'PICKED_UP',
  OUT_FOR_DELIVERY = 'OUT_FOR_DELIVERY',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED',
  DELIVERY_FAILED = 'DELIVERY_FAILED'
}

export interface OrderFilters {
  status?: OrderStatus[];
  dateRange?: {
    startDate: Date;
    endDate: Date;
  };
  searchTerm?: string;
  paymentMethod?: PaymentMethod[];
  minAmount?: number;
  maxAmount?: number;
}

export interface OrderListResponse {
  content: Order[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  size: number;
}

export interface UpdateOrderStatusRequest {
  status: OrderStatus;
  notes?: string;
  estimatedDeliveryTime?: Date;
}

export interface OrderAnalytics {
  totalOrders: number;
  totalRevenue: number;
  averageOrderValue: number;
  ordersByStatus: { [key in OrderStatus]: number };
  ordersByPaymentMethod: { [key in PaymentMethod]: number };
  dailyOrders: DailyOrderStats[];
  topProducts: ProductStats[];
  customerMetrics: CustomerMetrics;
}

export interface DailyOrderStats {
  date: Date;
  orderCount: number;
  revenue: number;
}

export interface ProductStats {
  productId: string;
  productName: string;
  orderCount: number;
  revenue: number;
  quantity: number;
}

export interface CustomerMetrics {
  totalCustomers: number;
  newCustomers: number;
  returningCustomers: number;
  averageOrdersPerCustomer: number;
}

export interface OrderStatusUpdate {
  orderId: string;
  oldStatus: OrderStatus;
  newStatus: OrderStatus;
  updatedAt: Date;
  updatedBy: string;
  notes?: string;
}

// Request/Response DTOs
export interface CreateOrderRequest {
  items: CreateOrderItemRequest[];
  deliveryAddress: DeliveryAddress;
  paymentMethod: PaymentMethod;
  guestInfo?: GuestInfo;
  notes?: string;
}

export interface CreateOrderItemRequest {
  productId: string;
  quantity: number;
  modifiers?: string[];
  specialInstructions?: string;
}

export interface OrderResponse {
  order: Order;
  message: string;
}