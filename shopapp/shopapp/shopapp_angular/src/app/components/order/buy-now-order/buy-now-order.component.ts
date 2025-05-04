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
  constructor(private userService: UserService,
    private fb: FormBuilder,
    private productService: ProductService,
    private cartService: CartService,
    private toastr: ToastrService,
    private orderService: OrderService,
    private router: Router,
    private route: ActivatedRoute,
    private paymentService: PaymentService) {
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
        this.getProductByIdWithAuth(this.productId)
      }
      // Lắng nghe user$ từ BehaviorSubject và gọi API khi có userId
      this.userService.user$.subscribe(user => {
        if (user && user.id) {
          this.userId = user.id;
          this.buyNowOrderForm.patchValue({
            fullname: user.fullname,
            phone: user.phone_number,
            address: user.address
          });
        }
      });
    });
  }
  // Lấy thông tin sản phẩm theo Id
  getProductByIdWithAuth(productId: number) {
    this.productService.getProductByIdWithAuth(productId).subscribe({
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
        total_price: this.product.price * 1,
        order_details: [{
          product_id: this.product.id,
          quantity: 1,
          unit_price: this.product.price,
          total_price: this.product.price * 1
        }],
        payment: {
          amount: this.product.price * 1,
          payment_method: this.paymentMethod,
          transaction_id: '',
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
  
    // Xử lý đặt hàng trực tiếp
    private processOrder(orderDto: OrderDto) {
      this.orderService.placeOrder(this.userId, orderDto).subscribe({
        next: (response) => {
          const orderId = response.data.order_id;
          Swal.fire({
            icon: "success",
            title: "Đặt hàng thành công!",
            text: "Đơn hàng của bạn đã được đặt thành công!",
            confirmButtonText: "Xem chi tiết",
          }).then(() => this.router.navigate(['/order-confirmation', orderId]));
        },
        error: (error: any) => {
          console.error("Lỗi!", error.error?.message || "Đặt hàng thất bại!");
        }
      });
    }
  
    // Xử lý thanh toán VNPAY
    private handleVnpayPayment(orderDto: OrderDto) {
      this.paymentService.createPaymentUrl({ amount: orderDto.total_price, language: 'vn' }).subscribe({
        next: (paymentResponse) => {
          const paymentUrl = paymentResponse.data;
          if (!paymentUrl) {
            console.error('Không thể lấy link thanh toán.');
            return;
          }
          // Parse mã giao dịch VNPAY từ URL
          const urlParams = new URLSearchParams(new URL(paymentUrl).search);
          const vnp_TxnRef = urlParams.get("vnp_TxnRef");
          if (!vnp_TxnRef) {
            console.error('Không thể lấy mã giao dịch VNPAY.');
            return;
          }
          // Cập nhật vnp_TxnRef rồi lưu đơn hàng
          orderDto.payment.transaction_id = vnp_TxnRef;
          this.orderService.placeOrder(this.userId, orderDto).subscribe({
            next: () => window.location.href = paymentUrl
            ,
            error: (error: any) => {
              console.error("Lỗi!", error.error?.message || "Không thể đặt hàng với VNPAY");
            }
          });
        },
        error: (error: any) => {
          console.error("Lỗi!", error.error?.message || "Không thể tạo URL thanh toán");
        }
      });
    }
}
