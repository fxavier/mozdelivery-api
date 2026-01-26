import { Injectable, inject } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { UserRole } from '../../shared/models/user.model';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  
  canActivate(route: ActivatedRouteSnapshot): boolean {
    const user = this.authService.currentUser;
    
    if (!user) {
      this.router.navigate(['/login']);
      return false;
    }
    
    const allowedRoles = route.data?.['roles'] as UserRole[];
    
    if (allowedRoles && !allowedRoles.includes(user.role)) {
      this.router.navigate(['/unauthorized']);
      return false;
    }
    
    return true;
  }
}