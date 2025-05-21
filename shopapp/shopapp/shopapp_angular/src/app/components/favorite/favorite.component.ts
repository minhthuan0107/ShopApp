import { FavoriteResponse } from './../../responses/favorite.response';
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FavoriteService } from '../../services/favorite.service';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';
import { filter, Observable } from 'rxjs';

@Component({
  selector: 'app-favorite',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './favorite.component.html',
  styleUrls: ['./favorite.component.scss'],
})
export class FavoriteComponent {
      userId!: number;
      user: User | null = null;
      user$!: Observable<User | null>;
      favorites: FavoriteResponse [] = [];
     constructor(
       private route: ActivatedRoute,
       private favoriteService : FavoriteService,
       private router : Router,
       private userService : UserService 
     ) { }
   ngOnInit(): void {
       //Lấy user từ behavior subject
          this.user$ = this.userService.user$;
          this.user$.pipe(
            filter((user): user is User => !!user),
          ).subscribe(user => {
            this.userId = user.id;
            this.getFavoriteProductsByUserId();
          });
        
    }

    //Hàm lấy danh sách yêu thích của người dùng
      getFavoriteProductsByUserId() {
        if (this.userId) {
          this.favoriteService.getFavoriteProductsByUserId().subscribe({
            next: (response) => {
            this.favorites = response.data;
            },
            error: (error) => {
              console.error("Lỗi!", error.error?.message || "Lỗi khi lấy danh sách sản phẩm yêu thích");
            
            }
          });
        }
      }
  removeFromFavorites(productId: number) {
  }
  clearAllFavorites() {
  if (confirm('Bạn có chắc muốn xóa tất cả sản phẩm yêu thích?')) {
    this.favorites = [];  // Xóa tất cả trong danh sách
    // Nếu bạn lưu trên server hoặc localStorage, nhớ gọi API hoặc cập nhật lại ở đây
  }
}
}
