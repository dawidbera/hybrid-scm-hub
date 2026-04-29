import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order } from '../models/order.model';

/**
 * Service for managing order-related operations, such as creating and retrieving orders.
 */
@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = '/api/orders';

  constructor(private http: HttpClient) {}

  /**
   * Submits a new order to the backend.
   * @param order The order data to be created.
   * @returns An Observable of the created Order object.
   */
  createOrder(order: Order): Observable<Order> {
    return this.http.post<Order>(this.apiUrl, order);
  }

  /**
   * Retrieves a list of all orders from the system.
   * @returns An Observable of an array of Order objects.
   */
  getOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(this.apiUrl);
  }

  /**
   * Updates the status of an existing order.
   * @param orderId The ID of the order to update.
   * @param status The new status (CREATED, PROCESSING, SHIPPED).
   * @returns An Observable of the updated Order object.
   */
  updateOrderStatus(orderId: string, status: string): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/${orderId}/status`, null, {
      params: { status }
    });
  }

  /**
   * Downloads the JSON document for a specific order from S3.
   * @param orderId The ID of the order.
   * @returns An Observable of the file Blob.
   */
  downloadOrderDocument(orderId: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${orderId}/document`, {
      responseType: 'blob'
    });
  }
}
