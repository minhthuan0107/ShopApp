import { CommonModule } from '@angular/common';
import { Product } from './../../models/product.model';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ProductService } from '../../services/product.service';
import { NgxPaginationModule } from 'ngx-pagination';
import { Router, RouterModule } from '@angular/router';
import { SlickCarouselModule } from 'ngx-slick-carousel';
import { UserService } from '../../services/user.service';
import { CartDetail } from '../../dtos/cartdetail.dto';
import { CartService } from '../../services/cart.service';
import { ToastrService } from 'ngx-toastr';
import Swal from 'sweetalert2';
import { filter } from 'rxjs';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, RouterModule, SlickCarouselModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  userId: number | null = null;
  products: Product[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 12;
  pages: number[] = [];
  totalPages: number = 1;
  totalItems: number = 1;
  visiblePages: number[] = [];
  slides = [
    { id: 1, url: 'assets/banner/banner-tet-6.jpg' },
    { id: 2, url: 'assets/banner/banner-tet-3.jpg' },
    { id: 3, url: 'assets/banner/banner-tet-6.jpg' }
  ];
  slideConfig = {
    slidesToShow: 2,      // Hiển thị 1 hình ảnh
    slidesToScroll: 1,    // Lướt 1 hình ảnh tại một thời điểm
    autoplay: true,       // Tự động lướt
    autoplaySpeed: 2000,  // Thời gian giữa các lần lướt (2 giây)
    dots: true,           // Hiển thị dấu chấm điều hướng
    infinite: true,       // Cho phép lướt vô hạn
    arrows: true          // Hiển thị nút điều hướng (next/prev)
  };

  constructor(private productService: ProductService,
    private router: Router,
    private userService: UserService,
    private cartService: CartService,
    private toastr: ToastrService
  ) { }

  ngOnInit(): void {
    this.getProducts(this.currentPage - 1, this.itemsPerPage);
    this.userService.user$.pipe(filter(user => !!user)).subscribe(user => {
        if (user) {
          this.userId = user.id;
        }
      });
  }
  getProducts(page: number, limit: number) {
    this.productService.getProducts(page, limit).subscribe({
      next: (response: any) => {
        // Lưu danh sách sản phẩm và tổng số trang
        this.products = response.productResponses;
        this.totalPages = response.totalPages;
        this.totalItems = response.totalItems;
      },
      complete: () => {
      },
      error: (error: any) => {
        console.error('Không tìm thấy danh sách sản phẩm', error);
      }
    });
  }
  onPageChange(page: number) {
    if (page < 1 || page > this.totalPages) return; // Kiểm tra hợp lệ
    this.currentPage = page;
    this.getProducts(this.currentPage - 1, this.itemsPerPage);
  }
  isFirstPage(): boolean {
    return this.currentPage === 1;
  }
  isLastPage(): boolean {
    return this.currentPage === this.totalPages;
  }
  goToProductDetail(productId: number): void {
    this.router.navigate(['/detail-product', productId]);
  }


  addToCart(productId: number): void {
    if (this.userId) {
      const cartDetail: CartDetail = { product_id: productId };
      this.cartService.addtoCart(this.userId, cartDetail).subscribe({
        next: (response) => {
          this.toastr.success('Thêm sản phẩm vào giỏ hàng thành công', 'Thành công', { timeOut: 2000 });
          //Lấy quantity để lưu vào biến cartItem
          const totalItems = response?.data?.cartDetails.length;
          if (totalItems !== undefined) {
            this.cartService.updateCartItemCount(totalItems);
          }
        },
        error: () => {
          this.toastr.error('Lỗi khi thêm sản phẩm vào giỏ hàng', 'Lỗi', { timeOut: 2000 });
        }
      });
    } else {
      Swal.fire({
        title: 'Bạn chưa đăng nhập!',
        text: 'Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng.',
        icon: 'warning',
        showCancelButton: true,
        cancelButtonText: 'Hủy',
        confirmButtonText: 'Đăng nhập ngay',
        reverseButtons: true // Đảo vị trí của nút Confirm và Cancel
      }).then((result) => {
        if (result.isConfirmed) {
          this.router.navigate(['/signin']);
        }
      });
    }
  }


}