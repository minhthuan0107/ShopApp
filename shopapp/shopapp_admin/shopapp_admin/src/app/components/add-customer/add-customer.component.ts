import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { MatInputModule } from '@angular/material/input';
import { NgbModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import Swal from 'sweetalert2';
import { CreateUserDto } from '../../dtos/create.user.dto';


@Component({
  selector: 'app-add-customer',
  standalone: true,
  imports: [CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    NgbModule,
    MatIconModule 
  ],
  templateUrl: './add-customer.component.html',
  styleUrl: './add-customer.component.scss'
})
export class AddCustomerComponent {
  @Output() userAdded = new EventEmitter<void>();
  postForm: FormGroup;
  showPassword: boolean = false;

  constructor( private fb: FormBuilder,
    private userService: UserService,
    private router: Router,
    private toastr: ToastrService,
  private modalService: NgbModal){ 
      this.postForm = this.fb.group({
      phone: ['', [Validators.required,Validators.minLength(9), Validators.pattern('^[0-9]*$')]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      retypePassword: ['', Validators.required],
      fullName: ['', Validators.required],
      address: ['', Validators.required],
      dateOfBirth: ['', [Validators.required, this.validateAge]],
    }, { validator: this.checkPasswordsMatch });}
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
//Trạng thái hiện thỉ mật khẩu
  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }
   open(content: any) {
    this.postForm.reset(); // Reset lại form về trạng thái ban đầu
    this.modalService.open(content, {
    centered: true,
    windowClass: 'custom-modal-md' 
   });

  }
   // Ví dụ emit sự kiện khi thêm thành công
  createNew() {
  if (this.postForm.invalid) {
    this.toastr.error('Vui lòng nhập đầy đủ thông tin.', 'Lỗi', {
      timeOut: 1500,
      progressBar: true,
    });
    return;
  }
  const userDto : CreateUserDto = {
    full_name: this.postForm.value.fullName,
    phone_number: this.postForm.value.phone,
    password: this.postForm.value.password,
    retype_password: this.postForm.value.retypePassword,
    address: this.postForm.value.address,
    date_of_birth: this.postForm.value.dateOfBirth,
    facebook_account_id: '',
    google_account_id: '',
  };

  this.userService.createUser(userDto).subscribe({
    next: () => {
      Swal.fire({
        icon: 'success',
        title: 'Thêm khách hàng thành công',
        showConfirmButton: false,
        timer: 1500,
        customClass: { popup: 'custom-swal' },
      }).then(() => {
        this.userAdded.emit(); // emit để reload bảng
        this.modalService.dismissAll(); // đóng modal
        this.postForm.reset(); // reset lại form
      });
    },
    error: (error) => {
      const errorMessage = error.error.message || 'Đã xảy ra lỗi';
      this.toastr.error(errorMessage, 'Lỗi', {
        timeOut: 1500,
        progressBar: true,
      });
    },
  });
}
}
