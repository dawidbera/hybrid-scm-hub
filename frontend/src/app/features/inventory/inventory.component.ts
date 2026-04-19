import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { InventoryService } from '../../core/services/inventory.service';
import { Product, Warehouse } from '../../core/models/inventory.model';
import { selectWarehouses } from '../../state/inventory/inventory.selectors';
import * as InventoryActions from '../../state/inventory/inventory.actions';

/**
 * Component for managing master data (Products, Warehouses) and manual stock updates.
 * Includes advanced searching and filtering of inventory levels.
 */
@Component({
  selector: 'app-inventory',
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.scss']
})
export class InventoryComponent implements OnInit {
  /** Observable stream of available warehouses from the store */
  warehouses$: Observable<Warehouse[]>;
  /** List of all products in the system */
  products: Product[] = [];
  /** Selected product ID for manual stock updates */
  selectedProductId: string = '';
  /** Selected warehouse ID for manual stock updates */
  selectedWarehouseId: string = '';
  /** New quantity to set during manual stock updates */
  updateQuantity: number = 0;
  /** Model for creating a new product */
  newProduct: Product = { sku: '', name: '', description: '', basePrice: 0 };
  /** Model for creating a new warehouse */
  newWarehouse: Warehouse = { name: '', location: '' };

  /** Advanced search query string */
  searchQuery: string = '';
  /** Warehouse ID filter for advanced search */
  filterWarehouseId: string = '';
  /** Minimum quantity filter for advanced search */
  minQuantity: number = 0;
  /** Search results containing matched stock items */
  stockResults: any[] = [];

  constructor(
    private store: Store,
    private inventoryService: InventoryService
  ) {
    this.warehouses$ = this.store.select(selectWarehouses);
  }

  /**
   * Initializes the component by loading warehouses and products.
   */
  ngOnInit(): void {
    this.store.dispatch(InventoryActions.loadWarehouses());
    this.inventoryService.getProducts().subscribe(p => this.products = p);
  }

  /**
   * Submits a new product for creation after validating that the SKU is unique.
   */
  addProduct(): void {
    this.inventoryService.getProducts().subscribe(products => {
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

  /**
   * Submits a new warehouse for registration and refreshes the warehouse list.
   */
  addWarehouse(): void {
    this.inventoryService.createWarehouse(this.newWarehouse).subscribe(() => {
      this.store.dispatch(InventoryActions.loadWarehouses());
      this.newWarehouse = { name: '', location: '' };
    });
  }

  /**
   * Executes a manual stock update for the selected product and warehouse.
   */
  updateStock(): void {
    if (!this.selectedProductId || !this.selectedWarehouseId) {
      alert('Select a product and a warehouse first');
      return;
    }
    this.inventoryService.updateStock(this.selectedProductId, this.selectedWarehouseId, this.updateQuantity)
      .subscribe(() => {
        alert('Stock updated successfully');
        this.selectedProductId = '';
        this.selectedWarehouseId = '';
        this.updateQuantity = 0;
      });
  }

  /**
   * Performs an advanced search for stock levels based on current UI filters.
   */
  searchStock(): void {
    this.inventoryService.searchStock(this.searchQuery, this.filterWarehouseId || undefined, this.minQuantity)
      .subscribe(results => {
        this.stockResults = results;
      });
  }
}
