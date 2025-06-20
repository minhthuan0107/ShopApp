import { ReplyComment } from './../models/reply-comment';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { HttpClient } from '@angular/common/http';
import { CommentDto } from '../dtos/comment.dto';
import { Comment } from '../models/comment';
import { ApiResponse } from '../responses/api.response';
import { Observable } from 'rxjs';
import { ReplyCommentDto } from '../dtos/reply-comment.dto';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private apiComments = `${environment.apiBaseUrl}/comments`;
  constructor(private http: HttpClient) { }
  submitComment(commentDto: CommentDto): Observable<ApiResponse<Comment>> {
    return this.http.post<ApiResponse<Comment>>(`${this.apiComments}/create-coment`, commentDto)
  }

  submitReplyComment(replyCommentDto: ReplyCommentDto): Observable<ApiResponse<ReplyComment>> {
    return this.http.post<ApiResponse<ReplyComment>>(`${this.apiComments}/create-reply-coment`, replyCommentDto)
  }
  
  getCommentsByProductId(productId: number): Observable<ApiResponse<Comment[]>> {
    return this.http.get<ApiResponse<Comment[]>>(`${this.apiComments}/${productId}`)
  }
}
