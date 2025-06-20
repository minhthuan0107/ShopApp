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
  //Api lấy danh sách chi tiết giỏ hàng
  getCartDetailsByUserId(): Observable<ApiResponse<CartDetail[]>> {
    return this.http.get<ApiResponse<CartDetail[]>>(`${this.apiCartDetail}`);
  }
  //Api xóa chi tiết sản phẩm theo Id
  deleteCartDetailById(cartDetailId: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.apiCartDetail}/${cartDetailId}`);
  }
  //Api cập nhật chi tiết sản phẩm (số lượng, tổng tiền)
  updateCartDetails(cartDetails: CartDetailsUpdate[]): Observable<ApiResponse<any>> {
    return this.http.put<ApiResponse<any>>(`${this.apiCartDetail}`, cartDetails);
  }


}
