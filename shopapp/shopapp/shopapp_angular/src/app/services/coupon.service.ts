import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { Observable } from 'rxjs';
import { CouponResponse } from '../responses/coupon.response';
import { ApiResponse } from '../responses/api.response';

@Injectable({
  providedIn: 'root'
})
export class CouponService {
  private apiCoupons = `${environment.apiBaseUrl}/coupons`;

  constructor(private http: HttpClient) { }
    //Api check mã giảm giá
    applyCoupon(code: string): Observable<ApiResponse<CouponResponse>> {
    return this.http.post<ApiResponse<CouponResponse>>(`${this.apiCoupons}/apply`, null, {
      params: {
        code: code,
      },
    });
  }
}
