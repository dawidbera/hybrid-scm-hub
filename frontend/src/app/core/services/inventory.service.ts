import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product, Warehouse, Stock } from '../models/inventory.model';

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private apiUrl = '/api/inventory';

  constructor(private http: HttpClient) { }

  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/products`);
  }

  getWarehouses(): Observable<Warehouse[]> {
    return this.http.get<Warehouse[]>(`${this.apiUrl}/warehouses`);
  }

  getStockByWarehouse(warehouseId: string): Observable<Stock[]> {
    return this.http.get<Stock[]>(`${this.apiUrl}/stock/warehouse/${warehouseId}`);
  }

  updateStock(productId: string, warehouseId: string, quantity: number): Observable<Stock> {
    return this.http.post<Stock>(`${this.apiUrl}/stock`, null, {
      params: { productId, warehouseId, quantity: quantity.toString() }
    });
  }
}
