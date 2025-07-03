import { OrderAdminResponse } from './order-admin.response';
export interface OrderListAdminResponse {
    orderResponses: OrderAdminResponse[];
    totalPages: number
    totalItems: number;
    currentPage: number;
}