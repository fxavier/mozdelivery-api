export interface InventoryState {
  products: any[];
  categories: any[];
  catalogs: any[];
  isLoading: boolean;
  error: string | null;
}