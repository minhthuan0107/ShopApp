import { SigninDto } from './../dtos/signin.dto';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable, tap } from 'rxjs';
import { SignupDto } from '../dtos/signup.dto';
import { environment } from '../environments/environment';
import { ApiResponse } from '../responses/api.response';
import { User } from '../models/user.model';
import { ChangePasswordDto } from '../dtos/change-password.dto';
import { UpdateProfileDto } from '../dtos/update-profile.dto';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiSignup = `${environment.apiBaseUrl}/users/signup`;
  private apiSignin = `${environment.apiBaseUrl}/users/signin`;
  private apiChangePassword = `${environment.apiBaseUrl}/users/change-password`;
  private apiupdateProfile = `${environment.apiBaseUrl}/users/update-profile`;
  private apigetUser = `${environment.apiBaseUrl}/users/me`;
  private userSubject = new BehaviorSubject<User | null>(null);
  user$ = this.userSubject.asObservable();
  constructor(private http: HttpClient) {
    this.fetchUserInfo().subscribe(user => {
      this.setCurrentUser(user);
    });
  }
  //Api để gửi request đăng nhập
  signup(signupDto: SignupDto): Observable<any> {
    return this.http.post(this.apiSignup, signupDto)
  }
  //Api để gửi request đăng ký
  signin(signinDto: SigninDto): Observable<any> {
    return this.http.post(this.apiSignin, signinDto)
  }
  //APi thay đổi password
  changePassword(passwordData: ChangePasswordDto): Observable<ApiResponse<any>> {
    return this.http.patch<ApiResponse<any>>(this.apiChangePassword, passwordData)
  }
  //Api chỉnh sửa hồ sơ cá nhân
  updateProfile(updateProfile: UpdateProfileDto): Observable<ApiResponse<any>> {
    return this.http.patch<ApiResponse<any>>(this.apiupdateProfile, updateProfile)
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
