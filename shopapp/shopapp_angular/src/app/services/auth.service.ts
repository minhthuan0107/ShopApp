import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiResponse } from '../responses/api.response';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiBaseUrl}/users/auth`;
  constructor(private http: HttpClient) { }

  getSocialLoginUrl(loginType: string): Observable<ApiResponse<any>> {
    const params = { login_type: loginType };
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/social-login`, { params });
  }

  sendSocialCode(code: string, loginType: string): Observable<any> {
    const params = {
      code: code,
      login_type: loginType
    };
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/social/callback`, { params });
  }


}
