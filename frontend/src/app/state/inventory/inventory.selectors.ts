import { createFeatureSelector, createSelector } from '@ngrx/store';
import { InventoryState } from './inventory.reducer';

export const selectInventoryState = createFeatureSelector<InventoryState>('inventory');

export const selectWarehouses = createSelector(
  selectInventoryState,
  state => state.warehouses
);

export const selectStocks = createSelector(
  selectInventoryState,
  state => state.stocks
);

export const selectLoading = createSelector(
  selectInventoryState,
  state => state.loading
);

export const selectError = createSelector(
  selectInventoryState,
  state => state.error
);