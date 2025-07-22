import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { NgbModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ToastrService } from 'ngx-toastr';
import { CreateCouponDto } from '../../dtos/create.coupon.dto';
import Swal from 'sweetalert2';
import { PromotionService } from '../../services/promotion.service';
import moment from 'moment';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';

@Component({
  selector: 'app-add-coupon',
  standalone: true,
  imports: [CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    NgbModule,
    MatIconModule,
    NgxMaskDirective],
  providers: [
    provideNgxMask() // ✅ BẮT BUỘC: cung cấp config cho ngx-mask
  ],
  templateUrl: './add-coupon.component.html',
  styleUrl: './add-coupon.component.scss'
})
export class AddCouponComponent {
  @Output() couponAdded = new EventEmitter<void>();
  couponForm: FormGroup;
  constructor(private fb: FormBuilder,
    private modalService: NgbModal,
    private toastr: ToastrService,
    private promotionService: PromotionService
  ) {
    this.couponForm = this.fb.group({
      code: ['', Validators.required],
      type: ['fixed', Validators.required],
      value: [null, [Validators.required, Validators.min(0.01)]],
      minOrderValue: [0, [Validators.min(0)]],
      quantity: [1, [Validators.required, Validators.min(1)]],
      expiryDate: [null, [Validators.required, this.futureDateValidator]]
    });
  }
  //Hàm validate ngày tháng năm hết hạn coupon
  futureDateValidator(control: any) {
    const selectedDate = moment(control.value);
    const today = moment().startOf('day');
    return selectedDate.isAfter(today)
      ? null
      : { futureDate: true };
  }
  open(content: any) {
    this.couponForm.reset(); // Reset lại form về trạng thái ban đầu
    this.modalService.open(content, {
      centered: true,
      windowClass: 'custom-modal-md'
    });
  }

  // Ví dụ emit sự kiện khi thêm thành công
  submitCoupon() {
    if (this.couponForm.invalid) {
      this.toastr.error('Vui lòng nhập đầy đủ thông tin.', 'Lỗi', {
        timeOut: 1500,
        progressBar: true,
      });
      return;
    }
    const couponDto: CreateCouponDto = {
      code: this.couponForm.value.code,
      type: this.couponForm.value.type,
      value: Number((this.couponForm.value.value + '').replace(/,/g, '')),
      min_order_value: Number((this.couponForm.value.minOrderValue + '').replace(/,/g, '')),
      quantity: this.couponForm.value.quantity,
      expiry_date: this.couponForm.value.expiryDate,
    };

    this.promotionService.createCoupon(couponDto).subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: 'Thêm mã giảm giá thành công',
          showConfirmButton: false,
          timer: 1500,
          customClass: { popup: 'custom-swal' },
        }).then(() => {
          this.couponForm.reset();            // Reset form sau khi alert xong
          this.couponAdded.emit();            // Emit event để reload bảng
          this.modalService.dismissAll();     // Đóng modal sau alert
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
