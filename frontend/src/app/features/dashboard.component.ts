import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable, of } from 'rxjs';
import { InventoryService } from '../core/services/inventory.service';
import { RealtimeService } from '../core/services/realtime.service';
import { Stock, Warehouse } from '../core/models/inventory.model';
import { selectWarehouses, selectStocks, selectLoading } from '../state/inventory/inventory.selectors';
import * as InventoryActions from '../state/inventory/inventory.actions';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  warehouses$: Observable<Warehouse[]>;
  stocks$: Observable<Stock[]> = of([]);
  loading$: Observable<boolean>;

  constructor(
    private store: Store,
    private inventoryService: InventoryService,
    private realtimeService: RealtimeService
  ) {
    this.warehouses$ = this.store.select(selectWarehouses);
    this.stocks$ = this.store.select(selectStocks);
    this.loading$ = this.store.select(selectLoading);
  }

  ngOnInit(): void {
    this.store.dispatch(InventoryActions.loadWarehouses());
    
    this.realtimeService.getUpdates().subscribe(update => {
      // Handle real-time stock updates
      if (update.type === 'STOCK_UPDATE') {
        // Dispatch action to update state
      }
    });
  }

  onWarehouseSelect(warehouseId: string): void {
    this.store.dispatch(InventoryActions.loadStock({ warehouseId }));
  }
}
