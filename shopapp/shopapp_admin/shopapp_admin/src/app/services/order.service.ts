import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { Observable } from 'rxjs';
import { OrderListAdminResponse } from '../responses/order/order-list-admin.response';
import { ApiResponse } from '../responses/api.response';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiGetAllOrders = `${environment.apiBaseAdminUrl}/orders/get-all`;
  private apiCancelOrder = `${environment.apiBaseUrl}/orders/cancel`;
  private apiUpdateOrder = `${environment.apiBaseAdminUrl}/orders/status`;
  constructor(private http: HttpClient) { }
  //Api lấy danh sách tất cả đơn hàng
  getAllOrders(page: number, size: number, keyword: string = ''): Observable<OrderListAdminResponse> {
    return this.http.get<OrderListAdminResponse>(this.apiGetAllOrders, {
      params: {
        page: page.toString(),
        size: size.toString(),
        keyword: keyword
      }
    });
  }
  //Api hủy đơn hàng
  cancelOrder(orderId: number): Observable<ApiResponse<any>> {
    return this.http.patch<ApiResponse<any>>(`${this.apiCancelOrder}/${orderId}`, {});
  }
  //Api cập nhật trạng thái đơn hàng
  updateOrderStatus(orderId: number): Observable<ApiResponse<any>> {
    return this.http.patch<ApiResponse<any>>(`${this.apiUpdateOrder}/${orderId}`, {});
  }
}
