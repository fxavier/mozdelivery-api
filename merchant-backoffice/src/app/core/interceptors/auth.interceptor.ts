import { Injectable, inject } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, filter, take, switchMap } from 'rxjs/operators';
import { AuthService } from '../auth/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private readonly authService = inject(AuthService);
  private isRefreshing = false;
  private refreshTokenSubject = new BehaviorSubject<any>(null);
  
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Add auth header if user is authenticated
    const authReq = this.addAuthHeader(req);
    
    return next.handle(authReq).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && this.authService.isAuthenticated) {
          return this.handle401Error(authReq, next);
        }
        return throwError(() => error);
      })
    );
  }
  
  private addAuthHeader(req: HttpRequest<any>): HttpRequest<any> {
    const token = this.authService.token;
    
    if (token?.accessToken && !this.isAuthRequest(req)) {
      return req.clone({
        setHeaders: {
          Authorization: `${token.tokenType} ${token.accessToken}`
        }
      });
    }
    
    return req;
  }
  
  private handle401Error(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);
      
      return this.authService.refreshToken().pipe(
        switchMap((token) => {
          this.isRefreshing = false;
          this.refreshTokenSubject.next(token);
          
          const authReq = req.clone({
            setHeaders: {
              Authorization: `${token.tokenType} ${token.accessToken}`
            }
          });
          
          return next.handle(authReq);
        }),
        catchError((error) => {
          this.isRefreshing = false;
          this.authService.logout();
          return throwError(() => error);
        })
      );
    } else {
      return this.refreshTokenSubject.pipe(
        filter(token => token != null),
        take(1),
        switchMap((token) => {
          const authReq = req.clone({
            setHeaders: {
              Authorization: `${token.tokenType} ${token.accessToken}`
            }
          });
          return next.handle(authReq);
        })
      );
    }
  }
  
  private isAuthRequest(req: HttpRequest<any>): boolean {
    return req.url.includes('/auth/login') || 
           req.url.includes('/auth/refresh') || 
           req.url.includes('/auth/logout');
  }
}