import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';

export const headerInterceptor: HttpInterceptorFn = (req, next) => {
  const headers: Record<string, string> = {
    'Accept-Language': 'vi'
  };

  const isFormData = req.body instanceof FormData;
  if (!isFormData) {
    headers['Content-Type'] = 'application/json';
  }

  const clonedRequest = req.clone({
    setHeaders: headers
  });

  return next(clonedRequest);
};