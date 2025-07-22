import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { Component, TemplateRef, ViewChild } from '@angular/core';
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
import { EditProductComponent } from '../edit-product/edit-product.component';
import { AddProductComponent } from '../add-product/add-product.component';
import Swal from 'sweetalert2';

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
    FormsModule,
    EditProductComponent,
    AddProductComponent],
  templateUrl: './product.component.html',
  styleUrl: './product.component.scss'
})
export class ProductComponent {
  displayedColumns: string[] = ['id', 'name', 'url_image', 'price', 'category', 'brand', 'description', 'quantity', 'sold', 'actions'];
  dataSource: MatTableDataSource<Product> = new MatTableDataSource<Product>([]);
  keyword?: string = '';
  selectedProduct!: Product;
  totalItems: number = 0;
  pageSize: number = 5;
  currentPage: number = 0;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('editProductModal') editProductModal!: TemplateRef<any>;
  constructor(
    private productService: ProductService,
    private modalService: NgbModal
  ) { }

  ngOnInit(): void {
    this.getAllProducts(0, 5, this.keyword);
  }
  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
  }
  getAllProducts(page: number, size: number, keyword: string = '') {
    this.productService.getAllProducts(page, size, keyword).subscribe({
      next: (res) => {
        this.dataSource.data = res.data.productResponses;
        this.paginator.length = res.data.totalItems; // Tổng số mục để phân trang đúng
      },
      error: (err) => {
        console.error('❌ Lỗi khi lấy danh sách sản phẩm:', err);
      }
    });
  }

  applyFilter() {
    const pageSize = this.paginator?.pageSize || 5;
    const keyword = (this.keyword || '').trim().toLowerCase();
    this.getAllProducts(0, pageSize, keyword);
  }


  onPageChange(event: PageEvent) {
    const page = event.pageIndex;      // Trang hiện tại (bắt đầu từ 0)
    const size = event.pageSize;       // Số lượng mỗi trang
    this.getAllProducts(page, size, this.keyword); // Gọi lại API để lấy dữ liệu trang mới
  }

  editProduct(product: Product) {
    this.selectedProduct = product;
    this.modalService.open(this.editProductModal, { size: 'lg', centered: true });
  }
  onProductUpdated(): void {
    this.modalService.dismissAll(); // Đóng modal sau khi cập nhật
    this.getAllProducts(0, this.paginator.pageSize || 5, this.keyword); // Reload lại danh sách sản phẩm
  }
  onProductAdded() {
    // Reload lại danh sách khách hàng
    this.getAllProducts(0, this.paginator.pageSize || 5, this.keyword);
  }
  confirmDelete(product: Product): void {
    Swal.fire({
      title: 'Xác nhận xóa',
      text: `Bạn có chắc chắn muốn xóa sản phẩm "${product.name}" không?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Xóa',
      cancelButtonText: 'Hủy'
    }).then((result) => {
      if (result.isConfirmed) {
        this.productService.deleteProductById(product.id).subscribe({
          next: () => {
            Swal.fire({
              title: 'Đã xóa!',
              text: `Sản phẩm "${product.name}" đã được xóa thành công.`,
              icon: 'success',
              timer: 2000,
              showConfirmButton: false
            });
            this.getAllProducts(0, this.paginator.pageSize || 5, this.keyword); // Load lại danh sách
          },
          error: (err) => {
            console.error('❌ Lỗi:', err?.error?.message || 'Lỗi không xác định');
          }
        });
      }
    });
  }
}
