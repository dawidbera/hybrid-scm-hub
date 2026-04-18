import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './features/dashboard.component';
import { AuditTrailComponent } from './features/audit-trail/audit-trail.component';

const routes: Routes = [
  { path: '', component: DashboardComponent },
  { path: 'audit-trail', component: AuditTrailComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
