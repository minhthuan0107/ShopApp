import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { Observable } from 'rxjs';
import { Product } from '../models/product.model';
import { ProductListAdminResponse } from '../responses/product-list-admin.response';
import { UpdateProductDto } from '../dtos/update.product.dto';
import { ApiResponse } from '../responses/api.response';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiGetProducts = `${environment.apiBaseAdminUrl}/products/get-all`;
  private apiUpdateProduct = `${environment.apiBaseAdminUrl}/products`;
  private apiCreateNew = `${environment.apiBaseAdminUrl}/products/create-new`;
  private apiUpload = `${environment.apiBaseAdminUrl}/product-images/uploads-cloudinary`;
  private apiDeleteProduct = `${environment.apiBaseAdminUrl}/products`;
  constructor(private http: HttpClient) { }

  getAllProducts(page: number, size: number, keyword: string = ''): Observable<ProductListAdminResponse> {
    return this.http.get<ProductListAdminResponse>(this.apiGetProducts, {
      params: {
        page: page.toString(),
        size: size.toString(),
        keyword: keyword
      }
    });
  }
  //Api update sản phẩm
  updateProduct(productId: number, productDto: UpdateProductDto): Observable<Product> {
    return this.http.put<Product>(`${this.apiUpdateProduct}/${productId}`, productDto);
  }
  //Api tạo mới sản phẩm
  createProduct(productDto: any): Observable<any> {
    return this.http.post(`${this.apiCreateNew}`, productDto);
  }
  //Api upload ảnh lên cloudinary
  uploadImages(productId: number, formData: FormData): Observable<any> {
    return this.http.post(`${this.apiUpload}/${productId}`, formData);
  }
  //Api xóa mềm sản phẩm
    deleteProductById(productId: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.apiDeleteProduct}/${productId}`);
  }
}
