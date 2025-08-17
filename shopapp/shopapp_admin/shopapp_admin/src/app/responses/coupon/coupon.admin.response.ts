export interface CouponAdminResponse {
    id: number;
    code: string;
    type: string;
    value : number;
    quantity: number;
    min_order_value: number
    expiry_date: Date;
    is_active: boolean;
    is_sent:boolean;
   }