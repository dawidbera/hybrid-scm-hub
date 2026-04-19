import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { InventoryService } from '../../core/services/inventory.service';
import { Stock } from '../../core/models/inventory.model';
import * as InventoryActions from './inventory.actions';
import { catchError, map, mergeMap } from 'rxjs/operators';
import { of } from 'rxjs';

/**
 * Effects for handling side-effects related to inventory state.
 * Orchestrates calls to InventoryService and dispatches corresponding actions.
 */
@Injectable()
export class InventoryEffects {
  /**
   * Effect to handle loading warehouses.
   */
  loadWarehouses$ = createEffect(() =>
    this.actions$.pipe(
      ofType(InventoryActions.loadWarehouses),
      mergeMap(() =>
        this.inventoryService.getWarehouses().pipe(
          map(warehouses => InventoryActions.loadWarehousesSuccess({ warehouses })),
          catchError(error => of(InventoryActions.loadWarehousesFailure({ error })))
        )
      )
    )
  );

  /**
   * Effect to handle loading stock levels for a warehouse.
   */
  loadStock$ = createEffect(() =>
    this.actions$.pipe(
      ofType(InventoryActions.loadStock),
      mergeMap(action =>
        this.inventoryService.getStockByWarehouse(action.warehouseId).pipe(
          map(stocks => InventoryActions.loadStockSuccess({ stocks })),
          catchError(error => of(InventoryActions.loadStockFailure({ error })))
        )
      )
    )
  );

  /**
   * Effect to handle updating stock levels.
   */
  updateStock$ = createEffect(() =>
    this.actions$.pipe(
      ofType(InventoryActions.updateStock),
      mergeMap(action =>
        this.inventoryService.updateStock(action.productId, action.warehouseId, action.quantity).pipe(
          map((response: any) => InventoryActions.updateStockSuccess({ stock: response as Stock })),
          catchError(error => of(InventoryActions.updateStockFailure({ error })))
        )
      )
    )
  );

  constructor(
    private actions$: Actions,
    private inventoryService: InventoryService
  ) {}
}
