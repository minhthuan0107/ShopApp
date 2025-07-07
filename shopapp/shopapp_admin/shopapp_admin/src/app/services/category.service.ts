import { ApiResponse } from './../responses/api.response';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Category } from '../models/category.model';
import { Observable } from 'rxjs';
import { CreateCategoryDto } from '../dtos/create.category.dto';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private apigetAllCategories = `${environment.apiBaseUrl}/categories/get-all`;
  private apiCreateNewCategory = `${environment.apiBaseAdminUrl}/categories/create`;
  private apiDeleteCategory = `${environment.apiBaseAdminUrl}/categories/delete`;
  constructor(private http: HttpClient) { }

  //Api lấy danh sách danh mục sản phẩm
  getAllCategories(): Observable<ApiResponse<Category[]>> {
    return this.http.get<ApiResponse<Category[]>>(this.apigetAllCategories);
  }
  //Api tạo mới danh mục sản phẩm
  createNewCategory(createCategoryDto: CreateCategoryDto): Observable<ApiResponse<Category>> {
    return this.http.post<ApiResponse<Category>>(this.apiCreateNewCategory, createCategoryDto)
  }
  //Api xóa mềm danh mục
    deleteCategoryById(categoryId: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.apiDeleteCategory}/${categoryId}`);
  }
}
