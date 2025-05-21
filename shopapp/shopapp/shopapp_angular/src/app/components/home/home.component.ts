import { CommonModule } from '@angular/common';
import { Product } from './../../models/product.model';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ProductService } from '../../services/product.service';
import { NgxPaginationModule } from 'ngx-pagination';
import { Router, RouterModule } from '@angular/router';
import { SlickCarouselModule } from 'ngx-slick-carousel';
import { UserService } from '../../services/user.service';
import { CartService } from '../../services/cart.service';
import { ToastrService } from 'ngx-toastr';
import Swal from 'sweetalert2';
import { filter } from 'rxjs';
import { CartDetailDto } from '../../dtos/cartdetail.dto';
import { FavoriteService } from '../../services/favorite.service';
import { FavoriteResponse } from '../../responses/favorite.response';

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
  favoriteProductIds: Set<number> = new Set();
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
    private toastr: ToastrService,
    private favoriteService: FavoriteService
  ) { }

  ngOnInit(): void {
    this.getProducts(this.currentPage - 1, this.itemsPerPage);
    this.userService.user$.pipe(filter(user => !!user)).subscribe(user => {
      if (user) {
        this.userId = user.id;
         this.getFavoriteProductsByUserId();
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
      const cartDetailDto: CartDetailDto = { product_id: productId };
      this.cartService.addToCart(cartDetailDto).subscribe({
        next: (response) => {
          this.toastr.success('Thêm sản phẩm vào giỏ hàng thành công', 'Thành công', { timeOut: 1500 });
          //Lấy quantity để lưu vào biến cartItem
          const totalItems = response?.data?.cartDetails.length;
          if (totalItems !== undefined) {
            this.cartService.updateCartItemCount(totalItems);
          }
        },
        error: () => {
          this.toastr.error('Lỗi khi thêm sản phẩm vào giỏ hàng', 'Lỗi', { timeOut: 1500 });
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
  //Hàm check user đăng nhập và mua ngay
  onBuyNow(productId: number) {
    if (this.userId) {
      this.router.navigate(['/buy-now-order', productId]);
    } else {
      Swal.fire({
        title: 'Bạn cần đăng nhập!',
        text: 'Vui lòng đăng nhập để mua sản phẩm.',
        icon: 'warning',
        showCancelButton: true,
        cancelButtonText: 'Hủy',
        confirmButtonText: 'Đăng nhập ngay',
        reverseButtons: true
      }).then((result) => {
        if (result.isConfirmed) {
          this.router.navigate(['/signin']);
        }
      });
    }
  }
  //Hàm thêm sản phẩm vào danh sách yêu thích 
  addToFavorite(productId: number) {
    if (this.userId) {
      this.favoriteService.addToFavorite(productId).subscribe({
        next: (res) => {
          if (res.status === 'CREATED') {
            this.toastr.success('Sản phẩm đã được thêm vào danh mục yêu thích', 'Thành công', { timeOut: 1500 });
            this.favoriteProductIds.add(productId);
          } else if (res.status === 'OK') {
            this.toastr.info('Sản phẩm đã bị xóa khỏi danh mục yêu thích', 'Thành công', { timeOut: 1500 });
            this.favoriteProductIds.delete(productId);
          }
        },
        error: (error) => {
          console.error("Lỗi!", error.error?.message || "Thêm sản phẩm vào danh mục yêu thích thất bại!");
        }
      });
    } else {
      Swal.fire({
        title: 'Bạn cần đăng nhập!',
        text: 'Vui lòng đăng nhập để thêm sản phẩm vào danh mục yêu thích.',
        icon: 'warning',
        showCancelButton: true,
        cancelButtonText: 'Hủy',
        confirmButtonText: 'Đăng nhập ngay',
        reverseButtons: true
      }).then((result) => {
        if (result.isConfirmed) {
          this.router.navigate(['/signin']);
        }
      });
    }
  }
  //Hàm lấy danh sách yêu thích của người dùng
  getFavoriteProductsByUserId() {
    if (this.userId) {
      this.favoriteService.getFavoriteProductsByUserId().subscribe({
        next: (response) => {
          this.favoriteProductIds = new Set(response.data.map((favorite: FavoriteResponse) => favorite.product_id));
          console.log('Array', this.favoriteProductIds)
        },
        error: (error) => {
          console.error("Lỗi!", error.error?.message || "Lỗi khi lấy danh sách sản phẩm yêu thích");
          this.favoriteProductIds = new Set();
        }
      });
    }
  }
}