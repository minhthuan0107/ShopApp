import { ApiResponse } from './../responses/api.response';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CommentResponse } from '../responses/comment/comment.response';
import { environment } from '../environments/environment';
import { CommentListAdminResponse } from '../responses/comment/comment-list-admin.response';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private apiGetAllComments = `${environment.apiBaseAdminUrl}/comments/get-all`;
  private apiDeleteComment = `${environment.apiBaseAdminUrl}/comments/delete`;
  constructor(private http: HttpClient) { }
  //Api lấy danh sách comment cha và các replies của comment cha
  getAllComments(page: number, size: number, keyword: string = ''): Observable<ApiResponse<CommentListAdminResponse>> {
    return this.http.get<ApiResponse<CommentListAdminResponse>>(this.apiGetAllComments, {
      params: {
        page: page.toString(),
        size: size.toString(),
        keyword: keyword
      }
    });
  }

  //Api xóa comment dựa theo commentId
  deleteCommentById(commentId: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.apiDeleteComment}/${commentId}`);
  }
}
