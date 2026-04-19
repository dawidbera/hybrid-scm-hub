import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { RealtimeService } from '../core/services/realtime.service';
import { Stock, Warehouse } from '../core/models/inventory.model';
import { selectWarehouses, selectStocks, selectLoading } from '../state/inventory/inventory.selectors';
import * as InventoryActions from '../state/inventory/inventory.actions';

/**
 * Dashboard component providing a real-time overview of stock levels across warehouses.
 * Features live updates via WebSockets and warehouse-specific filtering.
 */
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  /** Observable stream of available warehouses */
  warehouses$: Observable<Warehouse[]>;
  /** Observable stream of stock levels for the selected warehouse */
  stocks$: Observable<Stock[]>;
  /** ID of the currently selected warehouse for filtering */
  selectedWarehouseId = '';
  /** Observable stream indicating if data is currently being loaded */
  loading$: Observable<boolean>;

  constructor(
    private store: Store,
    private realtimeService: RealtimeService
  ) {
    this.warehouses$ = this.store.select(selectWarehouses);
    this.stocks$ = this.store.select(selectStocks);
    this.loading$ = this.store.select(selectLoading);
  }

  /**
   * Initializes the component by loading warehouses and subscribing to real-time updates.
   */
  ngOnInit(): void {
    this.store.dispatch(InventoryActions.loadWarehouses());

    // Listen for real-time updates to refresh stock levels if the selected warehouse is affected
    this.realtimeService.getUpdates().subscribe(update => {
      if (update && update.warehouseId === this.selectedWarehouseId) {
        this.store.dispatch(InventoryActions.loadStock({ warehouseId: this.selectedWarehouseId }));
      }
    });
  }

  /**
   * Handles warehouse selection from the UI.
   * @param warehouseId The ID of the selected warehouse.
   */
  onWarehouseSelect(warehouseId: string): void {
    this.selectedWarehouseId = warehouseId;
    this.store.dispatch(InventoryActions.loadStock({ warehouseId }));
  }
}
