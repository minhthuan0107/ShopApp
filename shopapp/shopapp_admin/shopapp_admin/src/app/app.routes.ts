import { Routes } from '@angular/router';
import { SigninComponent } from './components/signin/signin.component';
import { AuthLayoutComponent } from './components/layouts/auth-layout/auth-layout.component';
import { MainLayoutComponent } from './components/layouts/main-layout/main-layout.component';
import { HomeComponent } from './components/home/home.component';
import { authGuard } from './guards/auth.guard';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { CustomerComponent } from './components/customer/customer.component';
import { OrderComponent } from './components/order/order.component';
import { ProductComponent } from './components/product/product.component';
import { CategoryComponent } from './components/category/category.component';
import { BrandComponent } from './components/brand/brand.component';

export const routes: Routes = [
  // 👉 Redirect từ path rỗng sang signin
  {
    path: '',
    redirectTo: 'signin',
    pathMatch: 'full'
  },

  // 👉 Auth layout cho các trang chưa đăng nhập (VD: login)
  {
    path: '',
    component: AuthLayoutComponent,
    children: [
      { path: 'signin', component: SigninComponent }
    ]
  },

  // 👉 Main layout cho các trang đã đăng nhập
  {
    path: 'admin',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: 'home', component: HomeComponent },
      { path: 'customer', component: CustomerComponent },
      { path: 'order', component: OrderComponent },
      { path: 'product', component: ProductComponent },
      { path: 'category', component: CategoryComponent },
      { path: 'brand', component: BrandComponent },
      // Có thể thêm các route khác như: profile, dashboard, ...
    ]
  },
];
