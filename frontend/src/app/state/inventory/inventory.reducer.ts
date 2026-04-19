import { createReducer, on } from '@ngrx/store';
import { Warehouse, Stock } from '../../core/models/inventory.model';
import * as InventoryActions from './inventory.actions';

/**
 * Interface representing the structure of the inventory state.
 */
export interface InventoryState {
  /** List of all loaded warehouses */
  warehouses: Warehouse[];
  /** List of stock levels for the currently selected context */
  stocks: Stock[];
  /** Loading status indicator */
  loading: boolean;
  /** Error object if an operation fails */
  error: any;
}

/**
 * Initial state for the inventory feature.
 */
export const initialState: InventoryState = {
  warehouses: [],
  stocks: [],
  loading: false,
  error: null
};

/**
 * Reducer for managing inventory state transitions.
 */
export const inventoryReducer = createReducer(
  initialState,
  on(InventoryActions.loadWarehouses, state => ({
    ...state,
    loading: true
  })),
  on(InventoryActions.loadWarehousesSuccess, (state, { warehouses }) => ({
    ...state,
    warehouses,
    loading: false
  })),
  on(InventoryActions.loadWarehousesFailure, (state, { error }) => ({
    ...state,
    error,
    loading: false
  })),
  on(InventoryActions.loadStock, state => ({
    ...state,
    loading: true
  })),
  on(InventoryActions.loadStockSuccess, (state, { stocks }) => ({
    ...state,
    stocks,
    loading: false
  })),
  on(InventoryActions.loadStockFailure, (state, { error }) => ({
    ...state,
    error,
    loading: false
  })),
  on(InventoryActions.updateStockSuccess, (state, { stock }) => ({
    ...state,
    stocks: state.stocks.map(s => s.id === stock.id ? stock : s)
  }))
);
