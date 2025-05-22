import { ToastrModule, ToastrService } from 'ngx-toastr';
import { FavoriteResponse } from './../../responses/favorite.response';
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FavoriteService } from '../../services/favorite.service';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';
import { filter, Observable } from 'rxjs';
import Swal from 'sweetalert2';

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
  favorites: FavoriteResponse[] = [];
  favoritesLength: number = 0;
  constructor(
    private route: ActivatedRoute,
    private favoriteService: FavoriteService,
    private router: Router,
    private userService: UserService,
    private toastr: ToastrService

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
     if (!this.userId) return;
      this.favoriteService.getFavoriteProductsByUserId().subscribe({
        next: (response) => {
          this.favorites = response.data;
          this.favoritesLength = this.favorites.length;
          //Cập nhật lại số lượng favorites
          this.favoriteService.updateFavoriteItemCount(this.favoritesLength);
        },
        error: (error) => {
          console.error("Lỗi!", error.error?.message || "Lỗi khi lấy danh sách sản phẩm yêu thích");
        }
      });
  }
  deleteFavoriteProduct(productId: number) {
    if (!this.userId) return;
     Swal.fire({
           title: "Bạn có chắc chắn muốn xóa?",
           text: "Sản phẩm này sẽ bị xóa khỏi danh sách yêu thích!",
           icon: "warning",
           showCancelButton: true,
           confirmButtonColor: "#3085d6",
           cancelButtonColor: "#d33",
           confirmButtonText: "Xác nhận",
           cancelButtonText: "Hủy"
         }).then((result) => {
           if (result.isConfirmed) {
             this.favoriteService.deleteFavoriteProduct(productId).subscribe({
               next: () => {
                 Swal.fire({
                   title: "Đã xóa!",
                   text: "Sản phẩm đã được xóa danh sách yêu thích.",
                   icon: "success",
                   timer: 1500,
                   showConfirmButton: false
                 });
                 setTimeout(() => {
                   //gọi lại hàm lấy danh sách chi tiết
                   this.getFavoriteProductsByUserId();
                 }, 1500);
               },
               error: (error) => {
                 Swal.fire("Lỗi!", error.error?.message || "Xóa thất bại", "error");
               }
             });
           }
         });
       }
  deleteAllFavoriteProducts() {
  if (!this.userId) return;

  Swal.fire({
    title: "Bạn có chắc chắn muốn xóa tất cả?",
    text: "Toàn bộ sản phẩm trong danh sách yêu thích sẽ bị xóa!",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Xác nhận",
    cancelButtonText: "Hủy"
  }).then((result) => {
    if (result.isConfirmed) {
      this.favoriteService.deleteAllFavoriteProducts().subscribe({
        next: () => {
          Swal.fire({
            title: "Đã xóa!",
            text: "Tất cả sản phẩm đã được xóa khỏi danh sách yêu thích.",
            icon: "success",
            timer: 1500,
            showConfirmButton: false
          });
          setTimeout(() => {
            this.getFavoriteProductsByUserId();
          }, 1500);
        },
        error: (error) => {
          Swal.fire("Lỗi!", error.error?.message || "Xóa thất bại", "error");
        }
      });
    }
  });
}
}
