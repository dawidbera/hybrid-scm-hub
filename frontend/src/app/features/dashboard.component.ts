import { Component, OnInit } from '@angular/core';
import { InventoryService } from '../core/services/inventory.service';
import { RealtimeService } from '../core/services/realtime.service';
import { Stock, Warehouse } from '../core/models/inventory.model';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  warehouses: Warehouse[] = [];
  selectedWarehouseStocks: Stock[] = [];

  constructor(
    private inventoryService: InventoryService,
    private realtimeService: RealtimeService
  ) {}

  ngOnInit(): void {
    this.inventoryService.getWarehouses().subscribe(w => this.warehouses = w);
    
    this.realtimeService.getUpdates().subscribe(update => {
      // Handle real-time stock updates
      if (update.type === 'STOCK_UPDATE') {
        const index = this.selectedWarehouseStocks.findIndex(s => s.id === update.data.id);
        if (index !== -1) {
          this.selectedWarehouseStocks[index] = update.data;
        }
      }
    });
  }

  onWarehouseSelect(warehouseId: string): void {
    this.inventoryService.getStockByWarehouse(warehouseId).subscribe(s => this.selectedWarehouseStocks = s);
  }
}
