import { WebSocketCommentService } from './../../services/websocket/websocket-comment.service';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Product } from '../../models/product.model';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { ProductImageService } from '../../services/product-image.service';
import { ProductImage } from '../../models/product-image.model';
import { FormsModule } from '@angular/forms';
import { Comment } from '../../models/comment';
import { UserService } from '../../services/user.service';
import { filter, Subscription } from 'rxjs';
import { CommentDto } from '../../dtos/comment.dto';
import { CommentService } from '../../services/comment.service';
import { CommentRequestDto } from '../../dtos/websocket/comment-request.dto';
import { ReplyCommentDto } from '../../dtos/reply-comment.dto';
import { ReplyCommentRequestDto } from '../../dtos/websocket/reply-comment-request.dto';
import { RatingDto } from '../../dtos/rating.dto';
import { RateService } from '../../services/rate.service';
import { Rate } from '../../models/rate.model';
import { CartDetailDto } from '../../dtos/cartdetail.dto';
import { CartService } from '../../services/cart.service';
import Swal from 'sweetalert2';
import { ToastrService } from 'ngx-toastr';



@Component({
  selector: 'app-detail-product',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './detail-product.component.html',
  styleUrl: './detail-product.component.scss'
})
export class DetailProductComponent implements OnInit {
  private newCommentsSubscription?: Subscription; // Subscription để theo dõi bình luận mới
  private newRepliesSubscription?: Subscription;
  private newRatingSubscription?: Subscription;
  userId!: number;
  isUserLoggedIn: boolean = false; // Kiểm tra người dùng có đăng nhập hay không
  product!: Product;
  productImages: ProductImage[] = [];
  productId!: number;
  comments: Comment[] = [];
  rates: Rate[] = [];
  isRated: boolean = false;
  newComment: { content: string } = { content: '' }; // Biến để lưu nội dung bình luận mới
  replyContent: { [key: number]: string } = {};  // Lưu nội dung phản hồi cho mỗi bình luận
  replyingToCommentId: number | null = null;
  visibleReplyCounts: { [key: number]: number } = {}; // Số lượng phản hồi hiển thị cho mỗi bình luận
  showAllComments: boolean = false; // Biến để kiểm tra xem có hiển thị tất cả bình luận hay không
  selectedRating: number = 0; // Đánh giá hiện tại được chọn
  hoveredRating: number | null = null; // Đánh giá hiện tại khi di chuột qua
  isRatingVisible: boolean = true; // Kiểm tra xem có hiển thị phần đánh giá hay không
  constructor(private productService: ProductService,
    private productImageService: ProductImageService,
    private route: ActivatedRoute,
    private userService: UserService,
    private commentService: CommentService,
    private webSocketCommentService: WebSocketCommentService,
    private rateService: RateService,
    private cartService: CartService,
    private toastr: ToastrService,
    private router: Router) { }

