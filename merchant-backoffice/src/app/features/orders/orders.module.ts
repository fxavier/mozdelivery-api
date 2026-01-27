import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';

import { ordersRoutes } from './orders.routes';
import { orderReducer } from './store/order.reducer';
import { OrderEffects } from './store/order.effects';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule.forChild(ordersRoutes),
    StoreModule.forFeature('orders', orderReducer),
    EffectsModule.forFeature([OrderEffects])
  ]
})
export class OrdersModule { }