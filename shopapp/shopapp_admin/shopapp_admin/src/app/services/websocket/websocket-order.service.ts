import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import { NotificationResponse } from '../../responses/notification/notification.response';
import SockJS from 'sockjs-client';
import { BehaviorSubject, filter, Observable } from 'rxjs';
import { AuthService } from '../auth.service';

@Injectable({
  providedIn: 'root'
})
export class WebsocketOrderService {
  private stompClient: Client;
  private connected: boolean = false;
  private notificationSubject = new BehaviorSubject<NotificationResponse | null>(null);
  constructor(private authService: AuthService) {
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
      const user = this.authService.getCurrentUser(); // hoặc dùng take(1) nếu vẫn dùng user$
      if (user) {
        const userId = user.id;
        this.stompClient.subscribe(`/topic/order/notifications/${userId}`, (message: IMessage) => {
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
