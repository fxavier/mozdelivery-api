import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { map, tap, catchError } from 'rxjs/operators';
import { User, LoginRequest, LoginResponse, AuthToken } from '../../shared/models/user.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;
  
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  private tokenSubject = new BehaviorSubject<AuthToken | null>(null);
  
  public currentUser$ = this.currentUserSubject.asObservable();
  public token$ = this.tokenSubject.asObservable();
  
  constructor() {
    this.loadStoredAuth();
  }
  
  get currentUser(): User | null {
    return this.currentUserSubject.value;
  }
  
  get token(): AuthToken | null {
    return this.tokenSubject.value;
  }
  
  get isAuthenticated(): boolean {
    const token = this.token;
    if (!token) return false;
    
    // Check if token is expired
    const expirationTime = this.getTokenExpirationTime(token.accessToken);
    return expirationTime > Date.now();
  }
  
  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, credentials)
      .pipe(
        tap(response => {
          this.setAuthData(response.user, response.token);
        }),
        catchError(error => {
          console.error('Login failed:', error);
          return throwError(() => error);
        })
      );
  }
  
  logout(): void {
    this.clearAuthData();
    // Optionally call logout endpoint
    this.http.post(`${this.apiUrl}/auth/logout`, {}).subscribe({
      error: (error) => console.error('Logout error:', error)
    });
  }
  
  refreshToken(): Observable<AuthToken> {
    const currentToken = this.token;
    if (!currentToken?.refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }
    
    return this.http.post<AuthToken>(`${this.apiUrl}/auth/refresh`, {
      refreshToken: currentToken.refreshToken
    }).pipe(
      tap(newToken => {
        this.tokenSubject.next(newToken);
        this.storeToken(newToken);
      }),
      catchError(error => {
        this.logout();
        return throwError(() => error);
      })
    );
  }
  
  hasPermission(permission: string): boolean {
    const user = this.currentUser;
    return user?.permissions.includes(permission as any) || false;
  }
  
  hasRole(role: string): boolean {
    const user = this.currentUser;
    return user?.role === role;
  }
  
  private setAuthData(user: User, token: AuthToken): void {
    this.currentUserSubject.next(user);
    this.tokenSubject.next(token);
    this.storeAuthData(user, token);
  }
  
  private clearAuthData(): void {
    this.currentUserSubject.next(null);
    this.tokenSubject.next(null);
    
    // Check if we're in a browser environment
    if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
      localStorage.removeItem('currentUser');
      localStorage.removeItem('authToken');
    }
  }
  
  private storeAuthData(user: User, token: AuthToken): void {
    this.currentUserSubject.next(user);
    this.tokenSubject.next(token);
    
    // Check if we're in a browser environment
    if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
      localStorage.setItem('currentUser', JSON.stringify(user));
      this.storeToken(token);
    }
  }
  
  private storeToken(token: AuthToken): void {
    // Check if we're in a browser environment
    if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
      localStorage.setItem('authToken', JSON.stringify(token));
    }
  }
  
  private loadStoredAuth(): void {
    // Check if we're in a browser environment
    if (typeof window === 'undefined' || typeof localStorage === 'undefined') {
      return;
    }
    
    try {
      const storedUser = localStorage.getItem('currentUser');
      const storedToken = localStorage.getItem('authToken');
      
      if (storedUser && storedToken) {
        const user = JSON.parse(storedUser) as User;
        const token = JSON.parse(storedToken) as AuthToken;
        
        // Check if token is still valid
        if (this.isTokenValid(token)) {
          this.currentUserSubject.next(user);
          this.tokenSubject.next(token);
        } else {
          this.clearAuthData();
        }
      }
    } catch (error) {
      console.error('Error loading stored auth:', error);
      this.clearAuthData();
    }
  }
  
  private isTokenValid(token: AuthToken): boolean {
    try {
      const expirationTime = this.getTokenExpirationTime(token.accessToken);
      return expirationTime > Date.now();
    } catch {
      return false;
    }
  }
  
  private getTokenExpirationTime(token: string): number {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000; // Convert to milliseconds
    } catch {
      return 0;
    }
  }
}