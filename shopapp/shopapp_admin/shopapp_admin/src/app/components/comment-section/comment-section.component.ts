import { Component, Input, ViewChild } from '@angular/core';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { CommentResponse } from '../../responses/comment/comment.response';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CommentService } from '../../services/comment.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-comment-section',
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
  templateUrl: './comment-section.component.html',
  styleUrl: './comment-section.component.scss'
})
export class CommentSectionComponent {
  comments: CommentResponse[] = [];
  totalItems: number = 0;
  keyword?: string = '';
  flatData: any[] = [];
  dataSource: MatTableDataSource<CommentResponse> = new MatTableDataSource<CommentResponse>();
  displayedColumns: string[] = ['comment_id', 'user_name', 'content', 'createAt', 'actions'];
  expandedComments = new Set<number>();
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  constructor(private commentService: CommentService) { }

  ngOnInit(): void {
    this.loadComments(0, 5, this.keyword);
  }
  applyFilter() {
    const pageSize = this.paginator?.pageSize || 5;
    const keyword = (this.keyword || '').trim().toLowerCase();
    this.loadComments(0, pageSize, keyword);
  }
  onPageChange(event: PageEvent) {
    const page = event.pageIndex;      // Trang hiện tại (bắt đầu từ 0)
    const size = event.pageSize;       // Số lượng mỗi trang
    this.loadComments(page, size, this.keyword); // Gọi lại API để lấy dữ liệu trang mới
  }
  loadComments(page: number, size: number, keyword: string = '') {
    this.commentService.getAllComments(page, size, keyword).subscribe({
      next: (response) => {
        this.comments = response.data.commentResponses; //Lưu lại danh sách comment cha (có chứa replies bên trong)
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
    this.comments.forEach(comment => {
      this.flatData.push(comment); // push comment cha
      //nếu comment đang được mở rộng (user bấm "Xem phản hồi")
      if (this.expandedComments.has(comment.comment_id) && comment.replies) {
        comment.replies.forEach(reply => {
          this.flatData.push({
            isReply: true,
            comment_id: reply.reply_comment_id, // nếu có
            user_name: reply.user_name,
            content: reply.content,
            createAt: reply.createAt,
          });
        });
      }
    });

    this.dataSource.data = this.flatData;
  }
  //Hàm này dùng để đổi trạng thái MỞ/ẨN phản hồi:
  toggleReplies(row: CommentResponse): void {
    if (this.expandedComments.has(row.comment_id)) {
      this.expandedComments.delete(row.comment_id);
    } else {
      this.expandedComments.add(row.comment_id);
    }

    this.buildFlatData(); // 👈 rebuild lại bảng mỗi lần toggle
  }
  //isExpanded(row) dùng để kiểm tra xem comment đó đang mở hay không
  isExpanded(row: CommentResponse): boolean {
    return this.expandedComments.has(row.comment_id);
  }

  //Nếu row.isReply === true → Angular sẽ hiển thị dòng đó bằng hàng <tr> phản hồi (ngay dưới comment cha).
  isReplyRow = (index: number, row: any): boolean => {
    return row.isReply === true;
  };

  //Hàm xóa comment dựa theo Id
  deleteComment(commentId: number): void {
    Swal.fire({
      title: `Bạn có chắc muốn xóa bình luận với ID: ${commentId}?`,
      text: 'Hành động này không thể hoàn tác!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Xóa',
      cancelButtonText: 'Hủy',
    }).then((result) => {
      if (result.isConfirmed) {
        this.commentService.deleteCommentById(commentId).subscribe({
          next: () => {
            Swal.fire('Đã xóa!', `Bình luận có ID ${commentId} đã được xóa.`, 'success');
            this.loadComments(0, 5, this.keyword); // Gọi lại để load danh sách bình luận mới
          },
          error: (err) => {
            console.error('Lỗi', err.error.message || 'Xóa comment thất bại');
          }
        });
      }
    });
  }

}