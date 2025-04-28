import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { UserService } from '../services/user.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private userService: UserService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    return this.userService.user$.pipe(
      map(user => {
        if (user) {
          return true; // Cho phép truy cập
        } else {
          alert('Bạn hãy đăng nhập để thêm sản phẩm vào giỏ hàng')
          this.router.navigate(['/signin']); // Chuyển hướng đến trang đăng nhập
          return false; // Từ chối truy cập
        }
      })
    );
  }
}
