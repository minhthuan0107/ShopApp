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

export const routes: Routes = [ // ✅ Export `routes`
{ path: 'home', component: HomeComponent },
{ path: 'signin', component: SigninComponent },
{ path: 'cart/:userId', component: CartComponent },
{ path: 'signup', component: SignupComponent },  
{ path: 'detail-product/:productId', component: DetailProductComponent },
{ path: 'buy-now-order/:productId', component: BuyNowOrderComponent },
{ path: '', component: HomeComponent }, 
{ path: 'category/:id', component: ProductCategoryComponent },
{ path: 'search/:keyword', component: SearchComponent },
{ path: 'check-out', component: OrderComponent },
{ path: 'order-confirmation/:orderId', component: OrderConfirmComponent, canActivate: [orderConfirmationGuard]},
{ path: 'order-history', component: OrderHistoryComponent},
{ path: 'payments/payment-callback', component: PaymentCallbackComponent },
{ path: 'profile', component: ProfileComponent},
];