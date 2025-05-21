import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../responses/api.response';
import { environment } from '../environments/environment';
import { FavoriteResponse } from '../responses/favorite.response';

@Injectable({
  providedIn: 'root'
})
export class FavoriteService {
  private apiFavorite = `${environment.apiBaseUrl}/favorites`; 

  constructor(private http : HttpClient) { }
  //Api thêm sản phẩm yêu thích
  addToFavorite(productId: number) :Observable<ApiResponse<any>>{
    return this.http.post<ApiResponse<any>>(`${this.apiFavorite}/${productId}`,{})
  }
  //Api lấy danh sách sản phẩm yêu thích theo người dùng

  getFavoriteProductsByUserId() :Observable<ApiResponse<FavoriteResponse[]>>{
    return this.http.get<ApiResponse<FavoriteResponse[]>>(`${this.apiFavorite}`)
  }

}
