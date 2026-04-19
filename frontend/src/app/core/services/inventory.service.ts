import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product, Warehouse, Stock } from '../models/inventory.model';

/**
 * Service for managing inventory-related operations, including products, warehouses, and stock levels.
 */
@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private apiUrl = '/api/inventory';

  constructor(private http: HttpClient) { }

  /**
   * Retrieves all available products.
   * @returns An Observable of an array of Product objects.
   */
  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/products`);
  }

  /**
   * Retrieves all registered warehouses.
   * @returns An Observable of an array of Warehouse objects.
   */
  getWarehouses(): Observable<Warehouse[]> {
    return this.http.get<Warehouse[]>(`${this.apiUrl}/warehouses`);
  }

  /**
   * Retrieves stock levels for a specific warehouse.
   * @param warehouseId The unique identifier of the warehouse.
   * @returns An Observable of an array of Stock objects for the specified warehouse.
   */
  getStockByWarehouse(warehouseId: string): Observable<Stock[]> {
    return this.http.get<Stock[]>(`${this.apiUrl}/stock/warehouse/${warehouseId}`);
  }

  /**
   * Creates a new product in the system.
   * @param product The product object to be created.
   * @returns An Observable of the created Product object.
   */
  createProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(`${this.apiUrl}/products`, product);
  }

  /**
   * Registers a new warehouse in the system.
   * @param warehouse The warehouse object to be created.
   * @returns An Observable of the created Warehouse object.
   */
  createWarehouse(warehouse: Warehouse): Observable<Warehouse> {
    return this.http.post<Warehouse>(`${this.apiUrl}/warehouses`, warehouse);
  }

  /**
   * Updates the stock level for a product at a specific warehouse.
   * @param productId The ID of the product.
   * @param warehouseId The ID of the warehouse.
   * @param quantity The new absolute quantity of stock.
   * @returns An Observable of the updated Stock object.
   */
  updateStock(productId: string, warehouseId: string, quantity: number): Observable<Stock> {
    return this.http.post<Stock>(`${this.apiUrl}/stock`, null, {
      params: { productId, warehouseId, quantity: quantity.toString() }
    });
  }

  /**
   * Searches for stock levels based on a query, warehouse, and minimum quantity.
   * @param query Search term (e.g., product name or SKU).
   * @param warehouseId Optional filter by warehouse.
   * @param minQuantity Optional filter for minimum stock quantity.
   * @returns An Observable of an array containing matched stock information.
   */
  searchStock(query: string, warehouseId?: string, minQuantity?: number): Observable<any[]> {
    let params: any = {};
    if (query) params.query = query;
    if (warehouseId) params.warehouseId = warehouseId;
    if (minQuantity !== undefined) params.minQuantity = minQuantity.toString();
    return this.http.get<any[]>(`${this.apiUrl}/search`, { params });
  }
}
