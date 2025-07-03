import { PaymentAdminResponse } from "../payment.admin.response";
import { OrderDetailAdminResponse } from './order-detail-admin.response';

export interface OrderAdminResponse {
    order_id: number;
    user_id:number;
    full_name: string;
    email: string;
    phone_number: string;
    note: string;
    total_price: number;
    address: string;
    order_date: string;
    order_details: OrderDetailAdminResponse[];
    payment: PaymentAdminResponse;
    status: string;
    shipping_method: string;
    tracking_number: string;
}