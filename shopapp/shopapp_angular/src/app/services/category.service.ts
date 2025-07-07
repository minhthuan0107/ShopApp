import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { environment } from '../environments/environment';
import { Category } from '../models/category.model';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  private apiCategories = `${environment.apiBaseUrl}/categories/get-all`;
  constructor(private http: HttpClient) {}
  //Api lấy danh sách tất cả danh mục
  getAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.apiCategories);
  }

}
 
