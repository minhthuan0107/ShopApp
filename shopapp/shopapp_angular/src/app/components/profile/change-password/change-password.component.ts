import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { UserService } from '../../../services/user.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.scss'
})
export class ChangePasswordComponent {
  @Output() onCancel = new EventEmitter<void>();
  @Output() onPasswordChanged = new EventEmitter<void>();

  passwordForm!: FormGroup;

  constructor(private fb: FormBuilder,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    this.passwordForm = this.fb.group(
      {
        currentPassword: ['', Validators.required],
        newPassword: ['', [Validators.required, Validators.minLength(6)]],
        confirmPassword: ['', Validators.required]
      },
      { validators: this.passwordsMatchValidator }
    );
  }
  passwordsMatchValidator(form: FormGroup) {
    const newPassword = form.get('newPassword')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    return newPassword === confirmPassword ? null : { passwordMismatch: true };
  }

  changePassword(): void {
    if (this.passwordForm.invalid) return;
    const { currentPassword, newPassword } = this.passwordForm.value;
    const passwordData = {
      currentPassword,
      newPassword
    };
    this.userService.changePassword(passwordData).subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: 'Thành công!',
          text: 'Thay đổi mật khẩu thành công',
          timer: 1500,
          showConfirmButton: false
        });
        this.onPasswordChanged.emit();
      },
      error: (error: any) => {
        Swal.fire({
          icon: 'error',
          title: 'Lỗi!',
          timer: 1500,
          text: error.error?.message || 'Thay đổi mật khẩu thất bại'
        });
      }
    });
  }
}
