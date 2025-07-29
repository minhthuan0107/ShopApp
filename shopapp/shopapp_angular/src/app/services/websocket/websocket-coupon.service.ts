import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import { BehaviorSubject, filter, Observable } from 'rxjs';
import { NotificationMessage } from '../../models/notification-message';
import SockJS from 'sockjs-client';
import { UserService } from '../user.service';
import { NotificationResponse } from '../../responses/notification.response';

@Injectable({
  providedIn: 'root'
})
export class WebsocketCouponService {
  private stompClient: Client;
  private connected: boolean = false;
  private notificationSubject = new BehaviorSubject<NotificationResponse | null>(null);
  constructor(private userService: UserService) {
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
      console.log('Kết nối WebSocket coupon thành công');
      this.connected = true;
      // Lắng nghe các thông báo coupon gửi về
      const user = this.userService.getCurrentUser(); // hoặc dùng take(1) nếu vẫn dùng user$
      if (user) {
        const userId = user.id;
        this.stompClient.subscribe(`/topic/notifications/${userId}`, (message: IMessage) => {
          const notify: NotificationResponse = JSON.parse(message.body);
          this.notificationSubject.next(notify);
        });
      }

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
  getNotifications(): Observable<NotificationResponse> {
    return this.notificationSubject.asObservable().pipe(
      filter((notification): notification is NotificationResponse => notification !== null)
    );
  }

}
