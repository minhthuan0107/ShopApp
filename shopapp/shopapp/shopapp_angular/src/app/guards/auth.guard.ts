import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot, Router, CanActivateFn } from '@angular/router';
import Swal from 'sweetalert2';
import { TokenService } from '../services/token.service';

export const authGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const tokenService = inject(TokenService);
  const router = inject(Router);
  const authMessage = route.data['authMessage'] || 'Vui lòng đăng nhập để truy cập trang này!';
  const accessToken = tokenService.getAccessToken();
  if (accessToken) {
    return Promise.resolve(true);
  } else {
    return Swal.fire({
      title: 'Vui lòng đăng nhập!',
      text: authMessage,
      icon: 'warning',
      confirmButtonText: 'Đăng nhập'
    }).then(() => router.parseUrl('/signin')); // Angular tự redirect
  }
}