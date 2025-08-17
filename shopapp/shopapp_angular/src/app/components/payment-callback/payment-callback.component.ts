import { UserService } from './../../services/user.service';
import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';
import { CartService } from '../../services/cart.service';
import { switchMap, take, tap } from 'rxjs';
import { User } from '../../models/user.model';
import { PaymentService } from '../../services/payment.service';
import { UpdatePaymentStatusDto } from '../../dtos/update-payment-status.dto';

@Component({
  selector: 'app-payment-callback',
  standalone: true,
  imports: [],
  templateUrl: './payment-callback.component.html',
  styleUrl: './payment-callback.component.scss'
})
export class PaymentCallbackComponent {
  userId!: number;
  transactionId: string | null = '';
  orderId!: number;
  constructor(private route: ActivatedRoute,
    private router: Router,
    private cartService: CartService,
    private userService: UserService,
    private paymentService: PaymentService) { }

  ngOnInit(): void {
    const cachedUser = this.userService.getCurrentUser();
    if (cachedUser) {
      this.handleUser(cachedUser);
    } else {
      this.userService.fetchUserInfo().pipe(take(1)).subscribe(user => {
        if (user) {
          this.userService.setCurrentUser(user);
          this.handleUser(user);
        } else {
          console.log("Không có user nào đăng nhập!");
        }
      });
    }
  }
 private handleUser(user: User): void {
    this.userId = user.id;
    this.route.queryParams.subscribe(params => {
      const vnp_ResponseCode = params['vnp_ResponseCode'];
      this.transactionId = params['vnp_TxnRef'];
      if (this.transactionId) {
        this.paymentService.getOrderIdByTransactionId(this.transactionId).subscribe({
          next: (response) => {
           this.orderId = response?.data?.order_id;
              let status = vnp_ResponseCode === '00' ? 'SUCCESS' : 'FAILED';
              const statusDto = {
                orderId: this.orderId,
                transactionId: this.transactionId!,
                status: status
              };
              this.updatePaymentStatus(statusDto);
          },
          error: () => {
            this.showErrorAlert();
          }
        });
      }
    });
  }


  private updatePaymentStatus(statusDto: UpdatePaymentStatusDto): void {
    this.paymentService.updatePayment(statusDto).subscribe({
      next: (response) => {
        console.log("Phản hồi từ server:", response);
        if (response.data.status === 'SUCCESS') {
          this.showSuccessAlert();
        if (response.data.is_buy_now === false) {
          this.cartService.setCartItemCount(0);
        }
        } else {
          this.showErrorAlert();
        }
      },
      error: (error) => {
        console.error("Lỗi", error.error?.message||'Lỗi khi cập nhật thanh toán');
        this.showErrorAlert();
      } 
    });
  }
  showSuccessAlert() {
    Swal.fire({
      icon: 'success',
      title: 'Thanh toán thành công!',
      text: `Mã giao dịch: ${this.transactionId}\n\nĐơn hàng của bạn đã được đặt thành công!`,
      confirmButtonText: 'Xem chi tiết'
    }).then(() => {
      this.router.navigate(['/order-confirmation', this.orderId]);
    });
  }

  showErrorAlert() {
    Swal.fire({
      icon: 'error',
      title: 'Thanh toán thất bại!',
      text: 'Có lỗi xảy ra trong quá trình thanh toán. Vui lòng thử lại.',
      confirmButtonText: 'OK'
    }).then(() => {
      this.router.navigate(['/check-out']);
    });
  }
}
