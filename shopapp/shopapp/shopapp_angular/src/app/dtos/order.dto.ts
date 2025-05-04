import { OrderDetailDto } from "./order-detail.dto";
import { PaymentDto } from "./payment.dto";
export interface OrderDto {
    full_name: string;
    email: string;
    phone_number: string;
    note: string;
    total_price: number;
    address: string;
    order_details: OrderDetailDto[];
    payment: PaymentDto;
    is_buy_now: boolean;
  }