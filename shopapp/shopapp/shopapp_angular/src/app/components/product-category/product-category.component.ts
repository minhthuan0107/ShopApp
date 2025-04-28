import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BrandService } from '../../services/brand.service';
import { Brand } from '../../models/brand';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-product-category',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './product-category.component.html',
  styleUrl: './product-category.component.scss'
})
export class ProductCategoryComponent implements OnInit {
  products: Product[] = [];
  filteredProducts: Product[] = []; // Lưu danh sách sau khi lọc
  categoryId!: number;
  categoryName: string = '';
  currentPage: number = 1;
  itemsPerPage: number = 12;
  totalPages: number = 1;
  totalItems: number = 1;
  brands: Brand[] = [];
  selectedBrands: Brand[] = [];
  minPrice!: number; // Giá tối thiểu
  maxPrice!: number; // Giá tối đa
  enteredMinPrice!:number;
  enteredMaxPrice!:number;
  sortBy: string | null = null;
  brandIds: number[] = [];
  apiTimeout: any;
  loading: boolean = false;
  isFirstLoad: boolean = true;
  minPriceDisplay?: string;
  maxPriceDisplay?: string;
  constructor(private productService: ProductService,
    private route: ActivatedRoute,
    private brandService: BrandService) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.categoryId = +id;
        this.currentPage = 1;
        this.getProductsByCategoryId();
        this.getBrandsByCategoryId();
        this.getPriceRange();
      }
    });
  }

  getProductsByCategoryId() {
    this.loading = true;
    const brandIds: number[] = this.selectedBrands.length
      ? this.selectedBrands.map(b => b.id) : [];
    this.productService.getProductsByCategoryId(
      this.categoryId,
      this.currentPage - 1,
      this.itemsPerPage,
      brandIds,
      this.enteredMinPrice ?? undefined,
      this.enteredMaxPrice ?? undefined,
      this.sortBy ?? undefined
    ).pipe(finalize(() => {
      if (this.isFirstLoad) {
        this.loading = false; // Lần đầu tiên: Tắt ngay lập tức
        this.isFirstLoad = false;
      } else {
        setTimeout(() => {
          this.loading = false; // Các lần sau: Delay 500ms
        }, 500);
      }
    }))
      .subscribe({
        next: (response: any) => {
          this.products = response.productResponses;
          this.totalPages = response.totalPages;
          this.totalItems = response.totalItems;
          this.categoryName = response.productResponses.length > 0
            ? response.productResponses[0].category.name.toUpperCase()
            : '';
        },
        error: (error: any) => {
          console.error("Lỗi!", error.error || "Lấy danh sách sản phẩm thất bại", "error");
        }
      });
  }
  //Hàm lấy giá trị nhỏ nhất và lớn nhât của 
  getPriceRange() {
    this.productService.getPriceRange().subscribe({
      next: (response) => {
        this.minPrice = response.data.minPrice;
        this.maxPrice = response.data.maxPrice;
        this.minPriceDisplay = this.formatPrice(this.minPrice); 
        this.maxPriceDisplay = this.formatPrice(this.maxPrice); 
      },
      error: (error: any) => {
        console.error("Lỗi!", error.error?.message || "Lấy giá tiền nhỏ nhất và lớn nhất của sản phẩm thất bại", "error");
      }
    });
  }
  onMinPriceInput(event: any) {
    const rawValue = event.target.value.replace(/[^0-9]/g, '');
    this.enteredMinPrice = rawValue ? parseInt(rawValue, 10) : 0;
    this.minPriceDisplay = this.formatPrice(this.enteredMinPrice);
  }
  
  // Khi người dùng rời khỏi ô input
  onMinPriceBlur(event: any) {
    if (this.enteredMinPrice < this.minPrice) {
      this.enteredMinPrice = this.minPrice;
      this.minPriceDisplay = this.formatPrice(this.enteredMinPrice);
    } else {
      this.minPriceDisplay = this.formatPrice(this.enteredMinPrice);
    }
  }
  onMaxPriceInput(event: any) {
    const rawValue = event.target.value.replace(/[^0-9]/g, '');
    this.enteredMaxPrice = rawValue ? parseInt(rawValue, 10) : 0;
    this.maxPriceDisplay = this.formatPrice(this.enteredMaxPrice);
  }
  // Gọi API khi người dùng nhập xong giá trị Max
  onMaxPriceBlur(event: any) {
    if (this.enteredMaxPrice > this.maxPrice) {
      this.enteredMaxPrice = this.maxPrice
      this.maxPriceDisplay = this.formatPrice(this.enteredMaxPrice);
    } else {
      this.maxPriceDisplay = this.formatPrice( this.enteredMaxPrice);
    }
    this.getProductsByCategoryId();
  }
  formatPrice(value: number | null): string {
    return value !== null ? value.toLocaleString('vi-VN').replace(/\./g, ',') : '';
  }

  getBrandsByCategoryId() {
    this.brandService.getBrandsByCategoryId(this.categoryId).subscribe({
      next: (response) => {
        if (response && response.data) {
          this.brands = response.data;
        }
      },
      error: (error: any) => {
        console.error("Lỗi!", error.error?.message || "Lấy danh sách thương hiệu thất bại", "error");
      }
    });
  }

  onPageChange(page: number) {
    if (!this.categoryId) return;
    this.currentPage = Math.min(Math.max(page, 1), this.totalPages);
    this.getProductsByCategoryId();
  }
  isFirstPage(): boolean {
    return this.currentPage === 1;
  }

  isLastPage(): boolean {
    return this.currentPage == this.totalPages;
  }

  toggleBrand(brand: Brand) {
    this.selectedBrands = [...this.selectedBrands];
    const index = this.selectedBrands.findIndex(b => b.id === brand.id);
    if (index === -1) {
      this.selectedBrands.push(brand);
    } else {
      this.selectedBrands.splice(index, 1);
    }
    // Xóa timeout cũ nếu có (tránh gọi API liên tục)
    if (this.apiTimeout) {
      clearTimeout(this.apiTimeout);
    }
    // Đặt timeout để gọi API sau 1 giây
    this.apiTimeout = setTimeout(() => {
      this.getProductsByCategoryId();
    }, 500);
  }

  // Kiểm tra thương hiệu được chọn
  isBrandSelected(brand: Brand): boolean {
    return this.selectedBrands.some(b => b.id === brand.id);
  }
  onSortChange(event: any) {
    const value = event.target.value;
    if (value) {
      this.sortBy = value;
    } else {
      this.sortBy = null;
    }
    // Gọi hàm cập nhật danh sách sản phẩm với delay
    clearTimeout(this.apiTimeout); // Xóa timeout cũ nếu có
    this.apiTimeout = setTimeout(() => {
      this.getProductsByCategoryId(); // Gọi API sau 500ms
    }, 500);
  }
}
