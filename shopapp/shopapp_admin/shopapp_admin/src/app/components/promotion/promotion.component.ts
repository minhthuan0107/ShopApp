import { CommonModule } from '@angular/common';
import { Component, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { PromotionService } from '../../services/promotion.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Coupon } from '../../models/coupon.model';
import { AddCouponComponent } from '../add-coupon/add-coupon.component';
import Swal from 'sweetalert2';
import { UserSelectDialogComponent } from '../user-select-dialog/user-select-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { ToastrModule, ToastrService } from 'ngx-toastr';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-promotion',
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
    AddCouponComponent,
    MatSelectModule],
  templateUrl: './promotion.component.html',
  styleUrl: './promotion.component.scss'
})
export class PromotionComponent {
  totalItems: number = 0;
  keyword?: string = '';
  displayedColumns: string[] = ['id', 'code', 'type', 'value', 'min_order_value', 'quantity', 'expiry_date', 'is_active', 'actions', 'send'];
  dataSource = new MatTableDataSource<any>();

  // Dùng ViewChild để lấy đối tượng MatPaginator từ template
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  selectedCoupon!: Coupon;
  constructor(private promotionService: PromotionService,
    private dialog: MatDialog,
    private toastr: ToastrService) { }
  ngOnInit(): void {
    //Hiển thị danh sách người dùng
    this.getAllCoupons(0, 6, this.keyword);
  }

  // Sau khi view render xong thì gán
  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
  }

  applyFilter() {
    const pageSize = this.paginator?.pageSize || 6;
    const keyword = (this.keyword || '').trim().toLowerCase();
    this.getAllCoupons(0, pageSize, keyword);
  }

  onPageChange(event: PageEvent) {
    const page = event.pageIndex;      // Trang hiện tại (bắt đầu từ 0)
    const size = event.pageSize;
    this.getAllCoupons(page, size, this.keyword);      // Số lượng mỗi trang

  }
  onCouponAdded() {
    // Reload lại danh sách khách hàng
    this.getAllCoupons(0, this.paginator?.pageSize || 6, this.keyword);
  }
  getAllCoupons(page: number, size: number, keyword: string = '') {
    this.promotionService.getAllCoupons(page, size, keyword).subscribe({
      next: (res) => {
        this.dataSource.data = res.data.couponResponses;
        this.paginator.length = res.data.totalItems; // Tổng số mục để phân trang đúng
      },
      error: (err) => {
        console.error('❌ Lỗi:', err?.error?.message || 'Lỗi khi lấy danh sách khuyến mãi');
      }
    });
  }
  //Hàm khóa / mở tài khoản người dùng
  toggleCouponStatus(coupon: any) {
    const action = coupon.is_active ? 'vô hiệu hóa' : 'kích hoạt';
    Swal.fire({
      title: `Bạn có chắc chắn muốn ${action} mã giảm giá này?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: `Có, ${action}`,
      cancelButtonText: 'Hủy bỏ'
    }).then((result) => {
      if (result.isConfirmed) {
        this.promotionService.toggleCouponStatus(coupon.id).subscribe({
          next: () => {
            Swal.fire({
              icon: 'success',
              title: `Mã giảm giá đã được ${action} thành công!`,
              timer: 1500,
              showConfirmButton: false
            });
            this.getAllCoupons(0, this.paginator?.pageSize || 6, this.keyword); // Reload lại danh sách
          },
          error: (error) => {
            console.error("Lỗi!", error.error?.message || "Cập nhật trạng thái thất bại");
          }
        });
      }
    });
  }
  sendToAllUsers(coupon: Coupon) {
    this.promotionService.sendCouponToAllUsers(coupon.code).subscribe({
      next: () => this.toastr.success('Đã gửi mã đến tất cả người dùng'),
      error: () => this.toastr.error('Gửi thất bại'),
    });
  }

  openUserSelectDialog(coupon: Coupon) {
    const dialogRef = this.dialog.open(UserSelectDialogComponent, {
      width: '600px',
      data: { couponCode: coupon.code },
    });
    dialogRef.afterClosed().subscribe((selectedUserIds: number[]) => {
      if (selectedUserIds?.length) {
        this.promotionService.sendCouponToSelectedUsers(coupon.code, selectedUserIds).subscribe({
          next: () => this.toastr.success('Đã gửi mã đến người dùng được chọn'),
          error: () => this.toastr.error('Gửi thất bại'),
        });
      }
    });
  }
  onSendOptionChange(event: Event, coupon: Coupon) {
    const selectedValue = (event.target as HTMLSelectElement).value;
    if (selectedValue === 'all') {
      this.sendToAllUsers(coupon);
    } else if (selectedValue === 'select') {
      this.openUserSelectDialog(coupon);
    }
  }
}
