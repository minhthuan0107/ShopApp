import { TokenService } from './../../services/token.service';
import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { finalize, switchMap } from 'rxjs';
import { UserService } from '../../services/user.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-auth-callback',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './auth-callback.component.html',
  styleUrl: './auth-callback.component.scss'
})
export class AuthCallbackComponent {
  isLoading = true; // ban đầu là true
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private tokenService: TokenService,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    const code = this.route.snapshot.queryParamMap.get('code');
    if (!code) {
      console.error('Không tìm thấy code trong URL');
      // Không navigate ngay, chỉ log lỗi, hoặc show UI lỗi
      return;
    }

    const fullUrl = window.location.href;
    let loginType: 'google' | 'facebook' | undefined;

    if (fullUrl.includes('/auth/google/callback')) {
      loginType = 'google';
    } else if (fullUrl.includes('/auth/facebook/callback')) {
      loginType = 'facebook';
    } else {
      console.error('Không xác định được nhà cung cấp xác thực.');
      return;
    }

    this.authService.sendSocialCode(code, loginType).pipe(
      switchMap(response => {
        this.tokenService.setAccessToken(response.access_token);
        this.tokenService.setRefreshToken(response.refresh_token);
        return this.userService.fetchUserInfo();
      }),
      finalize(() => {
        this.isLoading = false;
      })
    ).subscribe({
      next: (user) => {
        this.userService.setCurrentUser(user);
        this.router.navigateByUrl('/home');
      },
      error: (err) => {
        console.error('Lỗi xác thực hoặc lấy user:', err);
        // Có thể điều hướng về /login hoặc hiển thị lỗi tại chỗ
      }
    });
  }
}
