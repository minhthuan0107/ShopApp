  import { CommonModule } from '@angular/common';
  import { Component, EventEmitter, Output } from '@angular/core';
  import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
  import { MatIconModule } from '@angular/material/icon';
  import { MatInputModule } from '@angular/material/input';
  import { NgbModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
  import { CategoryService } from '../../services/category.service';
  import { BrandService } from '../../services/brand.service';
  import { ProductService } from '../../services/product.service';
  import { ProductDto } from '../../dtos/product.dto';
  import { finalize } from 'rxjs';
  import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
  import Swal from 'sweetalert2';

  @Component({
    selector: 'app-add-product',
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
    templateUrl: './add-product.component.html',
    styleUrl: './add-product.component.scss'
  })
  export class AddProductComponent {
    addProductForm!: FormGroup;
    selectedFiles: File[] = [];
    categories: any[] = [];
    brands: any[] = [];
    isLoading = false;
    @Output() productAdded = new EventEmitter<void>();

    constructor(
      private fb: FormBuilder,
      private productService: ProductService,
      private categoryService: CategoryService,
      private brandService: BrandService,
      private modalService: NgbModal
    ) { }
    ngOnInit(): void {
      this.initForm();
      this.loadCategories();
      this.loadBrands();
    }
    initForm(): void {
      this.addProductForm = this.fb.group({
        name: ['', Validators.required],
        price: ['', [Validators.required, Validators.min(0)]],
        quantity: [0, [Validators.required, Validators.min(0)]],
        description: [''],
        categoryId: [null, Validators.required],
        brandId: [null, Validators.required]
      });
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
    open(content: any): void {
      this.modalService.open(content, { centered: true });
    }

    onFileSelected(event: Event): void {
      const input = event.target as HTMLInputElement;
      if (input.files) {
        this.selectedFiles = Array.from(input.files);
      }
    }

    createProduct(): void {
      if (this.addProductForm.invalid || this.selectedFiles.length === 0) return;
      this.isLoading = true;

      const formValue = this.addProductForm.value;
      const productDto: ProductDto = {
        name: formValue.name,
        price: Number((formValue.price + '').replace(/,/g, '')),
        quantity: formValue.quantity,
        description: formValue.description,
        url_image: '',
        category_id: formValue.categoryId,
        brand_id: formValue.brandId
      };

      this.productService.createProduct(productDto).subscribe({
        next: (response) => {
          const productId = response.data?.id;
          const formData = new FormData();
          this.selectedFiles.forEach(file => formData.append('files', file));

          this.productService.uploadImages(productId, formData).subscribe({
            next: () => {
              setTimeout(() => {
              Swal.fire('Thành công', 'Tạo sản phẩm thành công', 'success');
              this.resetForm();
              this.productAdded.emit();
              this.modalService.dismissAll();
              this.isLoading = false;
            }, 300);
          },
            error: (err) => {
              const msg = err?.error?.message || 'Tạo sản phẩm thất bại';
              Swal.fire('Thất bại', msg, 'error');
              this.resetForm();
              this.modalService.dismissAll();
              this.isLoading = false;
            }
          });
        },
        error: () => {
          Swal.fire('Thất bại', 'Tạo sản phẩm thất bại', 'error');
          this.resetForm();
          this.isLoading = false;
        }
      });
    }

    resetForm(): void {
      this.addProductForm.reset();
      this.selectedFiles = [];
    }

  }
