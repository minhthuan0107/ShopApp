import { CouponAdminResponse } from "./coupon.admin.response";

export interface CouponListAdminResponse {
    couponResponses: CouponAdminResponse[];
    totalPages: number
    totalItems: number;
    currentPage: number;
}