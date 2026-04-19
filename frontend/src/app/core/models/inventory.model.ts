/**
 * Represents a product in the SCM system.
 */
export interface Product {
  /** Unique identifier for the product */
  id?: string;
  /** Stock Keeping Unit, a unique code for product identification */
  sku: string;
  /** Display name of the product */
  name: string;
  /** Detailed description of the product's characteristics */
  description: string;
  /** Standard price of the product before any discounts */
  basePrice: number;
}

/**
 * Represents a physical warehouse location.
 */
export interface Warehouse {
  /** Unique identifier for the warehouse */
  id?: string;
  /** Name of the warehouse facility */
  name: string;
  /** Physical address or geographical location of the warehouse */
  location: string;
}

/**
 * Represents the inventory level of a specific product at a specific warehouse.
 */
export interface Stock {
  /** Unique identifier for the stock record */
  id?: string;
  /** ID of the product this stock belongs to */
  productId: string;
  /** ID of the warehouse where this stock is located */
  warehouseId: string;
  /** Current quantity of the product available in the warehouse */
  quantity: number;
  /** Timestamp of the last stock level update */
  lastUpdated?: string;
}
