import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { Component, Inject, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';

@Component({
  selector: 'app-user-select-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatPaginatorModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatCheckboxModule
  ],
  templateUrl: './user-select-dialog.component.html',
  styleUrls: ['./user-select-dialog.component.scss']
})
export class UserSelectDialogComponent implements OnInit {
  users: User[] = [];
  selectedUserIds: number[] = [];
  page = 0;
  size = 5;
  totalItems = 0;
  keyword = '';

  constructor(
    private dialogRef: MatDialogRef<UserSelectDialogComponent>,
    private userService: UserService,
    @Inject(MAT_DIALOG_DATA) public data: { couponCode: string }
  ) {}

  ngOnInit(): void {
    this.getAllUsers();
  }

  //Lấy danh sách user với tham số hiện tại
  getAllUsers(page: number = this.page, size: number = this.size, keyword: string = this.keyword): void {
    this.userService.getAllUsers(page, size, keyword || '').subscribe({
      next: (res) => {
        this.users = res.data.userResponses;
        this.totalItems = res.data.totalItems;
        this.page = page;
        this.size = size;
        this.keyword = keyword;
      },
      error: (err) => {
        console.error('❌ Lỗi:', err?.error?.message || 'Không thể lấy danh sách user');
      }
    });
  }

  //Tìm kiếm user, reset về trang 0
  onSearch(): void {
    this.getAllUsers(0, this.size, this.keyword);
  }
   //Chọn / bỏ chọn user
  toggleUserSelection(userId: number, checked: boolean): void {
    if (checked && !this.selectedUserIds.includes(userId)) {
      this.selectedUserIds.push(userId);
    } else if (!checked) {
      this.selectedUserIds = this.selectedUserIds.filter(id => id !== userId);
    }
  }

   // Kiểm tra xem user đã được chọn chưa
  isSelected(userId: number): boolean {
    return this.selectedUserIds.includes(userId);
  }
   //Sự kiện thay đổi trang
  onPageChange(event: PageEvent): void {
    this.getAllUsers(event.pageIndex, event.pageSize, this.keyword);
  }

   // Xác nhận và gửi danh sách ID user đã chọn về component cha
  confirm(): void {
    this.dialogRef.close(this.selectedUserIds);
  }

   // Hủy bỏ, đóng dialog
  cancel(): void {
    this.dialogRef.close();
  }
}
