import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZonelessChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, HTTP_INTERCEPTORS } from '@angular/common/http';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { provideStore } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';
import { provideStoreDevtools } from '@ngrx/store-devtools';
import { provideRouterStore } from '@ngrx/router-store';

import { routes } from './app.routes';
import { authReducer } from './core/store/auth/auth.reducer';
import { AuthEffects } from './core/store/auth/auth.effects';
import { AuthInterceptor } from './core/interceptors/auth.interceptor';
import { TenantInterceptor } from './core/interceptors/tenant.interceptor';
import { environment } from '../environments/environment';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideRouter(routes),
    provideClientHydration(withEventReplay()),
    provideHttpClient(),
    
    // NgRx Store Configuration
    provideStore({
      auth: authReducer
    }),
    provideEffects([AuthEffects]),
    provideRouterStore(),
    
    // Store DevTools (only in development)
    ...(environment.features.enableDevTools ? [
      provideStoreDevtools({
        maxAge: 25,
        logOnly: environment.production,
        autoPause: true,
        trace: false,
        traceLimit: 75
      })
    ] : []),
    
    // HTTP Interceptors
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TenantInterceptor,
      multi: true
    }
  ]
};
