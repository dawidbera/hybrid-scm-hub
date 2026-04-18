import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { DashboardComponent } from './features/dashboard.component';
import { AuditTrailComponent } from './features/audit-trail/audit-trail.component';

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    AuditTrailComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