  ngOnInit(): void {
    this.webSocketCommentService.connect();
    this.route.paramMap.subscribe(params => {
      const productId = params.get('productId');
      if (productId) {
        this.productId = +productId; //gán productId đc lấy từ param
        this.GetProductById(this.productId);
        this.GetProductImagesByProductId(this.productId); // Gọi hàm để lấy dữ liệu sản phẩm
      }
      this.getAllCommentsByProductId();
    });

    this.userService.user$.pipe(filter(user => !!user)).subscribe(user => {
      if (user) {
        this.userId = user.id;
        this.isUserLoggedIn = true;
      } else {
        this.isUserLoggedIn = false;
      }
    });

    // Subscribe vào bình luận mới từ WebSocket
    this.newCommentsSubscription = this.webSocketCommentService.getNewComments().subscribe((commentDto: CommentRequestDto) => {
      const existingComment = this.comments.find(c => c.comment_id === commentDto.comment_id);
      if (!existingComment) { // Kiểm tra xem bình luận đã tồn tại chưa
        const comment: Comment = {
          comment_id: commentDto.comment_id,
          content: commentDto.content,
          user_name: commentDto.user_name,
          createAt: commentDto.createAt,
          replies: commentDto.replies,
          product_id: commentDto.product_id,
          user_id: commentDto.user_id,
          showReplyForm: false,
        };
        this.comments.unshift(comment); // Thêm bình luận mới vào đầu danh sách
      }
    });

    this.newRepliesSubscription = this.webSocketCommentService.getNewReply().subscribe((replyCommentDto: ReplyCommentRequestDto) => {
      const parentComment = this.comments.find(c => c.comment_id === replyCommentDto.parent_id);
      if (parentComment) {
        parentComment.replies.unshift(replyCommentDto);
        this.visibleReplyCounts[parentComment.comment_id] = parentComment.replies.length;
      }
    });

    this.newRatingSubscription = this.webSocketCommentService.getNewRating().subscribe(rate => {
      const commentToUpdate = this.comments.find(c => c.comment_id === rate.comment_id);
      if (commentToUpdate) {
        commentToUpdate.rating = rate.rating;
        // Nếu user hiện tại là người đánh giá thì chặn ô đánh giá
        this.isRated = rate.user_id === this.userId;
      }
    });
  }
  ngOnDestroy() {
    // Hủy các subscription để ngăn chặn rò rỉ bộ nhớ
    this.newCommentsSubscription?.unsubscribe();
    this.newRepliesSubscription?.unsubscribe();
    this.newRatingSubscription?.unsubscribe();
    this.webSocketCommentService.disconnect();
  }
  GetProductById(productId: number) {
    this.productService.getProductById(productId).subscribe({
      next: (response: any) => {
        this.product = response.data;
      },
      error: (error: any) => {
        console.error('Lỗi không tìm thấy sản phẩm', error);
      }
    });

  }
  GetProductImagesByProductId(productId: number) {
    this.productImageService.getProducImagesByProductId(productId).subscribe({
      next: (response: any) => {
        this.productImages = response.data.map((item: ProductImage) => item.url_image);
      },
      error: (error: any) => {
        console.error('Lỗi không tìm thấy hình ảnh sản phẩm', error);
      }
    });
  }
  //set nút di chuyển ảnh trong carousel
  setActiveSlide(index: number): void {
    const carousel = document.querySelector('#carouselExample') as any;
    if (carousel) {
      const carouselItemCount = carousel.querySelectorAll('.carousel-item').length;
      if (index >= 0 && index < carouselItemCount) {
        carousel.querySelector('.carousel-item.active').classList.remove('active');
        carousel.querySelectorAll('.carousel-item')[index].classList.add('active');
      }
    }
  }
  submitComment() {
    const commentDto: CommentDto = {
      product_id: this.productId!,
      content: this.newComment.content
    };
    this.commentService.submitComment(commentDto).subscribe({
      next: (response) => {
        if (response && response.data) {
          this.newComment = { content: '' };// Reset form sau khi gửi
          this.webSocketCommentService.sendComment(response.data); // Gửi bình luận mới lên WebSocket
          //Gửi đánh giá
          if (!this.isRated) {
            const ratingDto: RatingDto = {
              comment_id: response.data.comment_id,
              rating: this.selectedRating
            };
            this.submitRating(ratingDto);
            this.isRatingVisible = false;
          } else {
            console.log("Người dùng đã đánh giá, không gửi rating.");
          }
        }
      },
      error: (err) => {
        console.error("Lỗi khi gửi bình luận:", err);
      }
    });
  }
  // Gửi phản hồi
  submitReply(commentId: number) {
    const replyCommentDto: ReplyCommentDto = {
      content: this.replyContent[commentId],
      parent_id: commentId
    };
    this.commentService.submitReplyComment(replyCommentDto).subscribe({
      next: (response) => {
        if (response && response.data) {
          this.webSocketCommentService.sendReply(response.data);
          this.replyContent = '';
        }
      },
      error: (err) => {
        console.error("Lỗi khi gửi phản hồi bình luận:", err);
      }
    });
  }
  submitRating(ratingDto: RatingDto) {
    this.rateService.submitRate(ratingDto).subscribe({
      next: (response) => {
        if (response && response.data) {
          this.webSocketCommentService.sendRating(response.data);
        }
      },
      error: (err) => {
        console.error("Lỗi khi gửi đánh giá:", err);
      }
    });
  }
  getAllCommentsByProductId() {
    this.commentService.getCommentsByProductId(this.productId).subscribe({
      next: (response) => {
        if (response && response.data) {
          this.comments = response.data;
        }
        if (this.comments && this.comments.length > 0) {
          this.comments.forEach(comment => {
            this.visibleReplyCounts[comment.comment_id] = 2; // Mặc định hiển thị 2 phản hồi
          });
        }
        this.getRatesByProductId();
      },
      error: (err) => {
        console.error('Lỗi khi danh sách lấy bình luận:', err);
      }
    });
  }
  getRatesByProductId() {
    this.rateService.getRatesByProductId(this.productId).subscribe({
      next: (response) => {
        //Thêm rating vào comments để hiển thị
        const ratesMap = new Map(response.data.map(rate => [rate.comment_id, rate.rating]));
        this.comments = this.comments.map(comment => ({
          ...comment,
          //Kiểm tra comment_id có tồn tại trong ratesMap k , nếu k thì gán undefined
          rating: ratesMap.has(comment.comment_id) ? ratesMap.get(comment.comment_id) : undefined
        }));
        // Kiểm tra biến isRated sau khi cập nhật rating
        this.isRated = this.comments.some(comment =>
          comment.user_id === this.userId &&
          comment.rating !== undefined
        );
      },
      error: (err) => {
        console.error("Lỗi khi lấy danh sách đánh giá:", err);
      }
    })
  }
  toggleReplyForm(commentId: number) {
    // Nếu form đang mở cho comment này -> đóng lại, nếu không -> mở form này và đóng form khác
    this.replyingToCommentId = this.replyingToCommentId === commentId ? null : commentId;
  }
  showMoreReplies(commentId: number) {
    // Tăng số lượng phản hồi hiển thị lên
    this.visibleReplyCounts[commentId] += 3;
  }
  showMoreComments() {
    // Hiển thị tất cả bình luận
    this.showAllComments = true;
  }
  setRating(star: number) {
    this.selectedRating = star; // Gán số sao khi người dùng chọn
  }

