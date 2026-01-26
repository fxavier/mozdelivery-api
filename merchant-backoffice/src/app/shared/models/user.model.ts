export interface User {
  id: string;
  email: string;
  name: string;
  role: UserRole;
  merchantId?: string;
  permissions: Permission[];
  createdAt: Date;
  updatedAt: Date;
}

export enum UserRole {
  ADMIN = 'ADMIN',
  MERCHANT = 'MERCHANT',
  COURIER = 'COURIER',
  CLIENT = 'CLIENT'
}

export enum Permission {
  // Merchant permissions
  MANAGE_CATALOGS = 'MANAGE_CATALOGS',
  MANAGE_ORDERS = 'MANAGE_ORDERS',
  VIEW_ANALYTICS = 'VIEW_ANALYTICS',
  MANAGE_SETTINGS = 'MANAGE_SETTINGS',
  
  // Admin permissions
  MANAGE_MERCHANTS = 'MANAGE_MERCHANTS',
  MANAGE_COURIERS = 'MANAGE_COURIERS',
  VIEW_SYSTEM_ANALYTICS = 'VIEW_SYSTEM_ANALYTICS',
  MANAGE_SYSTEM_SETTINGS = 'MANAGE_SYSTEM_SETTINGS'
}

export interface AuthToken {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  tokenType: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  user: User;
  token: AuthToken;
}