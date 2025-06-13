import { SigninDto } from './../../dtos/signin.dto';
import { Component, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HttpClientModule, HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { UserService } from '../../services/user.service';
import Swal from 'sweetalert2';
import { TokenService } from '../../services/token.service';
import { LoginResponse } from '../../responses/login.response';
import { switchMap } from 'rxjs';
import { AuthService } from '../../services/auth.service';

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
  constructor(private userService: UserService,
    private tokenService: TokenService,
    private router: Router,
    private toastr: ToastrService,
    private authService: AuthService) {
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
  
    const signinDto: SigninDto = {
      "phone_number": this.phoneNumber,
      "password": this.password
    };
    this.userService.signin(signinDto).pipe(
      switchMap((response: LoginResponse) => {
        const accessToken = response.access_token;
        const refreshToken = response.refresh_token;
        this.tokenService.setUseSessionStorage(!this.rememberMe);
        this.tokenService.setAccessToken(accessToken);
        this.tokenService.setRefreshToken(refreshToken);
        // Sau khi nhận token, gọi API lấy thông tin user
        return this.userService.fetchUserInfo();
      })
    ).subscribe({
      next: (user) => {
        this.userService.setCurrentUser(user); 
        // Sau khi cập nhật user thành công, hiển thị thông báo rồi chuyển hướng
        Swal.fire({
          icon: 'success',
          title: 'Đăng nhập thành công',
          showConfirmButton: false,
          timer: 800,
          customClass: { popup: 'custom-swal' }
        }).finally(() => {
          this.router.navigate(['/home']);
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
   // Mở redirect url gg
  loginWithSocial(type: string) {
    this.authService.getSocialLoginUrl(type).subscribe({
      next: res => {
        window.location.href = res.data; // Redirect đến Google login
      },
      error: (error) => {
         console.error('Lỗi!', error.error?.message || 'Lỗi khi tạo link đăng nhập');
      }
    });
  }
}

