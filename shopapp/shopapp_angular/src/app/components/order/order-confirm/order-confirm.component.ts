import { ReactiveFormsModule } from '@angular/forms';
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { OrderService } from '../../../services/order.service';
import { OrderResponse } from '../../../responses/order.response';
import { OrderDetailResponse } from '../../../responses/order-detail.response';
import { PaymentResponse } from '../../../responses/payment.response';

@Component({
  selector: 'app-order-confirm',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './order-confirm.component.html',
  styleUrl: './order-confirm.component.scss'
})
export class OrderConfirmComponent {
  orderId!: number;
  order: OrderResponse | null = null;
  orderDetails: OrderDetailResponse[] = [];
  payment : PaymentResponse | null = null;
  constructor(private orderService: OrderService,
    private route: ActivatedRoute
  ) { }


  ngOnInit(): void {
    const orderId = this.route.snapshot.paramMap.get('orderId');
    if (orderId) {
      this.orderId = Number(orderId);
      this.getOrderById(this.orderId);
    }
  }

  getOrderById(orderId: number): void {
    this.orderService.getOrderById(orderId).subscribe({
      next: (response) => {
       this.order = response.data;
       this.orderDetails = response.data.order_details;
       this.payment = response.data.payment;
      },
      error: (error) => {
        console.error("Lỗi!", error.error?.message || "Lấy thông tin đơn hàng thất bại");
      }
    });
  }

}
