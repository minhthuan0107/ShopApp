import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NgxPaginationModule } from 'ngx-pagination';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, NgxPaginationModule],
  templateUrl: './order-detail.component.html',
  styleUrl: './order-detail.component.scss'
})
export class OrderDetailComponent {
  @Input() order: any;
  @Output() onClose = new EventEmitter<void>();
  page: number = 1;
  itemsPerPage: number = 3;

  // Khi click "Đóng", emit sự kiện
  closeDetail() {
    this.onClose.emit();
  }
}
