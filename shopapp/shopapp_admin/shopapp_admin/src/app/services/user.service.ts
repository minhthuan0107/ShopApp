import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { CreateUserDto } from '../dtos/create.user.dto';
import { ApiResponse } from '../responses/api.response';
import { UpdateUserDto } from '../dtos/update.user.dto';
import { UserListAdminResponse } from '../responses/user/user-list-admin.response';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiGetAllUSers = `${environment.apiBaseAdminUrl}/users/get-all`;
  private apiCreateUser = `${environment.apiBaseUrl}/users/signup`;
  private apiUpdateUser = `${environment.apiBaseAdminUrl}/users/update-profile`;
  private apitoggleUserStatus = `${environment.apiBaseAdminUrl}/users/toggle-status`;

  constructor(private http: HttpClient) { }
  //Api lấy danh sách tất cả khách hàng
  getAllUsers(page: number, size: number,keyword: string = ''): Observable<ApiResponse<UserListAdminResponse>> {
    return this.http.get<ApiResponse<UserListAdminResponse>>(this.apiGetAllUSers, {
      params: {
        page: page.toString(),
        size: size.toString(),
        keyword: keyword
      }
    });
  }
   //Api để gửi request tạo mới user
  createUser(createUserDto: CreateUserDto): Observable<any> {
    return this.http.post(this.apiCreateUser, createUserDto)
  }
   //Api để gửi request cập nhật user
  updateUser(userId: number,updateUserDto: UpdateUserDto): Observable<ApiResponse<any>> {
    return this.http.patch<ApiResponse<any>>(`${this.apiUpdateUser}/${userId}`,updateUserDto)
  }
  //Api để khóa / mở tài khoản người dùng
  toggleUserStatus(userId: number): Observable<ApiResponse<any>> {
  return this.http.patch<ApiResponse<any>>(`${this.apitoggleUserStatus}/${userId}`, {});
  }
}
