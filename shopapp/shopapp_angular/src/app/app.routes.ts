import { Routes } from '@angular/router';
import { ProductCategoryComponent } from './components/product-category/product-category.component';
import { HomeComponent } from './components/home/home.component';
import { DetailProductComponent } from './components/detail-product/detail-product.component';
import { SigninComponent } from './components/signin/signin.component';
import { SignupComponent } from './components/signup/signup.component';
import { CartComponent } from './components/cart/cart.component';
import { OrderComponent } from './components/order/order.component';
import { OrderConfirmComponent } from './components/order/order-confirm/order-confirm.component';
import { PaymentCallbackComponent } from './components/payment-callback/payment-callback.component';
import { ProfileComponent } from './components/profile/profile.component';
import { orderConfirmationGuard } from './guards/order-confirmation.guard';
import { OrderHistoryComponent } from './components/order/order-history/order-history.component';
import { SearchComponent } from './components/search/search.component';
import { BuyNowOrderComponent } from './components/order/buy-now-order/buy-now-order.component';
import { authGuard} from './guards/auth.guard';
import { FavoriteComponent } from './components/favorite/favorite.component';
import { AuthCallbackComponent } from './components/auth-callback/auth-callback.component';

export const routes: Routes = [ 
{ path: 'home', component: HomeComponent },
{ path: 'signin', component: SigninComponent },
{ 
    path: 'cart', 
    component: CartComponent,
    canActivate: [authGuard],
    data: { authMessage: 'Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng' }
},
{ path: 'signup', component: SignupComponent },  
{ path: 'detail-product/:productId', component: DetailProductComponent },
{
    path: 'buy-now-order/:productId',
    component: BuyNowOrderComponent,
    canActivate: [authGuard],
    data: { authMessage: 'Vui lòng đăng nhập để mua sản phẩm!' }
  },
{ path: '', component: HomeComponent }, 
{ path: 'category/:id', component: ProductCategoryComponent },
{ path: 'search/:keyword', component: SearchComponent },
{ path: 'check-out', component: OrderComponent },
{ path: 'order-confirmation/:orderId', component: OrderConfirmComponent, canActivate: [orderConfirmationGuard]},
{ path: 'order-history', component: OrderHistoryComponent},
{ path: 'payments/payment-callback', component: PaymentCallbackComponent },
{ path: 'profile', component: ProfileComponent},
{ path: 'favorite', component: FavoriteComponent},
{ path: 'auth/google/callback', component: AuthCallbackComponent},
];