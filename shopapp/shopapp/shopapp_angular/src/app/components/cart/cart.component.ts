import { CartService } from './../../services/cart.service';

import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CartDetailService } from '../../services/cart-detail.service';
import { CartDetail } from '../../models/cart-detail';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { CartDetailsUpdate } from '../../dtos/cartdetails-update.dto';
import { filter, Observable } from 'rxjs';
import { User } from '../../models/user.model';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss'
})
export class CartComponent {
  userId!: number;
  user: User | null = null;
  user$!: Observable<User | null>;
  cartDetail: CartDetail[] = [];
  constructor(
    private route: ActivatedRoute,
    private cartDetailService: CartDetailService,
    private cartService: CartService,
    private router : Router,
    private userService : UserService 
  ) { }
  ngOnInit(): void {
     //Lấy user từ behavior subject
        this.user$ = this.userService.user$;
        this.user$.pipe(
          filter((user): user is User => !!user),
        ).subscribe(user => {
          this.userId = user.id;
          this.getCartDetailsByUserId();
        });
  }
  increaseQuantity(item: any) {
    item.quantity += 1;
    item.total_price = item.unit_price * item.quantity;
  }

  decreaseQuantity(item: any) {
    if (item.quantity > 1) {
      item.quantity -= 1;
      item.total_price = item.unit_price * item.quantity;
    }
  }
  // Hàm lấy thông tin giỏ hàng theo userId
  getCartDetailsByUserId() {
    this.cartDetailService.getCartDetailsByUserId().subscribe({
      next: (response: any) => {
        this.cartDetail = response.data.map((item: any) => ({
          ...item, // Sao chép toàn bộ thuộc tính của item
          total_price: item.unit_price * item.quantity // Thêm hoặc cập nhật total_price
        }));
        const totalItems = this.cartDetail.length;
        // Cập nhật số lượng giỏ hàng trên header
        this.cartService.updateCartItemCount(totalItems);
      },
      error: (error: any) => {
        console.error("Lỗi!", error.error?.message || "Lấy danh sách chi tiết giỏ hàng thất bại", "error");
        this.cartDetail = []; // Tránh lỗi khi dữ liệu không có
      }
    });
  }
  // Tính tổng tiền của giỏ hàng
  getTotalPrice(): number {
    return this.cartDetail.reduce((sum, item) => sum + item.total_price, 0);
  }

  deleteCartDetailById(cartDetailId: number) {
    Swal.fire({
      title: "Bạn có chắc chắn muốn xóa?",
      text: "Sản phẩm này sẽ bị xóa khỏi giỏ hàng!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Xác nhận",
      cancelButtonText: "Hủy"
    }).then((result) => {
      if (result.isConfirmed) {
        this.cartDetailService.deleteCartDetailById(cartDetailId).subscribe({
          next: () => {
            Swal.fire({
              title: "Đã xóa!",
              text: "Sản phẩm đã được xóa khỏi giỏ hàng.",
              icon: "success",
              timer: 1500,
              showConfirmButton: false
            });
            setTimeout(() => {
              //gọi lại hàm lấy danh sách chi tiết
              this.getCartDetailsByUserId();
            }, 1500);
          },
          error: (error) => {
            Swal.fire("Lỗi!", error.error?.message || "Xóa thất bại", "error");
          }
        });
      }
    });
  }
  updateCartDetail() {
    const cartDetailsUpdateDto: CartDetailsUpdate[] = this.cartDetail.map(item => ({
      cart_detail_id: item.cart_detail_id,
      new_quantity: item.quantity
    }));
    this.cartDetailService.updateCartDetails(cartDetailsUpdateDto).subscribe({
      next: () => {
        this.router.navigate(['/check-out']);
      },
      error: (error) => {
        console.error("Lỗi!", error?.error?.message || "Cập nhật chi tiết giỏ hàng thất bại","error");
      },
    });
  }

}
