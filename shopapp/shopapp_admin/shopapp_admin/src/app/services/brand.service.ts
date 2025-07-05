import { ApiResponse } from './../responses/api.response';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Brand } from '../models/brand.model';
import { environment } from '../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BrandService {
  private apigetAllBrands = `${environment.apiBaseUrl}/brands/get-all`;
  constructor(private http: HttpClient) { }

  //Api lấy danh sách thương hiệu sản phẩm
  getAllBrands(): Observable<ApiResponse<Brand[]>> {
    return this.http.get<ApiResponse<Brand[]>>(this.apigetAllBrands);
  }
}
