import { Component, HostListener, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { UserService } from './services/user.service';
import { TokenService } from './services/token.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.component.html',
  imports: [RouterModule, HeaderComponent, FooterComponent, CommonModule],
  styles: [`
    .scroll-to-top {
  position: fixed;
  bottom: 40px;
  right: 40px;
  z-index: 1000;
  background-color: #007bff; /* màu xanh sáng, nổi bật trên nền trắng */
  border: none;
  color: white;
  padding: 10px 15px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 20px;
  box-shadow: 0 4px 8px rgba(0, 123, 255, 0.5); /* bóng xanh nhẹ */
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
}
.scroll-to-top:hover {
  background-color: #0056b3; /* xanh đậm hơn khi hover */
  box-shadow: 0 6px 12px rgba(0, 86, 179, 0.7);
}
  `]
})
export class AppComponent {
  constructor(private userService: UserService, private tokenService: TokenService) { }
  showScrollButton = false;

  @HostListener('window:scroll', [])
  onWindowScroll() {
    const y = window.pageYOffset || document.documentElement.scrollTop || 0;
    this.showScrollButton = y > 500;
  }

  scrollToTop() {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
}



