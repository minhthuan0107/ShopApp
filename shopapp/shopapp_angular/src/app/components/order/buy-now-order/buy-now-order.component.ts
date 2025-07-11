import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { ToastrService } from 'ngx-toastr';
import { OrderService } from '../../../services/order.service';
import { PaymentService } from '../../../services/payment.service';
import { ProductService } from '../../../services/product.service';
import { Product } from '../../../models/product.model';
import { OrderDto } from '../../../dtos/order.dto';
import Swal from 'sweetalert2';
import { CartService } from '../../../services/cart.service';
import { CouponService } from '../../../services/coupon.service';

@Component({
  selector: 'app-buy-now-order',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule],
  templateUrl: './buy-now-order.component.html',
  styleUrl: './buy-now-order.component.scss'
})
export class BuyNowOrderComponent {
  buyNowOrderForm: FormGroup;
  userId!: number;
  product!: Product;
  productId!: number;
  paymentMethod: string = 'Cod';
  couponValue: number = 0;
  totalPrice: number = 0;
  couponCode: string = '';
  errorMessage: string | null = null;
  constructor(private userService: UserService,
    private fb: FormBuilder,
    private productService: ProductService,
    private cartService: CartService,
    private toastr: ToastrService,
    private orderService: OrderService,
    private router: Router,
    private route: ActivatedRoute,
    private paymentService: PaymentService,
    private couponService : CouponService) {
    this.buyNowOrderForm = this.fb.group({
      fullname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.minLength(9)]],
      address: ['', Validators.required],
      note: ['']
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const productId = params.get('productId');
      if (productId) {
        this.productId = +productId; //Ép chuỗi về số
        this.getProductById(this.productId)
      }
      // Lắng nghe user$ từ BehaviorSubject và gọi API khi có userId
      this.userService.user$.subscribe(user => {
        if (user && user.id) {
          this.userId = user.id;
          this.buyNowOrderForm.patchValue({
            fullname: user.full_name,
            phone: user.phone_number,
            address: user.address
          });
        }
      });
    });
  }
  // Lấy thông tin sản phẩm theo Id
  getProductById(productId: number) {
    this.productService.getProductById(productId).subscribe({
      next: (response: any) => {
        this.product = response.data
      },
      error: (error: any) => {
        console.error("Lỗi!", error.error?.message || "Lấy thông tin sản phẩm thất bại", "error");
      }
    });
  }
  placeOrder() {
      if (this.buyNowOrderForm.invalid) {
        this.buyNowOrderForm.markAllAsTouched();
        this.toastr.error('Vui lòng nhập đầy đủ thông tin.', 'Lỗi', { timeOut: 1500 });
        return;
      }
      const orderDto: OrderDto = {
        full_name: this.buyNowOrderForm.value.fullname,
        email: this.buyNowOrderForm.value.email,
        phone_number: this.buyNowOrderForm.value.phone,
        address: this.buyNowOrderForm.value.address,
        note: this.buyNowOrderForm.value.note || '',
        coupon_code: this.couponCode,
        order_details: [{
          product_id: this.product.id,
          quantity: 1,
        }],
        payment: {
          payment_method: this.paymentMethod,
        },
        is_buy_now: true
      };
      // Nếu là VNPAY, tạo URL thanh toán trước
      if (this.paymentMethod === 'Vnpay') {
        this.handleVnpayPayment(orderDto);
      } else {
        this.processOrder(orderDto);
      }
    }
    private processOrder(orderDto: OrderDto) {
      Swal.fire({
        title: 'Bạn có chắc chắn muốn đặt hàng này không?',
        text: 'Đơn hàng của bạn sẽ được xử lý ngay sau khi bạn xác nhận.',
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Đặt hàng',
        cancelButtonText: 'Hủy',
        reverseButtons: true // Đảo vị trí của nút Confirm và Cancel
      }).then((result) => {
        if (result.isConfirmed) {
          this.orderService.placeOrder(this.userId, orderDto).subscribe({
            next: (response) => {
              this.cartService.setCartItemCount(0);
              const orderId = response.data.order_id; 
    
              Swal.fire({
                icon: 'success',
                title: 'Đặt hàng thành công!',
                text: 'Đơn hàng của bạn đã được đặt thành công!',
                confirmButtonText: 'Xem chi tiết',
              }).then(() => {
                this.router.navigate(['/order-confirmation', orderId]);
              });
            },
            error: (error: any) => {
              console.error('Lỗi!', error.error?.message || 'Đặt hàng thất bại!');
            }
          });
        } else {
          // Nếu người dùng chọn hủy, điều hướng về lại trang cũ
          this.router.navigate([this.router.url]);
        }
      });
    }
  
   // Xử lý thanh toán VNPAY
  private handleVnpayPayment(orderDto: OrderDto) {
    // Bước 1: Đặt hàng trước, backend tính toán giá chính xác
    this.orderService.placeOrder(this.userId, orderDto).subscribe({
      next: (orderResponse) => {
        const orderId = orderResponse.data.order_id;
        const actualTotalPrice = orderResponse.data.total_price; // lấy giá tổng tiền thật từ backend
        // Bước 2: Tạo link thanh toán VNPAY với số tiền chính xác
        this.paymentService.createPaymentUrl({
          amount: actualTotalPrice,
          language: 'vn'
        }).subscribe({
          next: (paymentResponse) => {
            const paymentUrl = paymentResponse.data;
            if (!paymentUrl) {
              console.error('Không thể lấy link thanh toán.');
              return;
            }
            // Lấy mã giao dịch VNPAY trong URL (nếu cần dùng)
            const urlParams = new URLSearchParams(new URL(paymentUrl).search);
            const vnp_TxnRef = urlParams.get("vnp_TxnRef");
            if (!vnp_TxnRef) {
              console.error('Không thể lấy mã giao dịch VNPAY.');
              return;
            }
            // Gọi API cập nhật transactionId trước khi chuyển hướng
            this.paymentService.updateTransactionId({
              orderId: orderId, 
              transactionId: vnp_TxnRef}).subscribe({
              next: () => {
                debugger;
                window.location.href = paymentUrl;
              },
              error: (error: any) => {
                console.error("Lỗi!", error.error?.message || "Lỗi khi cập nhật transactionId");
              }

            });
          },
          error: (error: any) => {
            console.error("Lỗi!", error.error?.message || "Không thể tạo URL thanh toán");
          }
        });
      },
      error: (error: any) => {
        console.error("Lỗi!", error.error?.message || "Không thể đặt hàng");
      }
    });
  }
  //Hàm check mã giám giá
  checkCouponCode() {
    this.couponService.applyCoupon(this.couponCode).subscribe({
      next: (response) => {
        this.errorMessage = null;
        this.couponValue = response.data.value;
        this.toastr.success('Áp dụng mã giảm giá thành công', 'Thành công', { timeOut: 1500 });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Có lỗi xảy ra';
      }
    });
  }
}
