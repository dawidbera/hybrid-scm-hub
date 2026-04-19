import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './features/dashboard.component';
import { AuditTrailComponent } from './features/audit-trail/audit-trail.component';
import { InventoryComponent } from './features/inventory/inventory.component';
import { OrdersComponent } from './features/orders/orders.component';

const routes: Routes = [
  { path: '', component: DashboardComponent },
  { path: 'audit-trail', component: AuditTrailComponent },
  { path: 'inventory', component: InventoryComponent },
  { path: 'orders', component: OrdersComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
