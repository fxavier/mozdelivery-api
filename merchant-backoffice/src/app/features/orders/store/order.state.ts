export interface OrderState {
  orders: any[];
  selectedOrder: any | null;
  isLoading: boolean;
  error: string | null;
  filters: {
    status: string | null;
    dateRange: any | null;
    searchTerm: string;
  };
}