import { CommonModule } from '@angular/common';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Chart } from 'chart.js/auto';
import { ToastrService } from 'ngx-toastr';
import { StatisticsService } from '../../services/statistics.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule,FormsModule,RouterModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  totalRevenueCurrentYear? : number;
  totalRevenueCurrentMonth? : number;
  countPendingOrders? : number;
  availableOrderYears: number[] = [];
  revenueByYearMap: { [year: number]: number } = {};
  monthlyRevenue: number[] = [];
  chart!: Chart;
  currentYear: number = new Date().getFullYear();
  totalCustomers = 560;

    constructor(private statisticsService: StatisticsService,
    private router: Router,
    private toastr: ToastrService,
  ) { }
  @ViewChild('chartCanvas', { static: true }) chartCanvas!: ElementRef<HTMLCanvasElement>;

  ngOnInit(): void {
    this.getTotalRevenueCurrentYear();
    this.getTotalRevenueCurrentMonth();
    this.countPendingOrdersWithValidPayment();
    this.getAvailableOrderYears();
    this.renderChart();
  }

 renderChart(): void {
  const ctx = this.chartCanvas.nativeElement;
  if (this.chart) {
    this.chart.destroy(); // Clear chart cũ nếu có
  }
  this.chart = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: [
        'Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4',
        'Tháng 5', 'Tháng 6', 'Tháng 7', 'Tháng 8',
        'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12'
      ],
      datasets: [{
        label: 'Doanh thu (VND)',
        data: this.monthlyRevenue, // ✅ Dữ liệu thật
        backgroundColor: 'rgba(54, 162, 235, 0.6)',
        borderColor: 'rgba(54, 162, 235, 1)',
        borderWidth: 1
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        y: {
          beginAtZero: true
        }
      }
    }
  });
}
  //Hàm lấy tổng doanh thu theo năm hiện tại
  getTotalRevenueCurrentYear(){
    this.statisticsService.getTotalRevenueCurrentYear().subscribe({
      next: (response: any) =>{
        this.totalRevenueCurrentYear = response.data;
      }
    });
  }
   //Hàm lấy tổng doanh thu theotháng hiện tại
  getTotalRevenueCurrentMonth(){
    this.statisticsService.getTotalRevenueCurrentMonth().subscribe({
      next: (response: any) =>{
        this.totalRevenueCurrentMonth = response.data;
      }
    });
  }
  //Hàm đếm số đơn hàng chờ xử lí
  countPendingOrdersWithValidPayment(){
    this.statisticsService.countPendingOrdersWithValidPayment().subscribe({
      next: (response: any) =>{
        this.countPendingOrders = response.data;
      }
    });
  }
  //Lấy danh sách năm có đơn hàng và render biểu đồ năm hiện tại
  getAvailableOrderYears(): void {
  this.statisticsService.getAvailableOrderYears().subscribe({
    next: (res) => {
      this.availableOrderYears = res.data;
      // ✅ Gọi luôn API hiển thị biểu đồ cho năm hiện tại
      if (this.availableOrderYears.includes(this.currentYear)) {
        this.getRevenueByYear(this.currentYear);
      } else if (this.availableOrderYears.length > 0) {
        // fallback nếu currentYear chưa có đơn hàng
        this.currentYear = this.availableOrderYears[0];
        this.getRevenueByYear(this.currentYear);
      }
    }
  });
}
//Lấy doanh thu của 12 tháng để render biểu đồ
  getRevenueByYear(year: number): void {
  this.statisticsService.getMonthlyRevenueByYear(year).subscribe({
    next: (response) => {
      const data = response.data;
      // Lưu tổng doanh thu để hiển thị bên ngoài
      this.revenueByYearMap[year] = data.totalRevenue;
      // Lưu doanh thu theo từng tháng để render biểu đồ
      this.monthlyRevenue = data.monthlyRevenue.map((item: any) => item.revenue || 0);

      // Render lại biểu đồ
      this.renderChart();
    }
  });
}
  
}
