import { OrderDetailDto } from "./order-detail.dto";
import { PaymentDto } from "./payment.dto";
export interface OrderDto {
  full_name: string;
  email: string;
  phone_number: string;
  note: string;
  address: string;
  address_detail:string
  order_details: OrderDetailDto[];
  payment: PaymentDto;
  is_buy_now: boolean;
  coupon_code: string;
  province: string,
  district: string,
  ward: string,
  shipping_method: string
}