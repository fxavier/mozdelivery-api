package com.xavier.mozdeliveryapi.shared.infra.config;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to require specific roles for method access.
 * Can be used on methods or classes.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    
    /**
     * Required roles for access. User must have at least one of these roles.
     */
    UserRole[] value();
    
    /**
     * Whether to check merchant access (for merchant-specific operations).
     * If true, the method must have a MerchantId parameter or the user must be an admin.
     */
    boolean checkMerchantAccess() default false;
}