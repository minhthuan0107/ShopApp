export interface Coupon {
    id: number;
    code: string;
    type: string;
    value: number;
    min_order_value:number;
    quantity: number;
    expiry_date: Date;
    is_active: boolean;
}