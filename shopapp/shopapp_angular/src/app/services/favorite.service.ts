import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { ApiResponse } from '../responses/api.response';
import { environment } from '../environments/environment';
import { FavoriteResponse } from '../responses/favorite.response';

@Injectable({
  providedIn: 'root'
})
export class FavoriteService {
  private apiFavorite = `${environment.apiBaseUrl}/favorites`;
  private favoriteItemCount = new BehaviorSubject<number>(0);
  favoriteItemCount$ = this.favoriteItemCount.asObservable();

  constructor(private http: HttpClient) { }
  //Api thêm sản phẩm yêu thích
  addToFavorite(productId: number): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.apiFavorite}/${productId}`, {})
  }
  //Api lấy danh sách sản phẩm yêu thích theo người dùng
  getFavoriteProductsByUserId(): Observable<ApiResponse<FavoriteResponse[]>> {
    return this.http.get<ApiResponse<FavoriteResponse[]>>(`${this.apiFavorite}`)
  }
  //Api xóa sản phẩm yêu thích theo người dùng
 deleteFavoriteProduct(productId: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.apiFavorite}/${productId}`)
  }
  //Api xóa tất cả sản phẩm yêu thích
  deleteAllFavoriteProducts(): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.apiFavorite}/remove-all`)
  }
  getFavoriteItemsCount(): Observable<ApiResponse<number>> {
    return this.http.get<ApiResponse<number>>(`${this.apiFavorite}/count`).pipe(
      tap(response => {
        this.favoriteItemCount.next(response.data);
      })
    );
  }
  //Api để cập nhật count khi xóa sản phẩm
  updateFavoriteItemCount(count: number | undefined) {
    if (count === undefined) {
      return;
    }
    this.favoriteItemCount.next(count < 0 ? 0 : count);
  }
  //Hàm lấy giá trị count từ behavior subject
  getCurrentFavoriteItemCount(): number {
    return this.favoriteItemCount.getValue();
  }
  //Hàm set giá trị count vào behavior subject
  setFavoriteItemCount(count: number) {
    this.favoriteItemCount.next(count);
  }

}
