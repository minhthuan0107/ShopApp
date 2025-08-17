import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../responses/api.response';
import { UpdatePaymentStatusDto } from '../dtos/update-payment-status.dto';
import { UpdateTransactionDto } from '../dtos/update-transaction.dto';
import { PaymentResponse } from '../responses/payment.response';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private apiPayment = `${environment.apiBaseUrl}/payments`;

  constructor(private http: HttpClient) { }
  createPaymentUrl(payload: { amount: number,language: string}): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.apiPayment}/create_payment_url`, payload);
  }
  updatePayment(statusDto: UpdatePaymentStatusDto): Observable<ApiResponse<PaymentResponse>> {
    return this.http.put<ApiResponse<PaymentResponse>>(`${this.apiPayment}/update-status`,statusDto );
  }
  updateTransactionId(dto: UpdateTransactionDto): Observable<ApiResponse<any>> {
    return this.http.patch<ApiResponse<any>>(`${this.apiPayment}/update-transaction`,dto );
  }
  getOrderIdByTransactionId(transactionId: string): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`${this.apiPayment}/transaction/${transactionId}`);
  }
 
}
