import { CommonModule } from '@angular/common';
import { Component, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { SigninAdminDto } from '../../dtos/signin.admin.dto';
import { AuthService } from '../../services/auth.service';
import { ToastrService } from 'ngx-toastr';
import { HttpClientModule, HttpErrorResponse } from '@angular/common/http';
import { SigninAdminResponse } from '../../responses/user/signin-admin.response';
import { switchMap } from 'rxjs';
import Swal from 'sweetalert2';
import { TokenAdminService } from '../../services/token-admin.service';

@Component({
  selector: 'app-signin',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule, HttpClientModule],
  templateUrl: './signin.component.html',
  styleUrl: './signin.component.scss'
})
export class SigninComponent {
@ViewChild('signinForm') signinForm!: NgForm;
  phoneNumber: string;
  password: string;
  showPassword: boolean = false;
  rememberMe: boolean = false;
  constructor(private authService: AuthService,
    private tokenAdminService: TokenAdminService,
    private router: Router,
    private toastr: ToastrService) {
    this.phoneNumber = '';
    this.password = '';
  }
  togglePasswordVisibility() {
    this.showPassword = !this.showPassword; // Đảo ngược trạng thái hiển thị mật khẩu
  }
  public toggleRememberMe(): void {
    this.rememberMe = !this.rememberMe; // Đảo ngược giá trị của rememberMe
  }
  signin(form: NgForm) {
    if (!form.valid) {
      this.toastr.error('Vui lòng nhập SĐT hoặc mật khẩu.', 'Error', {
        timeOut: 1500,
        progressBar: true,
      });
      return;
    }
  
    const signinAdminDto: SigninAdminDto = {
      "phone_number": this.phoneNumber,
      "password": this.password
    };
    this.authService.signin(signinAdminDto).pipe(
      switchMap((response: SigninAdminResponse) => {
        const accessToken = response.access_token;
        const refreshToken = response.refresh_token;
        this.tokenAdminService.setAccessToken(accessToken);
        this.tokenAdminService.setRefreshToken(refreshToken);
        // Sau khi nhận token, gọi API lấy thông tin user
        return this.authService.fetchUserInfo();
      })
    ).subscribe({
      next: (user) => {
        this.authService.setCurrentUser(user); 
        // Sau khi cập nhật user thành công, hiển thị thông báo rồi chuyển hướng
        Swal.fire({
          icon: 'success',
          title: 'Đăng nhập thành công',
          showConfirmButton: false,
          timer: 800,
          customClass: { popup: 'custom-swal' }
        }).finally(() => {
          this.router.navigate(['/admin/home']);
        });
      },
      error: (error: HttpErrorResponse) => {
        const errorMessage = error.error.message;
        this.toastr.error(errorMessage, 'Error', {
          timeOut: 1500,
          progressBar: true,
        });
      }
    });
  }
  
}
