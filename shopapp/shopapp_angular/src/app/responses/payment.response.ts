export interface PaymentResponse{
  payment_id: number;
  order_id: number;
  is_buy_now: boolean;
  amount: number;
  payment_method: string;
  transaction_id: string;
  status: string;
}