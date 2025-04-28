import { Component } from '@angular/core';
import { CartDetailService } from '../../services/cart-detail.service';
import { UserService } from '../../services/user.service';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CartDetail } from '../../models/cart-detail';
import { ToastrService } from 'ngx-toastr';
import { OrderDto } from '../../dtos/order.dto';
import { OrderService } from '../../services/order.service';
import Swal from 'sweetalert2';
import { CartService } from '../../services/cart.service';
import { PaymentService } from '../../services/payment.service';


@Component({
  selector: 'app-order',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule],
  templateUrl: './order.component.html',
  styleUrl: './order.component.scss'
})
export class OrderComponent {
  orderForm: FormGroup;
  userId!: number;
  cartDetails: CartDetail[] = [];
  paymentMethod: string = 'Cod';

  constructor(private cartDetailService: CartDetailService,
    private userService: UserService,
    private fb: FormBuilder,
    private toastr: ToastrService,
    private orderService: OrderService,
    private router: Router,
    private cartService: CartService,
    private paymentService: PaymentService) {
    this.orderForm = this.fb.group({
      fullname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.minLength(9)]],
      address: ['', Validators.required],
      note: ['']
    });
  }
  ngOnInit(): void {
    // Lắng nghe user$ từ BehaviorSubject và gọi API khi có userId
    this.userService.user$.subscribe(user => {
      if (user && user.id) {
        this.userId = user.id;
        this.getCartDetailsByUserId(this.userId);
        // Cập nhật giá trị fullname vào form
        this.orderForm.patchValue({ fullname: user.fullname });
        this.orderForm.patchValue({ phone: user.phone_number });
        this.orderForm.patchValue({ address: user.address });
      }
    });
  }
  // Lấy thông tin giỏ hàng theo userId
  getCartDetailsByUserId(userId: number) {
    this.cartDetailService.getCartDetailsByUserId(userId).subscribe({
      next: (response: any) => {
        this.cartDetails = response.data.map((item: any) => ({
          ...item, // Sao chép toàn bộ thuộc tính của item
          total_price: item.unit_price * item.quantity // Thêm hoặc cập nhật total_price
        }));
      },
      error: (error: any) => {
        console.error("Lỗi!", error.error?.message || "Lấy danh sách chi tiết giỏ hàng thất bại", "error");
        this.cartDetails = []; // Tránh lỗi khi dữ liệu không có
      }
    });
  }
  // Tính tổng tiền của giỏ hàng
  getTotalPrice(): number {
    return this.cartDetails.reduce((sum, item) => sum + item.total_price, 0);
  }


  placeOrder() {
    if (this.orderForm.invalid) {
      this.orderForm.markAllAsTouched();
      this.toastr.error('Vui lòng nhập đầy đủ thông tin.', 'Lỗi', { timeOut: 1500 });
      return;
    }

    const totalPrice = this.getTotalPrice();
    const orderDto: OrderDto = {
      full_name: this.orderForm.value.fullname,
      email: this.orderForm.value.email,
      phone_number: this.orderForm.value.phone,
      address: this.orderForm.value.address,
      note: this.orderForm.value.note || '',
      total_price: totalPrice,
      order_details: this.cartDetails.map(item => ({
        product_id: item.product_id,
        quantity: item.quantity,
        unit_price: item.unit_price,
        total_price: item.total_price
      })),
      payment: {
        amount: totalPrice,
        payment_method: this.paymentMethod,
        transaction_id: '',
      }
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
        this.cartService.setCartItemCount(0);
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
