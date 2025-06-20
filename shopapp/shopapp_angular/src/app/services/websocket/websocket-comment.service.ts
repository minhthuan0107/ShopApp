import { Client } from '@stomp/stompjs';
import { BehaviorSubject, filter, Observable } from 'rxjs';
import SockJS from 'sockjs-client';
import { CommentRequestDto } from '../../dtos/websocket/comment-request.dto';
import { Comment } from '../../models/comment';
import { Injectable } from '@angular/core';
import { ReplyCommentRequestDto } from '../../dtos/websocket/reply-comment-request.dto';
import { ReplyComment } from '../../models/reply-comment';
import { Rate } from '../../models/rate.model';

@Injectable({
  providedIn: 'root'
})
export class WebSocketCommentService {
  private stompClient: Client;
  private commentSubject = new BehaviorSubject<CommentRequestDto | null>(null);
  private replySubject = new BehaviorSubject<ReplyComment | null>(null);
  private ratingSubject = new BehaviorSubject<Rate | null>(null);
  private connected: boolean = false;

  constructor() {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8081/ws'),
      reconnectDelay: 5000,
      onStompError: (frame) => console.error("Lỗi WebSocket:", frame),
      onDisconnect: () => {
        console.warn("Mất kết nối WebSocket!");
        this.connected = false;
      }
    });

    this.stompClient.onConnect = () => {
      console.log("Kết nối WebSocket thành công!");
      this.connected = true;

      this.stompClient.subscribe('/topic/comments', (message) => {
        const response: CommentRequestDto = JSON.parse(message.body);
        this.commentSubject.next(response);
      });

      this.stompClient.subscribe('/topic/replies', (message) => {
        const responseReply: ReplyComment = JSON.parse(message.body);
        this.replySubject.next(responseReply);
      });
      this.stompClient.subscribe('/topic/rates', (message) => {
        const ratingResponse: Rate = JSON.parse(message.body);
        this.ratingSubject.next(ratingResponse);
      });
    };

    this.stompClient.onWebSocketClose = () => {
      console.warn("WebSocket đóng!");
      this.connected = false;
    };
  }

  connect() {
    if (!this.connected) {
      this.stompClient.activate();
    }
  }

  disconnect() {
    if (this.connected) {
      console.log("Ngắt kết nối WebSocket...");
      this.stompClient.deactivate();
      this.connected = false;
    }
  }

  getNewComments(): Observable<CommentRequestDto> {
    return this.commentSubject.asObservable().pipe(
      filter((comment): comment is CommentRequestDto => comment !== null)
    );
  }

  getNewReply(): Observable<ReplyCommentRequestDto> {
    return this.replySubject.asObservable().pipe(
      filter((replycomment): replycomment is ReplyCommentRequestDto => replycomment !== null)
    );
  }
  getNewRating(): Observable<Rate> {
    return this.ratingSubject.asObservable().pipe(
      filter((rate): rate is Rate => rate !== null)
    );
  }

  sendComment(comment: Comment) {
    if (this.connected) {
      this.stompClient.publish({ destination: '/app/comment', body: JSON.stringify(comment) });
    } else {
      console.warn("Không thể gửi bình luận, WebSocket chưa kết nối.");
    }
  }

  sendReply(replycomment: ReplyComment) {
    if (this.connected) {
      this.stompClient.publish({ destination: '/app/reply-comment', body: JSON.stringify(replycomment) });
    } else {
      console.warn("Không thể gửi trả lời, WebSocket chưa kết nối.");
    }
  }
  sendRating(rate: Rate) {
    if (this.connected) {
      this.stompClient.publish({ destination: '/app/rating', body: JSON.stringify(rate) });
    } else {
      console.warn(" Không thể gửi đánh giá, WebSocket chưa kết nối.");
    }
  }

  

}
