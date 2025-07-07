import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CategoryService } from '../../services/category.service';
import { MatIcon } from '@angular/material/icon';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import Swal from 'sweetalert2';
import { CreateCategoryDto } from '../../dtos/create.category.dto';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-add-category',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatIcon],
  templateUrl: './add-category.component.html',
  styleUrl: './add-category.component.scss'
})
export class AddCategoryComponent {
  @Output() categoryAdded = new EventEmitter<void>();
  categoryForm: FormGroup;
  constructor(private fb: FormBuilder,
    private categoryService: CategoryService,
    private modalService: NgbModal,
    private toastr: ToastrService) {
    this.categoryForm = this.fb.group({
      name: ['', Validators.required]
    });
  }
  open(content: any) {
    this.categoryForm.reset(); // Reset lại form về trạng thái ban đầu
    this.modalService.open(content, {
      centered: true,
      windowClass: 'custom-modal-md'
    });
  }
  // Hàm thêm danh mục mới
  submitCategory() {
    if (this.categoryForm.invalid) {
      this.toastr.error('Vui lòng nhập đầy đủ thông tin.', 'Lỗi', {
        timeOut: 1500,
        progressBar: true,
      });
      return;
    }
    const categoryDto: CreateCategoryDto = {
      name: this.categoryForm.value.name
    };
    this.categoryService.createNewCategory(categoryDto).subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: 'Thêm danh mục thành công',
          showConfirmButton: false,
          timer: 1500,
          customClass: { popup: 'custom-swal' },
        }).then(() => {
          this.categoryAdded.emit(); // emit để reload bảng
          this.modalService.dismissAll(); // đóng modal
          this.categoryForm.reset(); // reset lại form
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
