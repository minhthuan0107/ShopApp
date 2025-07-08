import { CommonModule } from '@angular/common';
import { Component, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BrandService } from '../../services/brand.service';
import { Brand } from '../../models/brand.model';
import Swal from 'sweetalert2';
import { AddBrandComponent } from '../add-brand/add-brand.component';


@Component({
  selector: 'app-brand',
  standalone: true,
  imports: [CommonModule, FormsModule,
      MatTableModule,
      MatPaginatorModule,
      MatSortModule,
      MatFormFieldModule,
      MatInputModule,
      MatIconModule,
      MatButtonModule,
      MatTooltipModule,
      AddBrandComponent
      ],
  templateUrl: './brand.component.html',
  styleUrl: './brand.component.scss'
})
export class BrandComponent {
    totalItems: number = 0;
    keyword: string = '';
    displayedColumns: string[] = ['id', 'name', 'actions'];
    dataSource = new MatTableDataSource<any>();
  
    // 👇 Dùng ViewChild để lấy đối tượng MatPaginator từ template
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;
    constructor(
      private brandService: BrandService,
    ) { }
    ngOnInit() {
      this.getAllBrands();
    }
    ngAfterViewInit() {
      this.dataSource.sort = this.sort;
    }
    //Hàm lấy danh sách danh mục
    getAllBrands() {
      this.brandService.getAllBrands().subscribe({
        next: (res) => {
          this.dataSource.data = res.data;
          this.dataSource.paginator = this.paginator;
          //Định nghĩa cách lọc
          this.dataSource.filterPredicate = (data: Brand, filter: string) => {
            const keyword = filter;
            return data.name.toLowerCase().includes(keyword) || data.id.toString().includes(keyword);
          };
        },
        error: (err) => {
          console.error('❌ Lỗi:', err?.error?.message || 'Lỗi khi tải danh mục');
        }
      });
    }
    //Hàm load lại danh sách danh mục
    onBrandAdded() {
      // Reload lại danh sách danh mục sản phẩm
      this.getAllBrands();
    }
    applyFilter() {
      this.dataSource.filter = this.keyword.trim().toLowerCase();
    }
    //Hàm xóa mềm danh mục
    confirmDelete(brand: Brand): void {
      Swal.fire({
        title: 'Xác nhận xóa',
        text: `Bạn có chắc chắn muốn xóa danh mục "${brand.name}" không?`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        confirmButtonText: 'Xóa',
        cancelButtonText: 'Hủy'
      }).then((result) => {
        if (result.isConfirmed) {
          this.brandService.deleteBrandById(brand.id).subscribe({
            next: () => {
              Swal.fire({
                title: 'Đã xóa!',
                text: `Thương hiệu "${brand.name}" đã được xóa thành công.`,
                icon: 'success',
                timer: 2000,
                showConfirmButton: false
              });
              this.getAllBrands(); // Load lại danh sách
            },
            error: (err) => {
              console.error('❌ Lỗi:', err?.error?.message || 'Lỗi không xác định');
            }
          });
        }
      });
    }

}
