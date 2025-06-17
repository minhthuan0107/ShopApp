import { ApiResponse } from './../responses/api.response';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable, of } from 'rxjs';
import { environment } from '../environments/environment';
import { Product } from '../models/product.model';
import { ProductResponse } from '../responses/product.response';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiGetProducts = `${environment.apiBaseUrl}/products`;
  private apiGetProductsByCategoryId = `${environment.apiBaseUrl}/products/category`;
  private apiSearchProduct = `${environment.apiBaseUrl}/products/search`;
  private apiSuggestionProducts = `${environment.apiBaseUrl}/products/suggestions`;
  constructor(private http: HttpClient) { }
  //Lấy tất cả danh sách sản phẩm
  getProducts(page: number, limit: number): Observable<Product[]> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());
    return this.http.get<Product[]>(this.apiGetProducts, { params });
  }
  //Lấy danh sách sản phẩm theo categoryId hoặc lọc theo điều kiện
  getProductsByCategoryId(
    categoryId: number,
    page: number,
    limit: number,
    brandIds: number[], 
    minPrice?: number,
    maxPrice?: number,
    sortBy?: string
  ): Observable<Product[]> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());
    // Gửi danh sách thương hiệu như một chuỗi "1,2,3"
    if (brandIds.length > 0) {
      params = params.set('brandIds', brandIds.join(',')); // Chuyển thành chuỗi
    }
    if (minPrice !== undefined) {
      params = params.set('minPrice', minPrice.toString());
    }
    if (maxPrice !== undefined) {
      params = params.set('maxPrice', maxPrice.toString());
    }
    if (sortBy) {
      params = params.set('sort', sortBy);
    }
    return this.http.get<Product[]>(`${this.apiGetProductsByCategoryId}/${categoryId}`, { params });
  }

  searchProducts(
    keyword: string,
    page: number,
    limit: number,
    minPrice?: number,
    maxPrice?: number,
    sortBy?: string
  ): Observable<Product[]> {
    let params = new HttpParams()
      .set('keyword', keyword)  
      .set('page', page.toString())
      .set('limit', limit.toString());
  
    if (minPrice !== undefined) {
      params = params.set('minPrice', minPrice.toString());
    }
  
    if (maxPrice !== undefined) {
      params = params.set('maxPrice', maxPrice.toString());
    }
  
    if (sortBy) {
      params = params.set('sort', sortBy);
    }
    return this.http.get<Product[]>(`${this.apiSearchProduct}`, { params });
  }
  

  getProductById(productId: number) {
    return this.http.get<ApiResponse<Product>>(`${this.apiGetProducts}/${productId}`);
  }

  getPriceRange(){
    return this.http.get<ApiResponse<any>> (`${this.apiGetProducts}/price-range`) 
  }

  getTop14BestSellingProducts(){
    return this.http.get<ApiResponse<Product>> (`${this.apiGetProducts}/seller`) 
  }

  getTop14MostHighlyRatedProducts(){
    return this.http.get<ApiResponse<Product>> (`${this.apiGetProducts}/top-rated`) 
  }

  getProductSuggestions(keyword: string): Observable<ApiResponse<ProductResponse[]>> {
    return this.http.get<ApiResponse<ProductResponse[]>>(`${this.apiSuggestionProducts}`, {
      params: { keyword }
    });
  }
}
