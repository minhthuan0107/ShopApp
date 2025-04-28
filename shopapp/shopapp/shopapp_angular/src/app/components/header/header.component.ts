import { Router, RouterModule } from '@angular/router';
import { CategoryService } from './../../services/category.service';
import { CommonModule } from '@angular/common';
import { Component, ElementRef, ViewChild, OnInit, Input, ChangeDetectorRef } from '@angular/core';
import { Category } from '../../models/category.model';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';
import { TokenService } from '../../services/token.service';
import { debounceTime, distinctUntilChanged, filter, Observable, Subject, switchMap, take } from 'rxjs';
import { CartService } from '../../services/cart.service';
import { ProductService } from '../../services/product.service';
import { ProductResponse } from '../../responses/product.response';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit {
  @ViewChild('categoryList') categoryList!: ElementRef;
  user: User | null = null;
  cartItemCount: number = 0;
  categories: Category[] = [];
  user$!: Observable<User | null>;
  isCategoryVisible = false;
  isDropdownVisible = false; // Kiểm soát hiển thị dropdown
  suggestedProducts: ProductResponse[] = [];
  private keywordSubject = new Subject<string>();
  searchQuery: string = '';

  constructor(private categoryService: CategoryService,
    private router: Router,
    private userService: UserService,
    private tokenService: TokenService,
    private cartService: CartService,
    private productService: ProductService
  ) { }
  ngOnInit(): void {
    this.loadCategories();
    //Lấy user từ behavior subject
    this.user$ = this.userService.user$;
    this.user$.pipe(
      filter((user): user is User => !!user),
    ).subscribe(user => {
      this.cartService.getCartItemCount(user.id).subscribe();
    });
    //Lấy số lượng cart từ behavior subject
    this.cartService.cartItemCount$.subscribe(count => {
      this.cartItemCount = count;
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
  logout(): void {
    this.tokenService.logout();
    this.userService.clearUser();
    this.cartItemCount = 0;
    this.router.navigate(['/signin']);
  }
}
