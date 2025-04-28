import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OrderDto } from '../dtos/order.dto';
import { Observable } from 'rxjs';
import { ApiResponse } from '../responses/api.response';
import { environment } from '../environments/environment';
import { OrderResponse } from '../responses/order.response';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
   private apiOrder = `${environment.apiBaseUrl}/orders`;

  constructor(private http :HttpClient) { }
  //Api tạo đơn hàng
  placeOrder(userId: number , orderDto : OrderDto): Observable<ApiResponse<any>>{
    return this.http.post<ApiResponse<any>>(`${this.apiOrder}/${userId}`,orderDto)
  }
  //Api lấy thông tin đơn hàng 
  getOrderById (orderId :number): Observable<ApiResponse<OrderResponse>>{
    return this.http.get<ApiResponse<OrderResponse>>(`${this.apiOrder}/${orderId}`)
  }
  getOrdersByUserId (userId :number): Observable<ApiResponse<OrderResponse[]>>{
    return this.http.get<ApiResponse<OrderResponse[]>>(`${this.apiOrder}/user/${userId}`)
  }
  cancelOrder(orderId: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.apiOrder}/cancel/${orderId}`);
  }
}
