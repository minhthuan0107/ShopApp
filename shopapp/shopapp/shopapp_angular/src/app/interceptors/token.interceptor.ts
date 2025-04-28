import { HttpInterceptorFn } from '@angular/common/http';
import { TokenService } from '../services/token.service';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenService = inject(TokenService);
  const token = tokenService.getAccessToken();

  if (token) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }
  return next(req).pipe(
    catchError((error) => {
      if (error.status === 401) { 
        tokenService.logout(); // Xóa token & chuyển hướng về trang Home (nếu đã xử lý trong logout)
      }
      return throwError(() => error);
    })
  );
};
