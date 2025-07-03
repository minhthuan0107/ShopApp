import { CommonModule } from '@angular/common';
import { Component, TemplateRef, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { AddCustomerComponent } from '../add-customer/add-customer.component';
import { MatTooltipModule } from '@angular/material/tooltip';
import { OrderService } from '../../services/order.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { OrderAdminResponse } from '../../responses/order/order-admin.response';
import { OrderDetailComponent } from '../order-detail/order-detail.component';
import Swal from 'sweetalert2';
import { switchAll } from 'rxjs';

@Component({
  selector: 'app-order',
  standalone: true,
  imports: [CommonModule, FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    OrderDetailComponent],
  templateUrl: './order.component.html',
  styleUrl: './order.component.scss'
})
export class OrderComponent {
  totalItems: number = 0;
  keyword?: string = '';
  displayedColumns: string[] = ['order_id', 'full_name', 'phone_number', 'email', 'note',
    'address', 'order_date', 'total_price', 'status', 'paymentStatus', 'actions'];
  dataSource = new MatTableDataSource<any>();

  // 👇 Dùng ViewChild để lấy đối tượng MatPaginator từ template
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('viewDetailModal') viewDetailModal!: TemplateRef<any>;
  selectedOrder!: OrderAdminResponse;
  constructor(private orderService: OrderService,
    private modalService: NgbModal
  ) { }
  ngOnInit(): void {
    //Hiển thị danh sách người dùng
    this.getAllOrders(0, 6, this.keyword);
  }
  // Sau khi view render xong thì gán
  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
  }

  applyFilter() {
    const pageSize = this.paginator?.pageSize || 6;
    const keyword = (this.keyword || '').trim().toLowerCase();
    this.getAllOrders(0, pageSize, keyword);
  }

  onPageChange(event: PageEvent) {
    const page = event.pageIndex;      // Trang hiện tại (bắt đầu từ 0)
    const size = event.pageSize;       // Số lượng mỗi trang
    this.getAllOrders(page, size, this.keyword); // Gọi lại API để lấy dữ liệu trang mới
  }
  getAllOrders(page: number, size: number, keyword: string = '') {
    this.orderService.getAllOrders(page, size, keyword).subscribe({
      next: (res) => {
        this.dataSource.data = res.orderResponses;
        this.paginator.length = res.totalItems; // Tổng số mục để phân trang đúng
      },
      error: (err) => {
        console.error(err);
      }
    });
  }
  getStatusClass(status: string): string {
    switch (status?.toUpperCase()) {
      case 'PENDING':
        return 'status status-pending';
      case 'PROCESSING':
        return 'status status-processing';
      case 'SHIPPING':
        return 'status status-shipping';
      case 'COMPLETED':
        return 'status status-completed';
      case 'CANCELLED':
        return 'status status-cancelled';
      default:
        return 'status';
    }
  }
  getStatusLabel(status: string): string {
    switch (status) {
      case 'PENDING':
        return 'Chờ xác nhận';
      case 'PROCESSING':
        return 'Đang xử lý';
      case 'SHIPPING':
        return 'Đang giao hàng';
      case 'COMPLETED':
        return 'Hoàn tất';
      case 'CANCELLED':
        return 'Đã hủy';
      default:
        return status;
    }
  }
  getPaymentStatusClass(paymentStatus: string): string {
    switch (paymentStatus?.toUpperCase()) {
      case 'SUCCESS':
        return 'status payment-status-success';
      case 'PENDING':
        return 'status payment-status-pending';
      case 'FAILED':
        return 'status payment-status-failed';
      default:
        return 'status';
    }
  }

  getPaymentStatusLabel(paymentStatus: string): string {
    switch (paymentStatus?.toUpperCase()) {
      case 'SUCCESS':
        return 'Đã thanh toán';
      case 'PENDING':
        return 'Chờ thanh toán';
      case 'FAILED':
        return 'Thanh toán thất bại';
      default:
        return paymentStatus;
    }
  }

  viewOrderDetail(order: OrderAdminResponse) {
    this.selectedOrder = order;
    this.modalService.open(this.viewDetailModal, { size: 'lg', centered: true });
  }
  cancelOrder(order: OrderAdminResponse): void {
    Swal.fire({
      title: 'Xác nhận hủy đơn hàng?',
      text: `Bạn có chắc chắn muốn hủy đơn hàng có ID = ${order.order_id}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Hủy đơn',
      cancelButtonText: 'Không'
    }).then((result) => {
      if (result.isConfirmed) {
        this.orderService.cancelOrder(order.order_id).subscribe({
          next: () => {
            Swal.fire({
              icon: 'success',
              title: 'Đã hủy đơn hàng',
              text: `Đơn hàng có ID = ${order.order_id} đã được hủy.`,
              timer: 2000,
              showConfirmButton: false
            });
            this.getAllOrders(0, 6, '');
          },
          error: (err) => {
            console.error(err);
          }
        });
      }
    });
  }
  isFinalStatus(status: string): boolean {
  return status === 'COMPLETED' || status === 'CANCELLED';
  }
  onStatusChange(order: OrderAdminResponse): void {
  Swal.fire({
    title: 'Xác nhận cập nhật trạng thái',
    text: `Bạn có chắc muốn chuyển trạng thái đơn hàng #${order.order_id}?`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Xác nhận',
    cancelButtonText: 'Hủy'
  }).then((result) => {
    if (result.isConfirmed) {
      this.orderService.updateOrderStatus(order.order_id).subscribe({
        next: () => {
          Swal.fire('Thành công', 'Trạng thái đơn hàng đã được cập nhật', 'success');
          this.getAllOrders(0, 6, '');
        },
        error: (err) => {
          Swal.fire('Lỗi', err.error.message || 'Không thể cập nhật trạng thái', 'error');
        }
      });
    }
  });
}
}
