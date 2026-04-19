import { createReducer, on } from '@ngrx/store';
import { Warehouse, Stock } from '../../core/models/inventory.model';
import * as InventoryActions from './inventory.actions';

export interface InventoryState {
  warehouses: Warehouse[];
  stocks: Stock[];
  loading: boolean;
  error: any;
}

export const initialState: InventoryState = {
  warehouses: [],
  stocks: [],
  loading: false,
  error: null
};

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