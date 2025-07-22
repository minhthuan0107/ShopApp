import { CommonModule } from '@angular/common';
import { Component, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RateService } from '../../services/rate.service';
import { RateResponse } from '../../responses/rate/rate.response';

@Component({
  selector: 'app-review-section',
  standalone: true,
  imports: [CommonModule, FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule],
  templateUrl: './review-section.component.html',
  styleUrl: './review-section.component.scss'
})
export class ReviewSectionComponent {
  rates: RateResponse[] = [];
  flatData: any[] = [];
  totalItems = 0;
  keyword: string = '';
  expandedRates = new Set<number>();
  dataSource: MatTableDataSource<any> = new MatTableDataSource<any>();
  displayedColumns: string[] = ['rate_id', 'user_name', 'product_name','rating','comment_content', 'createAt','actions'];
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  constructor(private rateService: RateService) { }
   ngOnInit(): void {
    this.loadRates(0, 5, this.keyword);
  }

applyFilter() {
    const pageSize = this.paginator?.pageSize || 5;
    const keyword = (this.keyword || '').trim().toLowerCase();
    this.loadRates(0, pageSize, keyword);
  }
  onPageChange(event: PageEvent) {
    const page = event.pageIndex;      // Trang hiện tại (bắt đầu từ 0)
    const size = event.pageSize;       // Số lượng mỗi trang
    this.loadRates(page, size, this.keyword); // Gọi lại API để lấy dữ liệu trang mới
  }
  loadRates(page: number, size: number, keyword: string = '') {
    this.rateService.getAllRates(page, size, keyword).subscribe({
      next: (response) => {
        this.rates = response.data.rateResponses; //Lưu lại danh sách rate (có chứa replies bên trong)
        this.paginator.length = response.data.totalItems;
        this.buildFlatData(); // Build dữ liệu hiển thị cho table
      },
      error: (err) => {
        console.error('Lỗi', err.error.message || 'Tải danh sách comment thất bại');
      }
    });
  }
   buildFlatData() {
    this.flatData = []; // reset lại mảng hiển thị
    this.rates.forEach(rate => {
      this.flatData.push(rate); // push rate cha
      //nếu comment đang được mở rộng (user bấm "Xem phản hồi")
      if (this.expandedRates.has(rate.comment_id) && rate.replies) {
        rate.replies.forEach(reply => {
          this.flatData.push({
            isReply: true,
            commenter_name: reply.user_name,
            comment_content: reply.content,
            createAt: reply.createAt,
          });
        });
      }
    });
    this.dataSource.data = this.flatData;
  }
  //Hàm này dùng để đổi trạng thái MỞ/ẨN phản hồi:
    toggleReplies(row: RateResponse): void {
      if (this.expandedRates.has(row.comment_id)) {
        this.expandedRates.delete(row.comment_id);
      } else {
        this.expandedRates.add(row.comment_id);
      }
      this.buildFlatData(); // 👈 rebuild lại bảng mỗi lần toggle
    }
    //isExpanded(row) dùng để kiểm tra xem comment đó đang mở hay không
    isExpanded(row: RateResponse): boolean {
      return this.expandedRates.has(row.comment_id);
    }
  
  //Nếu row.isReply === true → Angular sẽ hiển thị dòng đó bằng hàng <tr> phản hồi (ngay dưới comment cha).
  isReplyRow = (index: number, row: any): boolean => {
    return row.isReply === true;
  };



}
