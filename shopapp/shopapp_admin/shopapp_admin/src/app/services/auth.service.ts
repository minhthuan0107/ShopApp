import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { BehaviorSubject, map, Observable } from 'rxjs';
import { User } from '../models/user.model';
import { HttpClient } from '@angular/common/http';
import { SigninAdminDto } from '../dtos/signin.admin.dto';
import { ApiResponse } from '../responses/api.response';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiSignin = `${environment.apiBaseAdminUrl}/auth/signin`;
  private apigetUser = `${environment.apiBaseUrl}/users/me`;
  private userSubject = new BehaviorSubject<User | null>(null);
  user$ = this.userSubject.asObservable();
  constructor(private http: HttpClient) {
    this.fetchUserInfo().subscribe(user => {
      this.setCurrentUser(user);
    });
  }
 
  //Api để gửi request đăng ký
  signin(signinAdminDto: SigninAdminDto): Observable<any> {
    return this.http.post(this.apiSignin, signinAdminDto)
  }

  fetchUserInfo(): Observable<User> {
    return this.http.get<ApiResponse<User>>(this.apigetUser).pipe(
      map(response => response.data) // Trả về trực tiếp user từ API
    );
  }
  //  Cập nhật user vào BehaviorSubject
  setCurrentUser(user: User | null): void {
    this.userSubject.next(user);
  }
  getCurrentUser(): User | null  {
    return this.userSubject.getValue();
  }
  clearUser(): void {
    this.userSubject.next(null);
  }
}
