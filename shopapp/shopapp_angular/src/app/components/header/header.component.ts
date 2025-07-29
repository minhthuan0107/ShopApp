import { Router, RouterModule } from '@angular/router';
import { CategoryService } from './../../services/category.service';
import { CommonModule } from '@angular/common';
import { Component, ElementRef, ViewChild, OnInit, HostListener } from '@angular/core';
import { Category } from '../../models/category.model';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';
import { TokenService } from '../../services/token.service';
import { debounceTime, distinctUntilChanged, filter, Observable, Subject, switchMap, take } from 'rxjs';
import { CartService } from '../../services/cart.service';
import { ProductService } from '../../services/product.service';
import { ProductResponse } from '../../responses/product.response';
import { FavoriteService } from '../../services/favorite.service';
import { WebsocketCouponService } from '../../services/websocket/websocket-coupon.service';
import { NotificationService } from '../../services/notification.service';
import { NotificationResponse } from '../../responses/notification.response';
import moment from 'moment';
import 'moment/locale/vi';
import { MomentModule } from 'ngx-moment';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, MomentModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit {
  @ViewChild('categoryList') categoryList!: ElementRef;
  user: User | null = null;
  cartItemCount: number = 0;
  favoriteItemCount: number = 0;
  categories: Category[] = [];
  user$!: Observable<User | null>;
  isCategoryVisible = false;
  isDropdownVisible = false; // Kiểm soát hiển thị dropdown
  suggestedProducts: ProductResponse[] = [];
  private keywordSubject = new Subject<string>();
  searchQuery: string = '';
  notifications: NotificationResponse[] = [];
  unreadNotifications: NotificationResponse[] = [];
  isNotificationDropdownOpen = false;
  selectedTab: 'all' | 'unread' = 'all';
  visibleLimit = 6;
  showAllNotifications = false;

  constructor(private categoryService: CategoryService,
    private router: Router,
    private userService: UserService,
    private tokenService: TokenService,
    private cartService: CartService,
    private productService: ProductService,
    private favoriteService: FavoriteService,
    private websocketCouponService: WebsocketCouponService,
    private notificationService: NotificationService,
    private toastr: ToastrService
  ) { }
  ngOnInit(): void {
    this.loadNotifications();
    this.loadCategories();
    //Lấy user từ behavior subject
    this.user$ = this.userService.user$;
    this.cartService.getCartItemCount().subscribe();
    //Lấy số lượng cart từ behavior subject
    this.cartService.cartItemCount$.subscribe(count => {
      this.cartItemCount = count;
    });
    this.favoriteService.getFavoriteItemsCount().subscribe();
    //Lấy số lượng favorite từ behavior subject
    this.favoriteService.favoriteItemCount$.subscribe(count => {
      this.favoriteItemCount = count;
    });
    //Lấy danh sách gợi ý khi người tìm kiếm sản phẩm
    this.keywordSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(keyword => this.productService.getProductSuggestions(keyword))
    ).subscribe({
      next: (response) => {
        this.suggestedProducts = response.data;
      },

      error: () => this.suggestedProducts = []
    });
    this.websocketCouponService.connect();
    // Lắng nghe thông báo mới
    this.websocketCouponService.getNotifications().subscribe((notification) => {
      this.notifications.unshift(notification); // thêm thông báo mới vào đầu danh sách
    });
  }
  // Lấy danh mục từ API
  loadCategories(): void {
    this.categoryService.getAllCategories().subscribe({
      next: (response: any) => {
        this.categories = response.data;
      },
      error: (error) => {
        console.error('Lỗi khi lấy danh mục:', error);
      },
    });
  }
  toggleCategory(): void {
    this.isCategoryVisible = !this.isCategoryVisible; // Bật/tắt danh mục
  }
  toggleDropdown(show: boolean) {
    this.isDropdownVisible = show;
  }
  selectCategory(categoryId: number): void {
    this.isCategoryVisible = false; // Ẩn danh mục sau khi chọn
    this.router.navigate(['/category', categoryId]); // Điều hướng đến ProductCategoryComponent
  }
  //Hàm tìm kiếm
  onSearch(keyword: string): void {
    this.suggestedProducts = [];
    keyword = keyword.trim();
    if (keyword) {
      this.router.navigate(['/search', keyword]);
    }
  }
  //Hàm chuyển đến trang chi tiết
  onSelectSuggestion(productId: number) {
    this.router.navigate(['/detail-product', productId]);
    // Ẩn gợi ý sau khi chuyển trang
    this.suggestedProducts = [];
  }
  onKeywordChange(keyword: string) {
    this.keywordSubject.next(keyword); // Gửi từ khóa tìm kiếm đến subject
    if (keyword.trim() === '') {
      this.suggestedProducts = [];
    }
  }
  //Hàm đăng xuất
  logout(): void {
    this.websocketCouponService.disconnect();
    this.tokenService.logout();
    this.userService.clearUser();
    this.cartItemCount = 0;
    this.router.navigate(['/signin']);
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
    this.notificationService.getNotificationByUserId().subscribe(
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
}
