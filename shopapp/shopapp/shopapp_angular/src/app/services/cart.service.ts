import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { HttpClient } from '@angular/common/http';
import { CartDetailDto } from '../dtos/cartdetail.dto';
import { BehaviorSubject, catchError, Observable, of, tap } from 'rxjs';
import { ApiResponse } from '../responses/api.response';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private cartItemCount = new BehaviorSubject<number>(0);
  cartItemCount$ = this.cartItemCount.asObservable();
  private apiCart = `${environment.apiBaseUrl}/carts`
  constructor(private http: HttpClient) { }
  // Api để thêm sản phẩm vào giỏ hàng
  addToCart(cartDetail: CartDetailDto): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.apiCart}`, cartDetail);
  }
  //Api lấy số count trong cart
  getCartItemCount(): Observable<ApiResponse<number>> {
    return this.http.get<ApiResponse<number>>(`${this.apiCart}/count`).pipe(
      tap(response => {
        this.cartItemCount.next(response.data);
      })
    );
  }
  //Api để cập nhật count khi xóa sản phẩm
  updateCartItemCount(count: number | undefined) {
    if (count === undefined) {
      return;
    }
    this.cartItemCount.next(count < 0 ? 0 : count);
  }
   //Hàm lấy giá trị count từ behavior subject
  getCurrentCartItemCount(): number {
    return this.cartItemCount.getValue();
  }
  //Hàm set giá trị count vào behavior subject
  setCartItemCount(count: number) {
    this.cartItemCount.next(count);
  }
}
