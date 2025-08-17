import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { Router } from '@angular/router';
import { HttpClient, HttpParams } from '@angular/common/http';
import { firstValueFrom, Observable } from 'rxjs';
import { ApiResponse } from '../responses/api.response';

@Injectable({
  providedIn: 'root'
})
export class TokenAdminService {

  private readonly ACCESS_TOKEN_KEY = 'access-token';
  private readonly REFRESH_TOKEN_KEY = 'refresh-token';
  private apigGenerateAccessToken = `${environment.apiBaseUrl}/tokens/new-access-token`;
  private apiRevokeRefreshToken = `${environment.apiBaseUrl}/tokens/refresh-token`;

  constructor(private router: Router,
    private http: HttpClient
  ) { }
  //Api tạo mới Access Token dựa trên Refresh Token
  generateNewAccessToken(refreshToken: string): Observable<ApiResponse<any>> {
    const params = new HttpParams().set('refreshToken', refreshToken);
    return this.http.post<ApiResponse<any>>(`${this.apigGenerateAccessToken}`, null, { params })
  }
  //Api thu thồi refresh token
  revokeRefreshToken(refreshToken: string): Observable<ApiResponse<any>> {
    const params = new HttpParams().set('refreshToken', refreshToken);
    return this.http.patch<ApiResponse<any>>(`${this.apiRevokeRefreshToken}`, null, { params })
  }

  public getAccessToken(): string | null {
    return sessionStorage.getItem(this.ACCESS_TOKEN_KEY);
  }
  public setAccessToken(accessToken: string): void {
    sessionStorage.setItem(this.ACCESS_TOKEN_KEY, accessToken);
  }

  public async removeToken(): Promise<void> {
    // Chờ thu hồi refresh token từ server trước khi xóa local token
    await this.handleRevokeRefreshToken();
    // Xóa access token và refresh token ở local storage và session storage
    sessionStorage.removeItem(this.ACCESS_TOKEN_KEY);
    localStorage.removeItem(this.ACCESS_TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
  }

  public async handleRevokeRefreshToken(): Promise<void> {
    const refreshToken = this.getRefreshToken();
    // Nếu có refresh token mới gọi API thu hồi
    if (refreshToken) {
      try {
        // Chờ API thu hồi thành công
        await firstValueFrom(this.revokeRefreshToken(refreshToken));
      } catch (error: any) {
        // Nếu lỗi, in ra lỗi nhưng vẫn tiếp tục xóa local token
        console.error("Lỗi!", error.error?.message || "Thu hồi Refresh Token thất bại");
      }
    }
  }

  public getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }
  public setRefreshToken(refreshToken: string): void {
    localStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
  }
  logout(): void {
    this.removeToken();
  }
}
