import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Product } from '../../models/product.model';
import { CategoryService } from '../../services/category.service';
import { BrandService } from '../../services/brand.service';
import { Category } from '../../models/category.model';
import { Brand } from '../../models/brand.model';
import { UpdateProductDto } from '../../dtos/update.product.dto';
import Swal from 'sweetalert2';
import { ProductService } from '../../services/product.service';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';

@Component({
  selector: 'app-edit-product',
  standalone: true,
  imports: [CommonModule, FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    NgbModule,
    MatIconModule,
    NgxMaskDirective],
  providers: [
    provideNgxMask() // ✅ BẮT BUỘC: cung cấp config cho ngx-mask
  ],
  templateUrl: './edit-product.component.html',
  styleUrl: './edit-product.component.scss'
})
export class EditProductComponent {
  @Input() product!: Product;
  @Output() productUpdated = new EventEmitter<void>(); // emit khi cập nhật xong
  editForm!: FormGroup;
  categories: Category[] = [];
  brands: Brand[] = [];
  constructor(private fb: FormBuilder,
    private categoryService: CategoryService,
    private brandService: BrandService,
    private productService: ProductService
  ) { }
  ngOnInit(): void {
    this.editForm = this.fb.group({
      name: [this.product?.name || '', [Validators.required, Validators.minLength(3), Validators.maxLength(200)]],
      price: [this.product?.price || 0, [Validators.required, Validators.min(0), Validators.max(1000000000)]],
      quantity: [this.product?.quantity || 0, [Validators.required, Validators.min(0)]],
      urlImage: [this.product?.url_image || ''],
      description: [this.product?.description || ''],
      categoryId: [this.product?.category?.id || null, Validators.required],
      brandId: [this.product?.brand?.id || null, Validators.required]
    });
    this.loadCategories();
    this.loadBrands();
  }


  //Hàm lấy danh sách danh mục
  loadCategories() {
    this.categoryService.getAllCategories().subscribe((res) => {
      this.categories = res.data;
    });
  }
  //Hàm lấy danh sách danh mục
  loadBrands() {
    this.brandService.getAllBrands().subscribe((res) => {
      this.brands = res.data;
    });
  }
  //Hàm cập nhật thông tin người dùng
  updateProduct() {
    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      return;
    }
    const formValue = this.editForm.value;
    const updatedProduct: UpdateProductDto = {
      name: formValue.name,
      price: Number((formValue.price + '').replace(/,/g, '')), // ✅ Bỏ dấu phẩy
      quantity: formValue.quantity,
      url_image: formValue.urlImage,
      description: formValue.description,
      category_id: formValue.categoryId,
      brand_id: formValue.brandId
    };

    this.productService.updateProduct(this.product.id, updatedProduct).subscribe({
      next: (res) => {
        Swal.fire({
          icon: 'success',
          title: 'Cập nhật sản phẩm thành công',
          showConfirmButton: false,
          timer: 1500,
          customClass: { popup: 'custom-swal' },
        }).then(() => {
          this.editForm.reset();            // Reset form nếu muốn
          this.productUpdated.emit();      // Gửi sự kiện về component cha để đóng modal hoặc reload
        });
      },
      error: (error) => {
        console.error("Lỗi!", error.error?.message || "Cập nhật thông tin sản phẩm thất bại");
      }
    });
  }
}

