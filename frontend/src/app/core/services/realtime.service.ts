import { Injectable } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { Observable, Subject } from 'rxjs';

/**
 * Service for handling real-time updates via WebSockets (STOMP).
 * Connects to the backend WebSocket endpoint and subscribes to relevant topics.
 */
@Injectable({
  providedIn: 'root'
})
export class RealtimeService {
  private client: Client;
  private updatesSubject = new Subject<any>();

  constructor() {
    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      reconnectDelay: 5000,
      onConnect: () => {
        // Subscribe to stock updates topic
        this.client.subscribe('/topic/stock-updates', (message: Message) => {
          if (message.body) {
            this.updatesSubject.next(JSON.parse(message.body));
          }
        });
        // Subscribe to audit logs topic for real-time sync monitoring
        this.client.subscribe('/topic/audit-logs', (message: Message) => {
          if (message.body) {
            this.updatesSubject.next(JSON.parse(message.body));
          }
        });
      },
      onStompError: frame => {
        console.error('STOMP error:', frame);
      }
    });
    this.client.activate();
  }

  /**
   * Provides an observable for real-time updates from all subscribed topics.
   * @returns An Observable emitting updates as they arrive.
   */
  getUpdates(): Observable<any> {
    return this.updatesSubject.asObservable();
  }
}
