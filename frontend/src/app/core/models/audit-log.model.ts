/**
 * Represents a synchronization log entry between On-Premise and Cloud environments.
 */
export interface AuditLog {
  /** Unique identifier for the sync log entry */
  id?: string;
  /** Name of the entity being synchronized (e.g., 'Order', 'Product') */
  entityName: string;
  /** Unique identifier of the specific entity instance */
  entityId: string;
  /** Current status of the synchronization (e.g., 'SUCCESS', 'FAILURE') */
  status: string;
  /** Detailed error message if the synchronization failed */
  errorMessage?: string;
  /** Timestamp when the synchronization event occurred */
  syncTimestamp?: string;
}
