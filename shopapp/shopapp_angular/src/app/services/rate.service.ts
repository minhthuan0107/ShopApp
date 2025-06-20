import { Rate } from './../models/rate.model';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { HttpClient } from '@angular/common/http';
import { RatingDto } from '../dtos/rating.dto';
import { Observable } from 'rxjs';
import { ApiResponse } from '../responses/api.response';

@Injectable({
  providedIn: 'root'
})
export class RateService {
    private apiRates = `${environment.apiBaseUrl}/rates`;

  constructor(private http : HttpClient) { }

  submitRate(ratingDto: RatingDto): Observable<ApiResponse<Rate>>{
    return this.http.post<ApiResponse<Rate>>(`${this.apiRates}/submit`,ratingDto)
  }
  getRatesByProductId(productId: number):Observable<ApiResponse<Rate[]>>{
    return this.http.get<ApiResponse<Rate[]>>(`${this.apiRates}/${productId}`)
  }

}
