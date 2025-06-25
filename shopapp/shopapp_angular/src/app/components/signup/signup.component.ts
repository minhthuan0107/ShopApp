import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import Swal from 'sweetalert2';
import { UserService } from './../../services/user.service';
import { SignupDto } from './../../dtos/signup.dto';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-signup',
  standalone: true,
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.scss',
  imports: [CommonModule, ReactiveFormsModule]
})
export class SignupComponent {
  signupForm: FormGroup;
  showPassword: boolean = false;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private router: Router,
    private toastr: ToastrService
  ) {
    this.signupForm = this.fb.group({
      phone: ['', [Validators.required,Validators.minLength(9), Validators.pattern('^[0-9]*$')]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      retypePassword: ['', Validators.required],
      fullName: ['', Validators.required],
      address: ['', Validators.required],
      dateOfBirth: ['', [Validators.required, this.validateAge]],
      isAccepted: [false, Validators.requiredTrue],
    }, { validator: this.checkPasswordsMatch });
  }

  // Hàm kiểm tra mật khẩu có khớp không
  checkPasswordsMatch(form: FormGroup) {
    const password = form.get('password')?.value;
    const retypePassword = form.get('retypePassword')?.value;
    return password === retypePassword ? null : { passwordMisMatch: true };
  }

  // Kiểm tra tuổi có đủ 18 không
  validateAge(control: any) {
    if (!control.value) return null;
    const birthDate = new Date(control.value);
    const today = new Date();
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }
    return age >= 18 ? null : { invalidAge: true };
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  signup() {
    if (this.signupForm.invalid) {
      this.toastr.error('Vui lòng nhập đầy đủ thông tin.', 'Lỗi', {
        timeOut: 1500,
        progressBar: true,
      });
      return;
    }
    const signupDto: SignupDto = {
      full_name: this.signupForm.value.fullName,
      phone_number: this.signupForm.value.phone,
      address: this.signupForm.value.address,
      password: this.signupForm.value.password,
      retype_password: this.signupForm.value.retypePassword,
      date_of_birth: this.signupForm.value.dateOfBirth,
      facebook_account_id: '',
      google_account_id: '',
    };
    this.userService.signup(signupDto).subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: 'Đăng kí thành công',
          showConfirmButton: false,
          timer: 1500,
          customClass: { popup: 'custom-swal' },
        }).then(() => {
          setTimeout(() => {
            this.router.navigate(['/signin']);
          }, 500);
        });
      },
      error: (error) => {
        const errorMessage = error.error.message;
        this.toastr.error(errorMessage, 'Lỗi', {
          timeOut: 1500,
          progressBar: true,
        });
      },
    });
  }
  //Chuyển đến đăng nhập
  goToSignIn() {
    this.router.navigate(['/signin']);
  }
}
