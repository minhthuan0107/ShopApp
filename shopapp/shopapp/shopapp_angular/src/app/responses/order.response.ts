import { OrderDetailResponse } from "./order-detail.response";
import { PaymentResponse } from "./payment.response";

export interface OrderResponse {
    order_id: number;
    full_name: string;
    email: string;
    phone_number: string;
    note: string;
    total_price: number;
    address: string;
    order_date: string;
    order_details: OrderDetailResponse[];
    payment: PaymentResponse;
    status: string;
}