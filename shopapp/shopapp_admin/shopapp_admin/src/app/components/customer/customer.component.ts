import { CommonModule } from '@angular/common';
import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { UserService } from '../../services/user.service';
import { FormsModule } from '@angular/forms';
import { AddCustomerComponent } from '../add-customer/add-customer.component';
import { MatTooltipModule } from '@angular/material/tooltip';
import { User } from '../../models/user.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { EditCustomerComponent } from '../edit-customer/edit-customer.component';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-customer',
  standalone: true,
  imports: [CommonModule, FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    AddCustomerComponent,
    MatTooltipModule,
    EditCustomerComponent],
  templateUrl: './customer.component.html',
  styleUrl: './customer.component.scss'
})
export class CustomerComponent implements OnInit {
  totalItems: number = 0;
  keyword?: string = '';
  displayedColumns: string[] = ['id', 'full_name', 'phone_number', 'address', 'date_of_birth', 'create_at', 'is_active', 'actions'];
  dataSource = new MatTableDataSource<any>();

  // 👇 Dùng ViewChild để lấy đối tượng MatPaginator từ template
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('editModal') editModal!: TemplateRef<any>;
  selectedUser!: User;
  constructor(private userService: UserService,
    private modalService: NgbModal
  ) { }
  ngOnInit(): void {
    //Hiển thị danh sách người dùng
    this.getAllUsers(0, 6, this.keyword);
  }
  // Sau khi view render xong thì gán
  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
  }

  applyFilter() {
    const pageSize = this.paginator?.pageSize || 6;
    const keyword = (this.keyword || '').trim().toLowerCase();
    this.getAllUsers(0, pageSize, keyword);
  }

  onPageChange(event: PageEvent) {
    const page = event.pageIndex;      // Trang hiện tại (bắt đầu từ 0)
    const size = event.pageSize;       // Số lượng mỗi trang
    this.getAllUsers(page, size, this.keyword); // Gọi lại API để lấy dữ liệu trang mới
  }
  getAllUsers(page: number, size: number, keyword: string = '') {
    this.userService.getAllUsers(page, size, keyword).subscribe({
      next: (res) => {
        this.dataSource.data = res.userResponses;
        this.paginator.length = res.totalItems; // Tổng số mục để phân trang đúng
      },
      error: (err) => {
            console.error('❌ Lỗi:', err?.error?.message || 'Lỗi khi lấy danh sách khách hàng');
          }
    });
  }
  onUserAdded() {
    // Reload lại danh sách khách hàng
    this.getAllUsers(0, this.paginator?.pageSize || 6, this.keyword);
  }
  editUser(user: any) {
    this.selectedUser = user;
    this.modalService.open(this.editModal, { size: 'lg', centered: true });
  }
  onUserUpdated(): void {
    this.modalService.dismissAll(); // đóng modal
    this.getAllUsers(0, this.paginator?.pageSize || 6, this.keyword); // reload danh sách
  }
  //Hàm khóa / mở tài khoản người dùng
  toggleActive(user: any) {
    const action = user.is_active ? 'khóa' : 'mở khóa';
    Swal.fire({
      title: `Bạn có chắc chắn muốn ${action} tài khoản này?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: `Có, ${action}`,
      cancelButtonText: 'Hủy bỏ'
    }).then((result) => {
      if (result.isConfirmed) {
        this.userService.toggleUserStatus(user.id).subscribe({
          next: () => {
            Swal.fire({
              icon: 'success',
              title: `Tài khoản đã được ${action} thành công!`,
              timer: 1500,
              showConfirmButton: false
            });
            this.getAllUsers(0, this.paginator?.pageSize || 6, this.keyword); // Reload lại danh sách
          },
          error: (error) => {
            console.error("Lỗi!", error.error?.message || "Cập nhật trạng thái thất bại");
          }
        });
      }
    });
  }
}
