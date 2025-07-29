import { HttpInterceptorFn } from '@angular/common/http';
import { TokenService } from '../services/token.service';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, finalize } from 'rxjs/operators';
import { throwError, of, BehaviorSubject, EMPTY } from 'rxjs';

let isRefreshing = false;
const refreshTokenSubject = new BehaviorSubject<string | null>(null);

export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenService = inject(TokenService);
  const router = inject(Router);
  const accessToken = tokenService.getAccessToken();
  if (accessToken) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${accessToken}`
      }
    });
  }
  return next(req).pipe(
    catchError((error) => {
      if (error.status === 401) {
        const refreshToken = tokenService.getRefreshToken();
        if (!refreshToken) {
          // Không có refresh token → logout ngay
          tokenService.logout();
          return EMPTY;
        }
        if (!isRefreshing) {
          isRefreshing = true;
          refreshTokenSubject.next(null);
          return tokenService.generateNewAccessToken(refreshToken).pipe(
            switchMap((res) => {
              const newAccessToken = res.data.access_token;
              tokenService.setAccessToken(newAccessToken);
              refreshTokenSubject.next(newAccessToken);
              // Gửi lại request với access token mới
              const newReq = req.clone({
                setHeaders: {
                  Authorization: `Bearer ${newAccessToken}`
                }
              });
              return next(newReq);
            }),
            catchError(() => {
              // Refresh token hết hạn hoặc lỗi → logout
              tokenService.logout();
              router.navigate(['/signin']);
              return EMPTY; // Dừng luồng request
            }),
            finalize(() => {
              isRefreshing = false;
            })
          );
        } else {
          // Đang refresh token rồi, chờ đến khi có token mới
          return refreshTokenSubject.pipe(
            switchMap(token => {
              if (token) {
                const newReq = req.clone({
                  setHeaders: {
                    Authorization: `Bearer ${token}`
                  }
                });
                return next(newReq);
              } else {
                // Nếu token không có (refresh token thất bại) → dừng luôn
                return EMPTY;
              }
            })
          );
        }
      }
      // Các lỗi khác thì ném ra ngoài
      return throwError(() => error);
    })
  );
};
