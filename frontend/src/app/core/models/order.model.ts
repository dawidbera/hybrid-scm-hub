/**
 * Possible lifecycle statuses for an order.
 */
export type OrderStatus = 'CREATED' | 'PROCESSING' | 'SHIPPED';

/**
 * Represents an individual item within an order.
 */
export interface OrderItem {
  /** ID of the product ordered */
  productId: string;
  /** ID of the warehouse from which the item will be fulfilled */
  warehouseId: string;
  /** Number of units ordered */
  quantity: number;
  /** Unit price of the product at the time of the order */
  price: number;
}

/**
 * Represents a customer order in the SCM system.
 */
export interface Order {
  /** Unique identifier for the order */
  id?: string;
  /** Name of the customer who placed the order */
  customerName: string;
  /** Current lifecycle status of the order */
  status?: OrderStatus;
  /** List of items included in this order */
  items: OrderItem[];
  /** Total value of the order (calculated) */
  total?: number;
  /** Timestamp when the order was created */
  createdAt?: string;
  /** Timestamp of the last update to the order */
  updatedAt?: string;
}
