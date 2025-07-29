import { ApiResponse } from './../responses/api.response';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { NotificationResponse } from '../responses/notification.response';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private readonly apiGetNotifications = `${environment.apiBaseUrl}/notifications`;
  private readonly apiMarkAsRead = `${environment.apiBaseUrl}/notifications/mark-as-read`;
  private readonly apiGetUnreadNotifications = `${environment.apiBaseUrl}/notifications/unread`;
  private readonly apiDeleteNotification = `${environment.apiBaseUrl}/notifications/delete`;
  constructor(private http: HttpClient) { }
  //Api lấy danh sách thông báo theo userId
  getNotificationByUserId(): Observable<ApiResponse<NotificationResponse[]>> {
    return this.http.get<ApiResponse<NotificationResponse[]>>(`${this.apiGetNotifications}`);
  }
  //Api đánh dấu thông báo đã đọc
  markAsRead(notificationId: number): Observable<ApiResponse<NotificationResponse>> {
    return this.http.patch<ApiResponse<NotificationResponse>>(`${this.apiMarkAsRead}/${notificationId}`, {});
  }
  //Api lấy danh sách thông báo chưa đọc theo userId
  getUnreadNotifications(): Observable<ApiResponse<NotificationResponse[]>> {
    return this.http.get<ApiResponse<NotificationResponse[]>>(`${this.apiGetUnreadNotifications}`);
  }
  //Api xóa thông báo
  deleteNotificationById(notificationId: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.apiDeleteNotification}/${notificationId}`)
  }

}
