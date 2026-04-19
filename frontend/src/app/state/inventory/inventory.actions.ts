import { createAction, props } from '@ngrx/store';
import { Product, Warehouse, Stock } from '../../core/models/inventory.model';

export const loadWarehouses = createAction('[Inventory] Load Warehouses');
export const loadWarehousesSuccess = createAction(
  '[Inventory] Load Warehouses Success',
  props<{ warehouses: Warehouse[] }>()
);
export const loadWarehousesFailure = createAction(
  '[Inventory] Load Warehouses Failure',
  props<{ error: any }>()
);

export const loadStock = createAction(
  '[Inventory] Load Stock',
  props<{ warehouseId: string }>()
);
export const loadStockSuccess = createAction(
  '[Inventory] Load Stock Success',
  props<{ stocks: Stock[] }>()
);
export const loadStockFailure = createAction(
  '[Inventory] Load Stock Failure',
  props<{ error: any }>()
);

export const updateStock = createAction(
  '[Inventory] Update Stock',
  props<{ productId: string; warehouseId: string; quantity: number }>()
);
export const updateStockSuccess = createAction(
  '[Inventory] Update Stock Success',
  props<{ stock: Stock }>()
);
export const updateStockFailure = createAction(
  '[Inventory] Update Stock Failure',
  props<{ error: any }>()
);