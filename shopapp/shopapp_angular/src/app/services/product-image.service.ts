import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { HttpClient } from '@angular/common/http';
import { ApiResponse } from '../responses/api.response';
import { ProductImage } from '../models/product-image.model';

@Injectable({
  providedIn: 'root'
})
export class ProductImageService {
    private apiGetProductImages = `${environment.apiBaseUrl}/product-images`;

  constructor(private http: HttpClient) { }

  getProducImagesByProductId(productId: number){
    return this.http.get<ApiResponse<ProductImage[]>>(`${this.apiGetProductImages}/${productId}`);
   }
}
