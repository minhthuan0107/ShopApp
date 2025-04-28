import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { UserService } from '../../../services/user.service';
import Swal from 'sweetalert2';
import { UpdateProfileDto } from '../../../dtos/update-profile.dto';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.scss'
})
export class EditProfileComponent {
  @Input() user!: any;
  @Output() onCancel = new EventEmitter<void>();
  @Output() onSave = new EventEmitter<any>();

  profileForm!: FormGroup;

  constructor(private fb: FormBuilder,
    private userService: UserService) { }
  ngOnInit(): void {
    // Khởi tạo form và gán giá trị cho form nếu user có dữ liệu
    this.profileForm = this.fb.group({
      name: [this.user?.fullname || '', Validators.required],
      address: [this.user?.address || '', Validators.required],
      dateOfBirth: [this.formatDate(this.user?.date_of_birth) || '', [
        Validators.required,
        this.validateBirthDateNotInFuture
      ]]
    });
  }
  private formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toISOString().substring(0, 10); // Trả về yyyy-MM-dd
  }

  validateBirthDateNotInFuture(control: AbstractControl) {
    if (!control.value) return null;
    const inputDate = new Date(control.value);
    const today = new Date();
    // So sánh ngày tháng, loại bỏ phần thời gian
    inputDate.setHours(0, 0, 0, 0);
    today.setHours(0, 0, 0, 0);

    return inputDate > today ? { futureDate: true } : null;
  }
  save() {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }
    const updateProfile: UpdateProfileDto = {
      fullname: this.profileForm.value.name,
      address: this.profileForm.value.address,
      date_of_birth: this.profileForm.value.dateOfBirth,

    };
    this.userService.updateProfile(updateProfile).subscribe({
      next: (response) => {
        Swal.fire({
          icon: 'success',
          title: 'Thành công!',
          text: 'Cập nhật thông tin hồ sơ cá nhân thành công',
          showConfirmButton: false,
          timer: 1000
        }).then(() => {
          this.onSave.emit(response.data);
          // Cập nhật lại thông tin người dùng trong BehaviorSubject
          this.userService.setCurrentUser(response.data);
        });
      },
      error: (error) => {
        console.error("Lỗi!", error.error?.message || "Cập nhật thông tin hồ sơ thất bại");
      }
    });
  }

  cancel() {
    this.onCancel.emit();
  }
}
