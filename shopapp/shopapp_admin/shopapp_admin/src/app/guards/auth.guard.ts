import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenAdminService } from '../services/token-admin.service';
import { catchError, map, of } from 'rxjs';
import Swal from 'sweetalert2';

export const authGuard: CanActivateFn = (route, state) => {
 const tokenService = inject(TokenAdminService);
  const router = inject(Router);
  const authMessage = route.data['authMessage'] || 'Vui lòng đăng nhập để truy cập trang này!';
  const accessToken = tokenService.getAccessToken();
  const refreshToken = tokenService.getRefreshToken();


  
  if (accessToken) {
    return of(true);
  } else if (refreshToken) {
    return tokenService.generateNewAccessToken(refreshToken).pipe(
      map((response) => {
        const newAccessToken = response.data.access_token; // Lấy access_token
        if (newAccessToken) {
          tokenService.setAccessToken(newAccessToken);
          return true;
        } else {
          Swal.fire({
            title: 'Vui lòng đăng nhập!',
            text: authMessage,
            icon: 'warning',
            confirmButtonText: 'Đăng nhập'
          });
          return router.parseUrl('/signin'); // redirect tới trang đăng nhập
        }
      }),
      catchError(() => {
        // Nếu có lỗi trong quá trình lấy access token, yêu cầu đăng nhập
        Swal.fire({
          title: 'Vui lòng đăng nhập!',
          text: authMessage,
          icon: 'warning',
          confirmButtonText: 'Đăng nhập'
        });
        return of(router.parseUrl('/signin')); // redirect tới trang đăng nhập
      })
    );
  } else {
    // Nếu không có cả accessToken và refreshToken, yêu cầu đăng nhập
    Swal.fire({
      title: 'Vui lòng đăng nhập!',
      text: authMessage,
      icon: 'warning',
      confirmButtonText: 'Đăng nhập'
    });
    return of(router.parseUrl('/signin')); // redirect tới trang đăng nhập
  }
};
