// src/app/guards/signin-page.guard.ts
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenAdminService } from '../services/token-admin.service';

export const signinPageGuard: CanActivateFn = () => {
  const router = inject(Router);
  const tokenService = inject(TokenAdminService);
  const accessToken = tokenService.getAccessToken();

  if (accessToken) {
    // Nếu có access token (đang login) thì chặn không cho vào trang signin
    router.navigate(['/admin/home']);
    return false;
  }

  // Nếu không có token (chưa login hoặc đã hết hạn) → cho vào signin
  return true;
};
