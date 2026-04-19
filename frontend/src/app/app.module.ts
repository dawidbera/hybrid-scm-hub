import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
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

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    AuditTrailComponent,
    InventoryComponent
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
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
