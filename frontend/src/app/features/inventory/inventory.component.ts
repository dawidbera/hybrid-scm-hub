import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { InventoryService } from '../../core/services/inventory.service';
import { Product, Warehouse } from '../../core/models/inventory.model';
import { selectWarehouses } from '../../state/inventory/inventory.selectors';
import * as InventoryActions from '../../state/inventory/inventory.actions';

@Component({
  selector: 'app-inventory',
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.scss']
})
export class InventoryComponent implements OnInit {
  warehouses$: Observable<Warehouse[]>;
  products: Product[] = [];
  newProduct: Product = { sku: '', name: '', description: '', basePrice: 0 };
  newWarehouse: Warehouse = { name: '', location: '' };

  constructor(
    private store: Store,
    private inventoryService: InventoryService
  ) {
    this.warehouses$ = this.store.select(selectWarehouses);
  }

  ngOnInit(): void {
    this.store.dispatch(InventoryActions.loadWarehouses());
    this.inventoryService.getProducts().subscribe(p => this.products = p);
  }

  addProduct(): void {
    this.inventoryService.getProducts().subscribe(products => {
      // Check if SKU exists
      if (products.some(p => p.sku === this.newProduct.sku)) {
        alert('SKU already exists');
        return;
      }
      this.inventoryService.createProduct(this.newProduct).subscribe(() => {
        this.products.push({ ...this.newProduct });
        this.newProduct = { sku: '', name: '', description: '', basePrice: 0 };
      });
    });
  }

  addWarehouse(): void {
    this.inventoryService.createWarehouse(this.newWarehouse).subscribe(() => {
      this.store.dispatch(InventoryActions.loadWarehouses());
      this.newWarehouse = { name: '', location: '' };
    });
  }
}
