import { createAction, props } from '@ngrx/store';
import { Product, Warehouse, Stock } from '../../core/models/inventory.model';

/**
 * Action to initiate loading of all warehouses.
 */
export const loadWarehouses = createAction('[Inventory] Load Warehouses');

/**
 * Action dispatched when warehouses are successfully loaded.
 */
export const loadWarehousesSuccess = createAction(
  '[Inventory] Load Warehouses Success',
  props<{ warehouses: Warehouse[] }>()
);

/**
 * Action dispatched when warehouse loading fails.
 */
export const loadWarehousesFailure = createAction(
  '[Inventory] Load Warehouses Failure',
  props<{ error: any }>()
);

/**
 * Action to initiate loading of stock levels for a specific warehouse.
 */
export const loadStock = createAction(
  '[Inventory] Load Stock',
  props<{ warehouseId: string }>()
);

/**
 * Action dispatched when stock levels are successfully loaded.
 */
export const loadStockSuccess = createAction(
  '[Inventory] Load Stock Success',
  props<{ stocks: Stock[] }>()
);

/**
 * Action dispatched when stock loading fails.
 */
export const loadStockFailure = createAction(
  '[Inventory] Load Stock Failure',
  props<{ error: any }>()
);

/**
 * Action to initiate a stock level update.
 */
export const updateStock = createAction(
  '[Inventory] Update Stock',
  props<{ productId: string; warehouseId: string; quantity: number }>()
);

/**
 * Action dispatched when a stock update is successful.
 */
export const updateStockSuccess = createAction(
  '[Inventory] Update Stock Success',
  props<{ stock: Stock }>()
);

/**
 * Action dispatched when a stock update fails.
 */
export const updateStockFailure = createAction(
  '[Inventory] Update Stock Failure',
  props<{ error: any }>()
);
