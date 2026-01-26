export interface Merchant {
  id: string;
  businessName: string;
  displayName: string;
  vertical: MerchantVertical;
  status: MerchantStatus;
  businessDetails: BusinessDetails;
  configuration: MerchantConfiguration;
  approvalStatus: ApprovalStatus;
  createdAt: Date;
  updatedAt: Date;
}

export enum MerchantVertical {
  RESTAURANT = 'RESTAURANT',
  GROCERY = 'GROCERY',
  PHARMACY = 'PHARMACY',
  CONVENIENCE = 'CONVENIENCE',
  ELECTRONICS = 'ELECTRONICS',
  FLORIST = 'FLORIST',
  BEVERAGES = 'BEVERAGES',
  FUEL_STATION = 'FUEL_STATION'
}

export enum MerchantStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED',
  PENDING_APPROVAL = 'PENDING_APPROVAL'
}

export enum ApprovalStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  UNDER_REVIEW = 'UNDER_REVIEW'
}

export interface BusinessDetails {
  address: Address;
  phone: string;
  email: string;
  website?: string;
  description?: string;
  businessHours: BusinessHours[];
  taxId?: string;
  licenseNumber?: string;
}

export interface Address {
  street: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  coordinates?: {
    latitude: number;
    longitude: number;
  };
}

export interface BusinessHours {
  dayOfWeek: number; // 0 = Sunday, 1 = Monday, etc.
  openTime: string; // HH:mm format
  closeTime: string; // HH:mm format
  isClosed: boolean;
}

export interface MerchantConfiguration {
  deliveryRadius: number; // in kilometers
  minimumOrderAmount: number;
  deliveryFee: number;
  estimatedPreparationTime: number; // in minutes
  acceptsOnlinePayments: boolean;
  acceptsCashOnDelivery: boolean;
  features: MerchantFeature[];
}

export enum MerchantFeature {
  INVENTORY_MANAGEMENT = 'INVENTORY_MANAGEMENT',
  PRESCRIPTION_VALIDATION = 'PRESCRIPTION_VALIDATION',
  AGE_VERIFICATION = 'AGE_VERIFICATION',
  BULK_ORDERS = 'BULK_ORDERS',
  SCHEDULED_DELIVERY = 'SCHEDULED_DELIVERY'
}