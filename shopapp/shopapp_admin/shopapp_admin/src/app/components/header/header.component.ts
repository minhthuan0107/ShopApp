import { WebsocketOrderService } from './../../services/websocket/websocket-order.service';
import { NotificationService } from './../../services/notification.service';
import { AuthService } from './../../services/auth.service';
import { CommonModule } from '@angular/common';
import { Component, EventEmitter, HostListener, Output } from '@angular/core';
import { Router } from '@angular/router';
import { TokenAdminService } from '../../services/token-admin.service';
import { Observable } from 'rxjs';
import { User } from '../../models/user.model';
import { NotificationResponse } from '../../responses/notification/notification.response';
import { MomentModule } from 'ngx-moment';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule,MomentModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  user$!: Observable<User | null>;
  notifications: NotificationResponse[] = [];
  unreadNotifications: NotificationResponse[] = [];
  isNotificationDropdownOpen = false;
  selectedTab: 'all' | 'unread' = 'all';
  visibleLimit = 6;
  showAllNotifications = false;
  constructor(private router: Router,
    private authService: AuthService,
    private tokenAdminSevice: TokenAdminService,
    private notificationService : NotificationService,
    private websocketOrderService : WebsocketOrderService,
    private toastr: ToastrService
  ) { }

  ngOnInit(): void {
    this.loadNotifications();
    this.user$ = this.authService.user$;
    this.websocketOrderService.connect();
    // Lắng nghe thông báo mới
    this.websocketOrderService.getNotifications().subscribe((notification) => {
      this.notifications.unshift(notification); // thêm thông báo mới vào đầu danh sách
      this.unreadNotifications.unshift(notification);   // Cập nhật tab "Chưa đọc"
    });
  }
  @Output() toggleSidebar = new EventEmitter<void>();
  onToggleSidebar() {
    this.toggleSidebar.emit();
  }
  //Hàm mở / đóng thông bảng thông báo
  toggleNotificationDropdown() {
    this.isNotificationDropdownOpen = !this.isNotificationDropdownOpen;
    if (this.isNotificationDropdownOpen) {
      this.loadNotifications(); // Gọi API khi mở bảng
    }
  }
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const targetElement = event.target as HTMLElement;
    // Nếu click KHÔNG nằm trong vùng thông báo
    if (!targetElement.closest('.notification-wrapper')) {
      this.isNotificationDropdownOpen = false;
    }
  }
  //Hàm gọi lấy tất cả danh sách thông báo cho người dùng
  loadNotifications() {
    this.notificationService.getNotificationByAdminId().subscribe(
      (response) => {
        this.notifications = response.data;
      },
      (error) => {
        console.error('Lỗi!', error.error?.message || 'Lấy danh sách thông báo thất bại!');
      }
    );
  }

  //Hàm gọi lấy tất cả danh sách thông báo chưa đọc cho người dùng
  loadUnreadNotifications() {
    this.notificationService.getUnreadNotifications().subscribe(
      (response) => {
        this.unreadNotifications = response.data;
      },
      (error) => {
        console.error('Lỗi!', error.error?.message || 'Lấy danh sách thông báo thất bại!');
      }
    );
  }
  //Hàm đánh dấu đã đọc thông báo
  markAsRead(notification: NotificationResponse) {
    if (!notification.is_read) {
      this.notificationService.markAsRead(notification.notification_id).subscribe({
        next: () => {
          // Sau khi đánh dấu thành công, gọi lại cả 2 danh sách để đảm bảo đồng bộ
          this.loadNotifications();        // cập nhật tab Tất cả
          this.loadUnreadNotifications();     // cập nhật tab Chưa đọc
        },
        error: (error) => {
          console.error('Lỗi!', error.error?.message || 'Không thể đánh dấu đã đọc!');
        }
      });
    }
  }
  //Hàm hiển thị số lượng thông báo chưa xem
  getUnreadCount(): number {
    return this.notifications.filter(n => !n.is_read).length;
  }

  onTabClick(tab: 'all' | 'unread') {
    this.selectedTab = tab;
    if (tab === 'unread') {
      this.loadUnreadNotifications(); // chỉ gọi khi cần
    }
  }
  //Hàm hiện thi danh sách tất cả thông báo || danh sách thông báo chưa đọc
  getCurrentNotifications(): NotificationResponse[] {
    return this.selectedTab === 'all' ? this.notifications : this.unreadNotifications;
  }
  // Toggle menu cho 1 thông báo
  toggleMenu(notify: NotificationResponse, event: MouseEvent): void {
    event.stopPropagation(); // không để markAsRead bị gọi khi click "..."
    const isCurrentlyOpen = notify.showMenu === true;
    // Tắt menu của tất cả các thông báo
    this.getCurrentNotifications().forEach(n => n.showMenu = false);
    notify.showMenu = !isCurrentlyOpen;
  }
  //Hàm hiển thị limited giới hạn thông báo = 6
  getCurrentNotificationsLimited(): NotificationResponse[] {
    const all = this.getCurrentNotifications();
    return this.showAllNotifications ? all : all.slice(0, this.visibleLimit);
  }
  //Hàm xóa thông báo
  deleteNotification(notify: NotificationResponse) {
    this.notificationService.deleteNotificationById(notify.notification_id).subscribe({
      next: () => {
        this.toastr.success('Đã xóa thông báo', 'Thành công', {
          timeOut: 1000
        });
        this.loadNotifications();        
        this.loadUnreadNotifications();   
      },
      error: (error) => {
        console.error('Lỗi!', error.error?.message || 'Xóa thông báo thất bại!');
      }
    });
  }
  //Hàm đăng xuất
  logout(): void {
    this.tokenAdminSevice.logout();
    this.authService.clearUser();
    this.router.navigate(['/signin']);
  }
}
