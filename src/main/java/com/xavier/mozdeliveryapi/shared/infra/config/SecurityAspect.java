package com.xavier.mozdeliveryapi.shared.infra.config;

import com.xavier.mozdeliveryapi.shared.application.usecase.PermissionService;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Permission;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserRole;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Aspect for handling custom security annotations.
 */
@Aspect
@Component
public class SecurityAspect {
    
    private final PermissionService permissionService;
    
    public SecurityAspect(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
    
    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        Permission requiredPermission = requirePermission.value();
        
        if (requirePermission.checkMerchantAccess()) {
            MerchantId merchantId = extractMerchantId(joinPoint);
            if (merchantId != null) {
                if (!permissionService.hasPermissionForMerchant(requiredPermission, merchantId)) {
                    throw new AccessDeniedException("Access denied: insufficient permissions for merchant " + merchantId);
                }
            } else if (!permissionService.isAdmin()) {
                throw new AccessDeniedException("Access denied: merchant ID required or admin role needed");
            }
        } else {
            if (!permissionService.hasPermission(requiredPermission)) {
                throw new AccessDeniedException("Access denied: missing permission " + requiredPermission.getCode());
            }
        }
    }
    
    @Before("@annotation(requireRole)")
    public void checkRole(JoinPoint joinPoint, RequireRole requireRole) {
        UserRole[] requiredRoles = requireRole.value();
        UserRole currentRole = permissionService.getCurrentUserRole();
        
        boolean hasRequiredRole = false;
        for (UserRole requiredRole : requiredRoles) {
            if (currentRole == requiredRole) {
                hasRequiredRole = true;
                break;
            }
        }
        
        if (!hasRequiredRole) {
            throw new AccessDeniedException("Access denied: insufficient role. Required: " + 
                java.util.Arrays.toString(requiredRoles) + ", Current: " + currentRole);
        }
        
        if (requireRole.checkMerchantAccess()) {
            MerchantId merchantId = extractMerchantId(joinPoint);
            if (merchantId != null) {
                if (!permissionService.canAccessMerchant(merchantId)) {
                    throw new AccessDeniedException("Access denied: cannot access merchant " + merchantId);
                }
            } else if (!permissionService.isAdmin()) {
                throw new AccessDeniedException("Access denied: merchant ID required or admin role needed");
            }
        }
    }
    
    private MerchantId extractMerchantId(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getType() == MerchantId.class) {
                return (MerchantId) args[i];
            }
            // Also check for String parameters that might be merchant IDs
            if (parameters[i].getType() == String.class && 
                (parameters[i].getName().toLowerCase().contains("merchant") ||
                 parameters[i].getName().toLowerCase().contains("tenantid"))) {
                try {
                    return MerchantId.of((String) args[i]);
                } catch (Exception e) {
                    // Not a valid merchant ID
                }
            }
        }
        
        return null;
    }
}