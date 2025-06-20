import { Routes } from '@angular/router';
import { SigninComponent } from './components/signin/signin.component';
import { AuthLayoutComponent } from './components/layouts/auth-layout/auth-layout.component';
import { MainLayoutComponent } from './components/layouts/main-layout/main-layout.component';
import { HomeComponent } from './components/home/home.component';
import { authGuard } from './guards/auth.guard';
import { SidebarComponent } from './components/sidebar/sidebar.component';

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
    canActivate: [authGuard],
    children: [
      { path: 'signin', component: SigninComponent }
    ]
  },

  // 👉 Main layout cho các trang đã đăng nhập
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: 'home', component: HomeComponent },
      { path: 'sidebar', component: SidebarComponent }
      // Có thể thêm các route khác như: profile, dashboard, ...
    ]
  },

  // 👉 Wildcard route: nếu route không đúng, về lại signin
  {
    path: '**',
    redirectTo: 'signin'
  }
];
