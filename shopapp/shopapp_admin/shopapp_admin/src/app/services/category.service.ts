import { ApiResponse } from './../responses/api.response';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Category } from '../models/category.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private apigetAllCategories = `${environment.apiBaseUrl}/categories/get-all`;
  constructor(private http: HttpClient) { }

  //Api lấy danh sách danh mục sản phẩm
  getAllCategories(): Observable<ApiResponse<Category[]>> {
    return this.http.get<ApiResponse<Category[]>>(this.apigetAllCategories);
  }
}
