import { createAction, props } from '@ngrx/store';
import { User, LoginRequest, AuthToken } from '../../../shared/models/user.model';

// Login Actions
export const login = createAction(
  '[Auth] Login',
  props<{ credentials: LoginRequest }>()
);

export const loginSuccess = createAction(
  '[Auth] Login Success',
  props<{ user: User; token: AuthToken }>()
);

export const loginFailure = createAction(
  '[Auth] Login Failure',
  props<{ error: string }>()
);

// Logout Actions
export const logout = createAction('[Auth] Logout');

export const logoutSuccess = createAction('[Auth] Logout Success');

// Token Refresh Actions
export const refreshToken = createAction('[Auth] Refresh Token');

export const refreshTokenSuccess = createAction(
  '[Auth] Refresh Token Success',
  props<{ token: AuthToken }>()
);

export const refreshTokenFailure = createAction(
  '[Auth] Refresh Token Failure',
  props<{ error: string }>()
);

// Load Stored Auth
export const loadStoredAuth = createAction('[Auth] Load Stored Auth');

export const loadStoredAuthSuccess = createAction(
  '[Auth] Load Stored Auth Success',
  props<{ user: User; token: AuthToken }>()
);

// Clear Error
export const clearError = createAction('[Auth] Clear Error');