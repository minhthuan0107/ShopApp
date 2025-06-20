import { CommonModule } from '@angular/common';
import { Component, ViewEncapsulation } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { OrderService } from '../../../services/order.service';
import { OrderResponse } from '../../../responses/order.response';
import { OrderDetailResponse } from '../../../responses/order-detail.response';
import { PaymentResponse } from '../../../responses/payment.response';
import { UserService } from '../../../services/user.service';
import { NgxPaginationModule } from 'ngx-pagination';
import { OrderDetailComponent } from './order-detail/order-detail.component';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule,NgxPaginationModule,OrderDetailComponent],
  templateUrl: './order-history.component.html',
  styleUrls: ['./order-history.component.scss'],

})
export class OrderHistoryComponent {
   constructor(private orderService: OrderService,
    private userService : UserService,
    private router: Router ) { }
    orders: OrderResponse[] = [];
    orderDetails: OrderDetailResponse[] = [];
    payment : PaymentResponse | null = null;
    userId!: number;
    page: number = 1;
    itemsPerPage: number = 5;
    isOrderDetailVisible = false;
    selectedOrder: any = null;
    ngOnInit(): void {
      this.userService.user$.subscribe(user => {
        if (user && user.id) {
          this.userId = user.id;
          this.loadOrderHistory(this.userId);
        }
      });
    }
    loadOrderHistory(userId: number): void {
      this.orderService.getOrdersByUserId(userId).subscribe({
        next: (response) => {
          this.orders = response.data.sort((a, b) => {
            const dateA = new Date(a.order_date);
            const dateB = new Date(b.order_date);
            return dateB.getTime() - dateA.getTime();
          });
        },
        error: (error) => {
          console.error("Lỗi!", error.error?.message || "Lấy danh sách lịch sử đơn hàng thất bại");
          this.orders = [];
        }
      });
    }
    getStatusText(status: string): string {
      switch (status) {
        case 'PENDING': return 'Đang chờ xử lý';
        case 'PROCESSING': return 'Đang xử lý';
        case 'SHIPPING': return 'Đang giao hàng';
        case 'COMPLETED': return 'Đã hoàn thành';
        case 'CANCELLED': return 'Đã hủy';
        default: return 'Không xác định';
      }
    }

  cancelOrder(orderId: number): void {
    Swal.fire({
      title: 'Bạn có chắc chắn muốn hủy đơn hàng này?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Có',
      cancelButtonText: 'Không',
    }).then((result) => {
      if (result.isConfirmed) {
        this.orderService.cancelOrder(orderId).subscribe({
          next: () => {
            // Cập nhật trạng thái đơn hàng thành 'CANCELLED' ngay lập tức
          const index = this.orders.findIndex(order => order.order_id === orderId);
          if (index !== -1) {
            this.orders[index].status = 'CANCELLED';
          }
            Swal.fire(
              'Đã hủy!',
              'Đơn hàng của bạn đã được hủy thành công.',
              'success'
            );
            this.router.navigate(['/order-history']);
          },
          error: (error) => {
            console.error("Lỗi!", error.error?.message || "Có lỗi xảy ra khi hủy đơn hàng");
          }
        });
      } else {
        this.router.navigate(['/order-history']);
      }
    });
  }
    viewOrderDetails(order: any) {
      this.selectedOrder = order;
      this.isOrderDetailVisible = true;
    }
    closeOrderDetail() {
      this.isOrderDetailVisible = false;
    }


}
