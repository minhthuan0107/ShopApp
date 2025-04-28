import { Brand } from './../models/brand';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { Observable } from 'rxjs';
import { ApiResponse } from '../responses/api.response';

@Injectable({
  providedIn: 'root'
})
export class BrandService {
  private apiBrand = `${environment.apiBaseUrl}/brands`;
  constructor(private http: HttpClient) { }
  //Api lấy danh sách thương hiệu theo danh mục 
  getBrandsByCategoryId (categoryId :number): Observable<ApiResponse<Brand[]>>{
   return this.http.get<ApiResponse<Brand[]>>(`${this.apiBrand}/${categoryId}`)
  }


}
