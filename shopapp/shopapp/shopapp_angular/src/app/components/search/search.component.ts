import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { finalize } from 'rxjs';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './search.component.html',
  styleUrl: './search.component.scss'
})

export class SearchComponent {
   products: Product[] = [];
   filteredProducts: Product[] = []; // Lưu danh sách sau khi lọc
   keyword! : string;
   categoryName: string = '';
   currentPage: number = 1;
   itemsPerPage: number = 12;
   totalPages: number = 1;
   totalItems: number = 1;
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
     private route: ActivatedRoute,) { }
 
     ngOnInit(): void {
      this.route.params.subscribe(params => {
        this.keyword = params['keyword'];
         this.searchProducts();
         this.getPriceRange();
      });
    }
 
    searchProducts()  {
      this.loading = true;
      this.productService.searchProducts(
        this.keyword,
        this.currentPage - 1,
        this.itemsPerPage,
        this.enteredMinPrice ?? undefined,
        this.enteredMaxPrice ?? undefined,
        this.sortBy ?? undefined
      ).pipe(
        finalize(() => {
          if (this.isFirstLoad) {
            this.loading = false;
            this.isFirstLoad = false;
          } else {
            setTimeout(() => {
              this.loading = false;
            }, 500);
          }
        })
      ).subscribe({
        next: (response: any) => {
          this.products = response.productResponses;
          this.totalPages = response.totalPages;
          this.totalItems = response.totalItems;
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
     this.searchProducts();
   }
   formatPrice(value: number | null): string {
     return value !== null ? value.toLocaleString('vi-VN').replace(/\./g, ',') : '';
   }
   onPageChange(page: number) {
     if (!this.keyword) return;
     this.currentPage = Math.min(Math.max(page, 1), this.totalPages);
     this.searchProducts();
   }
   isFirstPage(): boolean {
     return this.currentPage === 1;
   }
 
   isLastPage(): boolean {
     return this.currentPage == this.totalPages;
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
       this.searchProducts(); // Gọi API sau 500ms
     }, 500);
   }
 }

