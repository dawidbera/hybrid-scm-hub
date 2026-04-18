export interface Product {
  id?: string;
  sku: string;
  name: string;
  description: string;
  basePrice: number;
}

export interface Warehouse {
  id?: string;
  name: string;
  location: string;
}

export interface Stock {
  id?: string;
  productId: string;
  warehouseId: string;
  quantity: number;
  lastUpdated?: string;
}
