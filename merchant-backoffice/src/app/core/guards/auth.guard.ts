import { Injectable, inject } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable, map, take } from 'rxjs';
import { AuthService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    return this.authService.currentUser$.pipe(
      take(1),
      map(user => {
        if (user && this.authService.isAuthenticated) {
          // Check for required permissions if specified in route data
          const requiredPermissions = route.data?.['permissions'] as string[];
          const requiredRole = route.data?.['role'] as string;
          
          if (requiredPermissions?.length) {
            const hasPermissions = requiredPermissions.every(permission => 
              this.authService.hasPermission(permission)
            );
            if (!hasPermissions) {
              this.router.navigate(['/unauthorized']);
              return false;
            }
          }
          
          if (requiredRole && !this.authService.hasRole(requiredRole)) {
            this.router.navigate(['/unauthorized']);
            return false;
          }
          
          return true;
        } else {
          this.router.navigate(['/login'], { 
            queryParams: { returnUrl: state.url } 
          });
          return false;
        }
      })
    );
  }
}