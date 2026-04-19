import { Component, OnInit } from '@angular/core';
import { Order, OrderItem } from '../../core/models/order.model';
import { OrderService } from '../../core/services/order.service';
import { InventoryService } from '../../core/services/inventory.service';
import { Product, Warehouse } from '../../core/models/inventory.model';

/**
 * Component for managing customer orders.
 * Supports creating new orders with multiple items and viewing the order history.
 */
@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.scss']
})
export class OrdersComponent implements OnInit {
  /** List of all orders retrieved from the system */
  orders: Order[] = [];
  /** List of products available for selection in new orders */
  products: Product[] = [];
  /** List of warehouses available for fulfillment selection */
  warehouses: Warehouse[] = [];
  /** Model for creating a new order */
  newOrder: Order = {
    customerName: '',
    items: [{ productId: '', warehouseId: '', quantity: 1, price: 0 }]
  };

  constructor(
    private orderService: OrderService,
    private inventoryService: InventoryService
  ) {}

  /**
   * Initializes the component by loading orders, products, and warehouses.
   */
  ngOnInit(): void {
    this.loadOrders();
    this.inventoryService.getProducts().subscribe(products => {
      this.products = products;
      if (this.products.length && !this.newOrder.items[0].productId) {
        this.newOrder.items[0].productId = this.products[0].id || '';
      }
    });
    this.inventoryService.getWarehouses().subscribe(warehouses => {
      this.warehouses = warehouses;
      if (this.warehouses.length && !this.newOrder.items[0].warehouseId) {
        this.newOrder.items[0].warehouseId = this.warehouses[0].id || '';
      }
    });
  }

  /**
   * Fetches the current list of orders from the backend.
   */
  loadOrders(): void {
    this.orderService.getOrders().subscribe(orders => {
      this.orders = orders;
    });
  }

  /**
   * Adds a new empty item line to the order creation form.
   */
  addOrderItem(): void {
    this.newOrder.items.push({ productId: '', warehouseId: '', quantity: 1, price: 0 });
  }

  /**
   * Removes an item line from the order creation form.
   * @param index The index of the item to remove.
   */
  removeOrderItem(index: number): void {
    this.newOrder.items.splice(index, 1);
  }

  /**
   * Submits the new order to the backend and resets the form.
   */
  submitOrder(): void {
    this.orderService.createOrder(this.newOrder).subscribe(() => {
      this.newOrder = { customerName: '', items: [{ productId: this.products[0]?.id || '', warehouseId: this.warehouses[0]?.id || '', quantity: 1, price: 0 }] };
      this.loadOrders();
    });
  }
}
