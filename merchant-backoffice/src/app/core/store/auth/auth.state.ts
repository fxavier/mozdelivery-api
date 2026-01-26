import { User, AuthToken } from '../../../shared/models/user.model';

export interface AuthState {
  user: User | null;
  token: AuthToken | null;
  isLoading: boolean;
  error: string | null;
}