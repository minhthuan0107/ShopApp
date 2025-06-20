import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';

export const headerInterceptor: HttpInterceptorFn = (req, next) => {
  // Thêm headers vào request
  const clonedRequest = req.clone({
    setHeaders: {
      'Content-Type': 'application/json',
      'Accept-Language': 'vi'
    }
  });
  
  return next(clonedRequest);
};