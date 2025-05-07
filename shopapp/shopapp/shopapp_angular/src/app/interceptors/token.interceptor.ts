import { HttpInterceptorFn } from '@angular/common/http';
import { TokenService } from '../services/token.service';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap } from 'rxjs/operators';
import { throwError, of } from 'rxjs';

export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenService = inject(TokenService);
  const router = inject(Router);
  const accessToken = tokenService.getAccessToken();

  // Gắn access token vào request nếu có
  if (accessToken) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${accessToken}`
      }
    });
  }

  // Xử lý request
  return next(req).pipe(
    catchError((error) => {
      // Nếu lỗi 401 (Access Token hết hạn)
      if (error.status === 401) {
        const refreshToken = tokenService.getRefreshToken();
        
        if (refreshToken) {
          // Gọi API refresh token
          return tokenService.generateNewAccessToken(refreshToken).pipe(
            switchMap((res) => {
              const newAccessToken = res.data.access_token; // tùy backend trả về
              // Lưu access token mới
              tokenService.setAccessToken(newAccessToken);
              // Gửi lại request ban đầu với access token mới
              const newReq = req.clone({
                setHeaders: {
                  Authorization: `Bearer ${newAccessToken}`
                }
              });
              return next(newReq);
            }),
            catchError(() => {
              // Nếu refresh token cũng hết hạn → logout
              tokenService.logout();
              router.navigate(['/signin']);
              return throwError(() => error);
            })
          );
        }
      }
      // Các lỗi khác thì ném ra ngoài
      return throwError(() => error);
    })
  );
};