  hoverRating(star: number) {
    this.hoveredRating = star; // Hiển thị số sao khi di chuột qua
  }

  resetHover() {
    this.hoveredRating = null; // Reset khi chuột rời đi
  }
  getArray(n: number): number[] {
    return Array(n).fill(0);
  }
  addToCart(productId: number): void {
    if (this.userId) {
      const cartDetailDto: CartDetailDto = { product_id: productId };
      this.cartService.addToCart(cartDetailDto).subscribe({
        next: (response) => {
          this.toastr.success('Thêm sản phẩm vào giỏ hàng thành công', 'Thành công', { timeOut: 1500 });
          //Lấy quantity để lưu vào biến cartItem
          const totalItems = response?.data?.cartDetails.length;
          if (totalItems !== undefined) {
            this.cartService.updateCartItemCount(totalItems);
          }
        },
        error: (error) => {
          this.toastr.error(error.error?.message || 'Lỗi khi thêm sản phẩm vào giỏ hàng', 'Lỗi', { timeOut: 1500 });
        }
      });
    } else {
      Swal.fire({
        title: 'Bạn chưa đăng nhập!',
        text: 'Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng.',
        icon: 'warning',
        showCancelButton: true,
        cancelButtonText: 'Hủy',
        confirmButtonText: 'Đăng nhập ngay',
        reverseButtons: true // Đảo vị trí của nút Confirm và Cancel
      }).then((result) => {
        if (result.isConfirmed) {
          this.router.navigate(['/signin']);
        }
      });
    }
  }
  //Hàm check user đăng nhập và mua ngay
  onBuyNow(productId: number) {
    if (this.userId) {
      this.router.navigate(['/buy-now-order', productId]);
    } else {
      Swal.fire({
        title: 'Bạn cần đăng nhập!',
        text: 'Vui lòng đăng nhập để mua sản phẩm.',
        icon: 'warning',
        showCancelButton: true,
        cancelButtonText: 'Hủy',
        confirmButtonText: 'Đăng nhập ngay',
        reverseButtons: true
      }).then((result) => {
        if (result.isConfirmed) {
          this.router.navigate(['/signin']);
        }
      });
    }
  }

}
