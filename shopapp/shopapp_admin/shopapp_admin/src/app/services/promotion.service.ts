import { CouponAdminResponse } from './../responses/coupon/coupon.admin.response';
import { ApiResponse } from './../responses/api.response';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { Observable } from 'rxjs';
import { CouponListAdminResponse } from '../responses/coupon/coupon-list-admin.response';
import { CreateCouponDto } from '../dtos/create.coupon.dto';

@Injectable({
  providedIn: 'root'
})
export class PromotionService {
  private apiGetAllCoupons = `${environment.apiBaseAdminUrl}/coupons/get-all`;
  private apiCreateCoupon = `${environment.apiBaseAdminUrl}/coupons/create`;
  private apiToggleCoupon = `${environment.apiBaseAdminUrl}/coupons/toggle`;
  private apiSendCoupon = `${environment.apiBaseAdminUrl}/coupons/send-to-users`;
  constructor(private http: HttpClient) { }
  //Api lấy danh sách comment cha và các replies của comment cha
  getAllCoupons(page: number, size: number, keyword: string = ''): Observable<ApiResponse<CouponListAdminResponse>> {
    return this.http.get<ApiResponse<CouponListAdminResponse>>(this.apiGetAllCoupons, {
      params: {
        page: page.toString(),
        size: size.toString(),
        keyword: keyword
      }
    });
  }
  //Api để gửi request tạo mới coupon
  createCoupon(createCouponDto: CreateCouponDto): Observable<any> {
    return this.http.post(this.apiCreateCoupon, createCouponDto)
  }
  //Api bật / tắt coupon
  toggleCouponStatus(couponId: number): Observable<ApiResponse<CouponAdminResponse>> {
    return this.http.patch<ApiResponse<CouponAdminResponse>>(`${this.apiToggleCoupon}/${couponId}`, {})
  }
  sendCouponToAllUsers(couponCode: string): Observable<any> {
    return this.http.post(`${this.apiToggleCoupon}/coupons/send-to-all`, { couponCode });

  }
  sendCouponToSelectedUsers(couponCode: string, userIds: number[]): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.apiSendCoupon}`, { couponCode,userIds});
  }
}
