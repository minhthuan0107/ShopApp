import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { BrandService } from '../../services/brand.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastrService } from 'ngx-toastr';
import { CreateBrandDto } from '../../dtos/create.brand.dto';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-add-brand',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatIcon],
  templateUrl: './add-brand.component.html',
  styleUrl: './add-brand.component.scss'
})
export class AddBrandComponent {
  @Output() brandAdded = new EventEmitter<void>();
  brandForm: FormGroup;
  constructor(private fb: FormBuilder,
    private brandService: BrandService,
    private modalService: NgbModal,
    private toastr: ToastrService) {
    this.brandForm = this.fb.group({
      name: ['', Validators.required]
    });
  }
  open(content: any) {
    this.brandForm.reset(); // Reset lại form về trạng thái ban đầu
    this.modalService.open(content, {
      centered: true,
      windowClass: 'custom-modal-md'
    });
  }
  // Hàm thêm thương hiệu mới
  submitBrand() {
    if (this.brandForm.invalid) {
      this.toastr.error('Vui lòng nhập đầy đủ thông tin.', 'Lỗi', {
        timeOut: 1500,
        progressBar: true,
      });
      return;
    }
    const brandDto: CreateBrandDto = {
      name: this.brandForm.value.name
    };
    this.brandService.createNewBrand(brandDto).subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: 'Thêm thương hiệu thành công',
          showConfirmButton: false,
          timer: 1500,
          customClass: { popup: 'custom-swal' },
        }).then(() => {
          this.brandAdded.emit(); // emit để reload bảng
          this.modalService.dismissAll(); // đóng modal
          this.brandForm.reset(); // reset lại form
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
