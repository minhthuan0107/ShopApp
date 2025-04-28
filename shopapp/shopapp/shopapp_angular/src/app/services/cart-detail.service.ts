import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { ApiResponse } from '../responses/api.response';
import { CartDetail } from '../models/cart-detail';
import { Observable } from 'rxjs';
import { CartDetailsUpdate } from '../dtos/cartdetails-update.dto';

@Injectable({
  providedIn: 'root'
})
export class CartDetailService {
  private apiCartDetail = `${environment.apiBaseUrl}/cartdetails`;

  constructor(private http: HttpClient) { }
  //
  getCartDetailsByUserId(userId: number) {
    return this.http.get<ApiResponse<CartDetail[]>>(`${this.apiCartDetail}/${userId}`);
  }
  //Api xóa chi tiết sản phẩm theo Id
  deleteCartDetailById(userId: number, cartDetailId: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.apiCartDetail}/${userId}/${cartDetailId}`);
  }
  //Api cập nhật chi tiết sản phẩm (số lượng, tổng tiền)
  updateCartDetails(userId: number, cartDetails: CartDetailsUpdate[]): Observable<ApiResponse<any>> {
    return this.http.put<ApiResponse<any>>(`${this.apiCartDetail}/${userId}`, cartDetails);
  }


}
