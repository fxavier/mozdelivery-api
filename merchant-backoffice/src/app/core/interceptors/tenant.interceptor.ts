import { Injectable, inject } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';

@Injectable()
export class TenantInterceptor implements HttpInterceptor {
  private readonly authService = inject(AuthService);
  
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const user = this.authService.currentUser;
    
    // Add tenant context header for merchant users
    if (user?.merchantId && this.requiresTenantContext(req)) {
      const tenantReq = req.clone({
        setHeaders: {
          'X-Tenant-ID': user.merchantId
        }
      });
      return next.handle(tenantReq);
    }
    
    return next.handle(req);
  }
  
  private requiresTenantContext(req: HttpRequest<any>): boolean {
    // Add tenant context to all API calls except auth and public endpoints
    return !req.url.includes('/auth/') && 
           !req.url.includes('/public/') &&
           req.url.includes('/api/');
  }
}