import { AuthState } from './auth/auth.state';

export interface AppState {
  auth: AuthState;
}

export const initialAppState: AppState = {
  auth: {
    user: null,
    token: null,
    isLoading: false,
    error: null
  }
};