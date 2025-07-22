import { Component, ViewChild } from '@angular/core';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { Chart, registerables } from 'chart.js';
import { Product } from '../../models/product.model';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { ProductService } from '../../services/product.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-best-selling',
  standalone: true,
  imports: [CommonModule, FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule],
  templateUrl: './best-selling.component.html',
  styleUrl: './best-selling.component.scss'
})
export class BestSellingComponent {
  listData!: MatTableDataSource<Product>;
  products!: Product[];
  productsLength!: number;
  columns: string[] = ['url_image', 'id', 'name', 'sold', 'category'];

  labels: string[] = [];
  data: number[] = [];
  myChartBar !: Chart;

  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private productService: ProductService) { }

  ngOnInit(): void {
    this.getProduct();
    Chart.register(...registerables);
  }

  getProduct() {
    this.productService.getTop10BestSellingProducts().subscribe(res => {
      this.products = res.data;
      this.listData = new MatTableDataSource(this.products);
      this.listData.sort = this.sort;
      this.listData.paginator = this.paginator;
      for (let i = 0; i < this.products.length && i < 10; i++) {
        this.labels.push(this.products[i].name);
        this.data.push(this.products[i].sold);
      }
      this.loadChartBar();
    }, error => {
      console.log(error);
    })
  }

  loadChartBar() {
  this.myChartBar = new Chart('chart', {
    type: 'bar',
    data: {
      labels: this.labels,
      datasets: [{
        label: 'Số lượng bán',
        data: this.data,
        backgroundColor: [
          'rgba(255, 99, 132, 0.6)',
          'rgba(54, 162, 235, 0.6)',
          'rgba(255, 206, 86, 0.6)',
          'rgba(75, 192, 192, 0.6)',
          'rgba(153, 102, 255, 0.6)',
          'rgba(255, 159, 64, 0.6)',
          'rgba(201, 203, 207, 0.6)',
          'rgba(0, 162, 71, 0.6)',
          'rgba(82, 0, 36, 0.6)',
          'rgba(82, 164, 36, 0.6)',
          'rgba(255, 158, 146, 0.6)',
          'rgba(123, 39, 56, 0.6)'
        ],
        borderColor: [
          'rgba(255, 99, 132, 1)',
          'rgba(54, 162, 235, 1)',
          'rgba(255, 206, 86, 1)',
          'rgba(75, 192, 192, 1)',
          'rgba(153, 102, 255, 1)',
          'rgba(255, 159, 64, 1)',
          'rgba(201, 203, 207, 1)',
          'rgba(0, 162, 71, 1)',
          'rgba(82, 0, 36, 1)',
          'rgba(82, 164, 36, 1)',
          'rgba(255, 158, 146, 1)',
          'rgba(123, 39, 56, 1)'
        ],
        borderWidth: 1
      }]
    },
    options: {
      indexAxis: 'y', // 👉 Biểu đồ nằm ngang
      responsive: true,
      plugins: {
        legend: {
          display: false
        },
        title: {
          display: true,
          text: 'Top sản phẩm bán chạy nhất'
        }
      },
      scales: {
        x: {
          beginAtZero: true
        }
      }
    }
  });
}
}
