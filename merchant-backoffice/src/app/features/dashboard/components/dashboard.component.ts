import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';

import { AppState } from '../../../core/store/app.state';
import { selectCurrentUser } from '../../../core/store/auth/auth.selectors';
import { User } from '../../../shared/models/user.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  private readonly store = inject(Store<AppState>);
  
  currentUser$: Observable<User | null>;
  
  constructor() {
    this.currentUser$ = this.store.select(selectCurrentUser);
  }
  
  ngOnInit(): void {
    // Initialize dashboard data
  }
}