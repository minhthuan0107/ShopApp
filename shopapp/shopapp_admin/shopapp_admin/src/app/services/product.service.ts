import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { Observable } from 'rxjs';
import { Product } from '../models/product.model';
import { ProductListAdminResponse } from '../responses/product-list-admin.response';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiGetProducts = `${environment.apiBaseAdminUrl}/products/get-all`;
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
}
