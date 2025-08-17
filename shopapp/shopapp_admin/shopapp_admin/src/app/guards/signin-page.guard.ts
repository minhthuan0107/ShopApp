// src/app/guards/auto-login.guard.ts
import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { TokenAdminService } from '../services/token-admin.service';

export const signinPageGuard: CanActivateFn = () => {
  const router = inject(Router);
  const tokenService = inject(TokenAdminService);

  const token = tokenService.getRefreshToken();
  if (token) {
    router.navigate(['/admin/home']);
    return false; // chặn vào signin
  }
  return true; // cho vào signin
};