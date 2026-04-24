import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { DashboardComponent } from './features/dashboard.component';
import { AuditTrailComponent } from './features/audit-trail/audit-trail.component';
import { inventoryReducer } from './state/inventory/inventory.reducer';
import { InventoryEffects } from './state/inventory/inventory.effects';
import { InventoryComponent } from './features/inventory/inventory.component';
import { OrdersComponent } from './features/orders/orders.component';
import { LoginComponent } from './features/auth/login.component';
import { AuthInterceptor } from './core/interceptors/auth.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    AuditTrailComponent,
    InventoryComponent,
    OrdersComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    StoreModule.forRoot({}),
    StoreModule.forFeature('inventory', inventoryReducer),
    EffectsModule.forRoot([]),
    EffectsModule.forFeature([InventoryEffects]),
    StoreDevtoolsModule.instrument({ maxAge: 25 }),
    AppRoutingModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
