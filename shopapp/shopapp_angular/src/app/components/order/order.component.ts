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
import { CouponService } from '../../services/coupon.service';
import { LocationService } from '../../services/location.service';


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
  couponCode: string = '';
  errorMessage: string | null = null;
  tempCouponCode: string = '';
  couponValue: number = 0;
  totalPrice: number = 0;
  // Đầu class
  provinces: any[] = [];
  districts: any[] = [];
  wards: any[] = [];

  selectedProvinceCode = '';
  selectedDistrictCode = '';
  selectedWardCode = '';

  selectedProvinceName = '';
  selectedDistrictName = '';
  selectedWardName = '';
  addressDetail = '';

  constructor(private cartDetailService: CartDetailService,
    private userService: UserService,
    private fb: FormBuilder,
    private toastr: ToastrService,
    private orderService: OrderService,
    private router: Router,
    private cartService: CartService,
    private paymentService: PaymentService,
    private couponService: CouponService,
    private locationService: LocationService) {
    this.orderForm = this.fb.group({
      fullname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.minLength(9)]],
      provinceCode: ['', Validators.required],
      districtCode: ['', Validators.required],
      wardCode: ['', Validators.required],
      addressDetail: ['', Validators.required],
      note: ['']
    });
  }
  ngOnInit(): void {
    // Lắng nghe user$ từ BehaviorSubject và gọi API khi có userId
    this.userService.user$.subscribe(user => {
      if (user && user.id) {
        this.userId = user.id;
        this.getCartDetailsByUserId();
        this.orderForm.patchValue({
          fullname: user.full_name,
          phone: user.phone_number,
        });
      }
    });
    this.locationService.getProvinces().subscribe({
      next: (data) => {
        this.provinces = data || [];
      }
    });
  }
  // Lấy thông tin giỏ hàng theo userId
  getCartDetailsByUserId() {
    this.cartDetailService.getCartDetailsByUserId().subscribe({
      next: (response: any) => {
        this.cartDetails = response.data.map((item: any) => ({
          ...item, // Sao chép toàn bộ thuộc tính của item
          total_price: item.unit_price * item.quantity // Thêm hoặc cập nhật total_price
        }));
        this.updateTotalPrice();
      },
      error: (error: any) => {
        console.error("Lỗi!", error.error?.message || "Lấy danh sách chi tiết giỏ hàng thất bại", "error");
        this.cartDetails = []; // Tránh lỗi khi dữ liệu không có
      }
    });
  }

  updateTotalPrice() {
    this.totalPrice = this.cartDetails.reduce((sum, item) => sum + item.total_price, 0);
  }


  placeOrder() {
    if (this.orderForm.invalid) {
      this.orderForm.markAllAsTouched();
      this.toastr.error('Vui lòng nhập đầy đủ thông tin.', 'Lỗi', { timeOut: 1500 });
      return;
    }
    // Ép kiểu giá trị mã tỉnh/thành, quận/huyện, phường/xã từ form thành số (vì có thể đang là string)
    const provinceCode = +this.orderForm.value.provinceCode;
    const districtCode = +this.orderForm.value.districtCode;
    const wardCode = +this.orderForm.value.wardCode;
    // Tìm tên tỉnh/thành theo mã (code), nếu không tìm thấy thì trả về chuỗi rỗng
    const selectedProvinceName = this.provinces.find(p => +p.code === provinceCode)?.name || '';
    // Tìm tên quận/huyện theo mã, nếu không tìm thấy thì trả về chuỗi rỗng
    const selectedDistrictName = this.districts.find(d => +d.code === districtCode)?.name || '';
    // Tìm tên phường/xã theo mã, nếu không tìm thấy thì trả về chuỗi rỗng
    const selectedWardName = this.wards.find(w => +w.code === wardCode)?.name || '';
    // Gộp địa chỉ chi tiết và tên phường, quận, tỉnh thành thành một chuỗi đầy đủ
    const fullAddress = [
      this.orderForm.value.addressDetail?.trim(),  // Lấy địa chỉ chi tiết từ form và loại bỏ khoảng trắng đầu/cuối
      selectedWardName,                            // Tên phường/xã
      selectedDistrictName,                        // Tên quận/huyện
      selectedProvinceName                         // Tên tỉnh/thành phố
    ].filter(Boolean)                               // Loại bỏ các phần tử rỗng (null, undefined, '')
      .join(', ');                                    // Nối các phần tử còn lại bằng dấu phẩy và khoảng trắng
    const orderDto: OrderDto = {
      full_name: this.orderForm.value.fullname,
      email: this.orderForm.value.email,
      phone_number: this.orderForm.value.phone,
      address: fullAddress,
      note: this.orderForm.value.note || '',
      coupon_code: this.couponCode,
      order_details: this.cartDetails.map(item => ({
        product_id: item.product_id,
        quantity: item.quantity,
      })),
      payment: {
        payment_method: this.paymentMethod,

      },
      is_buy_now: false //Biến theo dõi mua sản phẩm từ giỏ hàng hay là mua thẳng
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
              transactionId: vnp_TxnRef
            }).subscribe({
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
  onInputCoupon(event: any) {
    this.tempCouponCode = event.target.value;
    // Lúc này couponCode chưa thay đổi
  }

  applyCoupon() {
    this.couponCode = this.tempCouponCode; // mới cập nhật biến chính
    this.checkCouponCode();
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
  onProvinceChange(): void {
    const provinceCode = this.orderForm.get('provinceCode')?.value;
    const selectedProvince = this.provinces.find(p => p.code === provinceCode);
    this.selectedProvinceName = selectedProvince?.name || '';
    // Reset các lựa chọn sau khi chọn tỉnh
    this.districts = [];
    this.wards = [];
    this.orderForm.patchValue({
      districtCode: '',
      wardCode: ''
    });

    if (provinceCode) {
      this.locationService.getDistrictsByProvince(provinceCode)
        .subscribe(data => this.districts = data.districts || []);
    }
  }

  onDistrictChange(): void {
    const districtCode = this.orderForm.get('districtCode')?.value;
    const selectedDistrict = this.districts.find(d => d.code === districtCode);
    this.selectedDistrictName = selectedDistrict?.name || '';

    this.wards = [];
    this.orderForm.patchValue({
      wardCode: ''
    });

    if (districtCode) {
      this.locationService.getWardsByDistrict(districtCode)
        .subscribe(data => this.wards = data.wards || []);
    }
  }

  onWardChange(): void {
    const wardCode = this.orderForm.get('wardCode')?.value;
    const selectedWard = this.wards.find(w => w.code === wardCode);
    this.selectedWardName = selectedWard?.name || '';
  }
}
