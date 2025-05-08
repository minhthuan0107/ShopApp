import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { OrderService } from '../services/order.service';
import { of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { TokenService } from '../services/token.service';
import { UserService } from '../services/user.service';
import Swal from 'sweetalert2';

export const orderConfirmationGuard: CanActivateFn = (route, state) => {
  const orderId = Number(route.paramMap.get('orderId'));
  const orderService = inject(OrderService);
  const tokenService = inject(TokenService);
  const userService = inject(UserService);
  const router = inject(Router);
  const refreshToken = tokenService.getRefreshToken();


  if (!refreshToken) {
    Swal.fire({
      icon: 'warning',
      title: 'Bạn chưa đăng nhập',
      text: 'Vui lòng đăng nhập để tiếp tục.',
      confirmButtonText: 'Đăng nhập'
    }).then(() => {
      router.navigate(['/signin']);
    });
    return of(false); // Không cho phép truy cập nếu không có refreshToken
  }

  const accessToken = tokenService.getAccessToken();

  if (accessToken) {
    // Nếu có accessToken, tiếp tục lấy thông tin người dùng
    return userService.fetchUserInfo().pipe(
      switchMap((currentUser) => {
        if (!currentUser) {
          Swal.fire({
            icon: 'warning',
            title: 'Thông tin người dùng không hợp lệ',
            text: 'Vui lòng đăng nhập lại.',
            confirmButtonText: 'Đăng nhập'
          }).then(() => {
            router.navigate(['/signin']);
          });
          return of(false);
        }

        return orderService.getOrderById(orderId).pipe(
          map(response => {
            const order = response.data;
            if (order.phone_number === currentUser.phone_number &&
              (order.payment.status === 'SUCCESS' || order.payment.status === 'PENDING')) {
              return true;
            } else {
              Swal.fire({
                icon: 'error',
                title: 'Truy cập bị từ chối',
                text: 'Bạn không có quyền truy cập vào đơn hàng này.',
                confirmButtonText: 'Về trang chủ'
              }).then(() => router.navigate(['/home']));
              return false;
            }
          }),
          catchError(() => {
            Swal.fire({
              icon: 'error',
              title: 'Không tìm thấy đơn hàng',
              text: 'Đơn hàng không tồn tại hoặc đã bị xoá.',
              confirmButtonText: 'Về trang chủ'
            }).then(() => router.navigate(['/home']));
            return of(false);
          })
        );
      })
    );
  } else {
    // Nếu không có accessToken, gọi API để lấy lại accessToken
    return tokenService.generateNewAccessToken(refreshToken).pipe(
      switchMap((response) => {
        const newAccessToken = response.data.access_token;
        if (newAccessToken) {
          tokenService.setAccessToken(newAccessToken); // Lưu accessToken mới
          return userService.fetchUserInfo().pipe(
            switchMap((currentUser) => {
              if (!currentUser) {
                Swal.fire({
                  icon: 'warning',
                  title: 'Thông tin người dùng không hợp lệ',
                  text: 'Vui lòng đăng nhập lại.',
                  confirmButtonText: 'Đăng nhập'
                }).then(() => router.navigate(['/signin']));
                return of(false);
              }
              return orderService.getOrderById(orderId).pipe(
                map(response => {
                  const order = response.data;
                  if (order.phone_number === currentUser.phone_number &&
                    (order.payment.status === 'SUCCESS' || order.payment.status === 'PENDING')) {
                    return true;
                  } else {
                    Swal.fire({
                      icon: 'error',
                      title: 'Truy cập bị từ chối',
                      text: 'Bạn không có quyền truy cập vào đơn hàng này.',
                      confirmButtonText: 'Về trang chủ'
                    }).then(() => router.navigate(['/home']));
                    return false;
                  }
                }),
                catchError(() => {
                  Swal.fire({
                    icon: 'error',
                    title: 'Không tìm thấy đơn hàng',
                    text: 'Đơn hàng không tồn tại hoặc đã bị xoá.',
                    confirmButtonText: 'Về trang chủ'
                  }).then(() => router.navigate(['/home']));
                  return of(false);
                })
              );
            })
          );
        } else {
          Swal.fire({
            icon: 'warning',
            title: 'Phiên đăng nhập đã hết hạn',
            text: 'Vui lòng đăng nhập lại.',
            confirmButtonText: 'Đăng nhập'
          }).then(() => router.navigate(['/signin']));
          return of(false);
        }
      }),
      catchError(() => {
        Swal.fire({
          icon: 'warning',
          title: 'Đã xảy ra lỗi',
          text: 'Vui lòng thử lại sau.',
          confirmButtonText: 'OK'
        }).then(() => router.navigate(['/signin']));
        return of(false);
      })
    );
  }
}
