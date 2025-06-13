import { TokenService } from './../../services/token.service';
import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { finalize, switchMap } from 'rxjs';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-auth-callback',
  standalone: true,
  imports: [],
  templateUrl: './auth-callback.component.html',
  styleUrl: './auth-callback.component.scss'
})
export class AuthCallbackComponent {
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
      })
    ).subscribe({
      next: (user) => {
        this.userService.setCurrentUser(user);
        this.router.navigateByUrl('/home');
      },
      error: (err) => {
        console.error('Lỗi xác thực hoặc lấy user:', err);
        // Bạn có thể hiện thông báo lỗi thay vì navigate
      }
    });

  }
}
