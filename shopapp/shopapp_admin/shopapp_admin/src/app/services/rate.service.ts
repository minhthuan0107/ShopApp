import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { HttpClient } from '@angular/common/http';
import { ApiResponse } from '../responses/api.response';
import { Observable } from 'rxjs';
import { RateListAdminResponse } from '../responses/rate/rate-list-admin.response';

@Injectable({
  providedIn: 'root'
})
export class RateService {
   private apiGetAllRates = `${environment.apiBaseAdminUrl}/rates/get-all`;
   private apiDeleteComment = `${environment.apiBaseAdminUrl}/comments/delete`;
   constructor(private http: HttpClient) { }
   //Api lấy danh sách đánh giá kèm comment
   getAllRates(page: number, size: number, keyword: string = ''): Observable<ApiResponse<RateListAdminResponse>> {
     return this.http.get<ApiResponse<RateListAdminResponse>>(this.apiGetAllRates, {
       params: {
         page: page.toString(),
         size: size.toString(),
         keyword: keyword
       }
     });
   }
}
