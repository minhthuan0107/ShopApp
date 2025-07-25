import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { User } from '../../models/user.model';
import { UserService } from '../../services/user.service';
import Swal from 'sweetalert2';
import { UpdateUserDto } from '../../dtos/update.user.dto';
import moment from 'moment';

@Component({
  selector: 'app-edit-customer',
  standalone: true,
  imports: [CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    NgbModule,
    MatIconModule],
  templateUrl: './edit-customer.component.html',
  styleUrl: './edit-customer.component.scss'
})
export class EditCustomerComponent {
  @Input() user!: User;
  @Output() userUpdated = new EventEmitter<void>();
  editForm!: FormGroup;

  constructor(private fb: FormBuilder,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    this.editForm = this.fb.group({
      name: [this.user?.full_name || '', Validators.required],
      address: [this.user?.address || '', Validators.required],
      dateOfBirth: [
        this.formatDate(this.user?.date_of_birth) || '',
        [Validators.required, this.validateBirthDateNotInFuture]
      ]
    });
  }
  private formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toISOString().substring(0, 10); // Trả về yyyy-MM-dd
  }
  //hàm validate ngày tháng năm
  validateBirthDateNotInFuture(control: AbstractControl) {
    if (!control.value) return null;
    const inputDate = moment(control.value).startOf('day');
    const today = moment().startOf('day');
    return inputDate.isAfter(today) ? { futureDate: true } : null;
  }
  //Hàm cập nhật thông tin người dùng
  updateUser() {
    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      return;
    }
    const formValue = this.editForm.value;
    const updatedUser: UpdateUserDto = {
      full_name: formValue.name,
      address: formValue.address,
      date_of_birth: formValue.dateOfBirth
    };
    // Gọi API để cập nhật
    this.userService.updateUser(this.user.id, updatedUser).subscribe({
      next: (res) => {
        console.log('✅ Server phản hồi:', res);
        Swal.fire({
          icon: 'success',
          title: 'Cập nhật thông tin hồ sơ khách thành công',
          showConfirmButton: false,
          timer: 1500,
          customClass: { popup: 'custom-swal' },
        }).then(() => {
          this.editForm.reset(); // reset lại form
          this.userUpdated.emit(); // Gửi về component cha để đóng modal
        });
      },
      error: (error) => {
        console.error("Lỗi!", error.error?.message || "Cập nhật thông tin hồ sơ khách hàng thất bại");
      }
    });
  }
}
