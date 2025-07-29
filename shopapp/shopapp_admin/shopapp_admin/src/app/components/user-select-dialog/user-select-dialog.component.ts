import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { Component, Inject } from '@angular/core';
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
  imports: [ CommonModule,
    FormsModule,
    MatPaginatorModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatCheckboxModule],
  templateUrl: './user-select-dialog.component.html',
  styleUrl: './user-select-dialog.component.scss'
})
export class UserSelectDialogComponent {
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

  getAllUsers() {
    this.userService.getAllUsers(this.page, this.size, this.keyword).subscribe({
      next: (res) => {
        this.users = res.data.userResponses;
        this.totalItems = res.data.totalItems;
      },
      error: (err) => {
        console.error('❌ Lỗi:', err?.error?.message || 'Không thể lấy danh sách user');
      }
    });
  }

  toggleUserSelection(userId: number, checked: boolean) {
    if (checked && !this.selectedUserIds.includes(userId)) {
      this.selectedUserIds.push(userId);
    } else if (!checked) {
      this.selectedUserIds = this.selectedUserIds.filter(id => id !== userId);
    }
  }

  isSelected(userId: number): boolean {
    return this.selectedUserIds.includes(userId);
  }

  onPageChange(event: PageEvent) {
    this.page = event.pageIndex;
    this.size = event.pageSize;
    this.getAllUsers();
  }

  confirm() {
    this.dialogRef.close(this.selectedUserIds);
  }

  cancel() {
    this.dialogRef.close();
  }
}

