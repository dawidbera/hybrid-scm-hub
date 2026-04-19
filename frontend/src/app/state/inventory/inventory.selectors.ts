import { createFeatureSelector, createSelector } from '@ngrx/store';
import { InventoryState } from './inventory.reducer';

/**
 * Feature selector for the inventory state.
 */
export const selectInventoryState = createFeatureSelector<InventoryState>('inventory');

/**
 * Selector for retrieving the list of warehouses.
 */
export const selectWarehouses = createSelector(
  selectInventoryState,
  state => state.warehouses
);

/**
 * Selector for retrieving the list of stocks.
 */
export const selectStocks = createSelector(
  selectInventoryState,
  state => state.stocks
);

/**
 * Selector for retrieving the loading status.
 */
export const selectLoading = createSelector(
  selectInventoryState,
  state => state.loading
);

/**
 * Selector for retrieving the last error.
 */
export const selectError = createSelector(
  selectInventoryState,
  state => state.error
);
