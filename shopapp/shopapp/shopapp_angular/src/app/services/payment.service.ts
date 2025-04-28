import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../responses/api.response';
import { UpdatePaymentStatusDto } from '../dtos/update-payment-status.dto';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private apiPayment = `${environment.apiBaseUrl}/payments`;

  constructor(private http: HttpClient) { }
  createPaymentUrl(payload: { amount: number, language: string }): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.apiPayment}/create_payment_url`, payload);
  }
  updatePayment(statusDto: UpdatePaymentStatusDto): Observable<ApiResponse<UpdatePaymentStatusDto>> {
    return this.http.put<ApiResponse<UpdatePaymentStatusDto>>(`${this.apiPayment}/update-status`,statusDto );
  }
  getOrderIdByTransactionId(transactionId: string): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`${this.apiPayment}/transaction/${transactionId}`);
  }
}
