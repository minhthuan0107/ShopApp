import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { Observable } from 'rxjs';
import { ApiResponse } from '../responses/api.response';

@Injectable({
  providedIn: 'root'
})
export class StatisticsService {
  private apiRevenueYear = `${environment.apiBaseAdminUrl}/statistics/revenue/year`;
  private apiRevenueMonth = `${environment.apiBaseAdminUrl}/statistics/revenue/month`;
  private apiRevenueMonthByYear = `${environment.apiBaseAdminUrl}/statistics`;
  private apiAvailableYear = `${environment.apiBaseAdminUrl}/statistics/available/year`;
  private apiCountOrders = `${environment.apiBaseAdminUrl}/statistics/count/orders`;


  constructor(private http: HttpClient) { }
  //Api lấy doanh thu của năm hiện tại
  getTotalRevenueCurrentYear(): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(this.apiRevenueYear);
  }

  //Api lấy doanh thu của tháng hiện tại
  getTotalRevenueCurrentMonth(): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(this.apiRevenueMonth);
  }

  //Api lấy doanh thu 12 tháng theo năm truyền vào
  getMonthlyRevenueByYear(year: number): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`${this.apiRevenueMonthByYear}/${year}`);
  }

  //Api lấy danh sách năm mà có đơn hàng
  getAvailableOrderYears(): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(this.apiAvailableYear);
  }
  //Api lấy đơn hàng chưa xử lí
  countPendingOrdersWithValidPayment(): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(this.apiCountOrders);
  }

}
