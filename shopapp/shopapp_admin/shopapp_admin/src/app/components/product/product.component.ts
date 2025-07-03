import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { Component, ViewChild } from '@angular/core';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { Product } from '../../models/product.model';
import { ProductService } from '../../services/product.service';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-product',
  standalone: true,
  imports: [CommonModule, MatTableModule,
      MatPaginatorModule,
      MatSortModule,
      MatFormFieldModule,
      MatInputModule,
      MatIconModule,
      MatButtonModule,
      MatTooltipModule,
      FormsModule],
  templateUrl: './product.component.html',
  styleUrl: './product.component.scss'
})
export class ProductComponent {
  displayedColumns: string[] = ['id','name','url_image', 'price', 'category', 'brand', 'description', 'quantity', 'sold', 'actions'];
  dataSource: MatTableDataSource<Product> = new MatTableDataSource<Product>([]);
  keyword?: string = '';
  selectedProduct: Product | null = null;
  totalItems: number = 0;
  pageSize: number = 4;
  currentPage: number = 0;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private productService: ProductService,
    private modalService: NgbModal
  ) {}

  ngOnInit(): void {
    this.getAllProducts(0, 4, this.keyword);
  }

  getAllProducts(page: number, size: number, keyword: string = '') {
    this.productService.getAllProducts(page, size, keyword).subscribe({
      next: (res) => {
        this.dataSource.data = res.productResponses;
        this.paginator.length = res.totalItems; // Tổng số mục để phân trang đúng
      },
      error: (err) => {
        console.error(err);
      }
    });
  }

    applyFilter() {
    const pageSize = this.paginator?.pageSize || 4;
    const keyword = (this.keyword || '').trim().toLowerCase();
    this.getAllProducts(0, pageSize, keyword);
  }


   onPageChange(event: PageEvent) {
    const page = event.pageIndex;      // Trang hiện tại (bắt đầu từ 0)
    const size = event.pageSize;       // Số lượng mỗi trang
    this.getAllProducts(page, size, this.keyword); // Gọi lại API để lấy dữ liệu trang mới
  }

  editProduct(product: Product): void {
    this.selectedProduct = product;
    const modalRef = this.modalService.open('editModal', { size: 'lg', centered: true });
  }

 
}
