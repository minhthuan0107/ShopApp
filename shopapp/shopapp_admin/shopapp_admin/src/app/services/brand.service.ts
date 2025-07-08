import { ApiResponse } from './../responses/api.response';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Brand } from '../models/brand.model';
import { environment } from '../environments/environment';
import { Observable } from 'rxjs';
import { CreateBrandDto } from '../dtos/create.brand.dto';

@Injectable({
  providedIn: 'root'
})
export class BrandService {
  private apigetAllBrands = `${environment.apiBaseUrl}/brands/get-all`;
  private apiDeleteBrand = `${environment.apiBaseAdminUrl}/brands/delete`;
  private apiCreateNewBrand = `${environment.apiBaseAdminUrl}/brands/create`;
  constructor(private http: HttpClient) { }

  //Api lấy danh sách thương hiệu sản phẩm
  getAllBrands(): Observable<ApiResponse<Brand[]>> {
    return this.http.get<ApiResponse<Brand[]>>(this.apigetAllBrands);
  }
  //Api tạo mới thương hiệu sản phẩm
  createNewBrand(createBrandDto: CreateBrandDto): Observable<ApiResponse<Brand>> {
    return this.http.post<ApiResponse<Brand>>(this.apiCreateNewBrand, createBrandDto)
  }
  //Api xóa mềm thương hiệu
  deleteBrandById(brandId: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.apiDeleteBrand}/${brandId}`);
  }
}
