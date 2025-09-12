import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { HttpClient } from '@angular/common/http';
import { NotificationResponse } from '../responses/notification/notification.response';
import { ApiResponse } from '../responses/api.response';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private readonly apiGetNotifications = `${environment.apiBaseAdminUrl}/notifications`;
  private readonly apiMarkAsRead = `${environment.apiBaseAdminUrl}/notifications/mark-as-read`;
  private readonly apiGetUnreadNotifications = `${environment.apiBaseAdminUrl}/notifications/unread`;
  private readonly apiDeleteNotification = `${environment.apiBaseAdminUrl}/notifications/delete`;
  constructor(private http: HttpClient) { }
  //Api lấy danh sách thông báo theo AdminId
  getNotificationByAdminId(): Observable<ApiResponse<NotificationResponse[]>> {
    return this.http.get<ApiResponse<NotificationResponse[]>>(`${this.apiGetNotifications}`);
  }
  //Api đánh dấu thông báo đã đọc
  markAsRead(notificationId: number): Observable<ApiResponse<NotificationResponse>> {
    return this.http.patch<ApiResponse<NotificationResponse>>(`${this.apiMarkAsRead}/${notificationId}`, {});
  }
  //Api lấy danh sách thông báo chưa đọc theo AdminId
  getUnreadNotifications(): Observable<ApiResponse<NotificationResponse[]>> {
    return this.http.get<ApiResponse<NotificationResponse[]>>(`${this.apiGetUnreadNotifications}`);
  }
  //Api xóa thông báo
  deleteNotificationById(notificationId: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.apiDeleteNotification}/${notificationId}`)
  }

}
