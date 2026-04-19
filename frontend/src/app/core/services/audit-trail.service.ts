import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuditLog } from '../models/audit-log.model';

/**
 * Service for retrieving audit trail logs from the backend.
 */
@Injectable({
  providedIn: 'root'
})
export class AuditTrailService {
  private apiUrl = '/api/audit-trail';

  constructor(private http: HttpClient) {}

  /**
   * Fetches all audit logs.
   * @returns An Observable of AuditLog array.
   */
  getAuditLogs(): Observable<AuditLog[]> {
    return this.http.get<AuditLog[]>(this.apiUrl);
  }
}
