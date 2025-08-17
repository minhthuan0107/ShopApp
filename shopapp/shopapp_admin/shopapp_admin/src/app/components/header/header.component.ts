import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { TokenAdminService } from '../../services/token-admin.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  constructor(private router: Router,
    private authService: AuthService,
    private tokenAdminSevice: TokenAdminService
  ) { }
  @Output() toggleSidebar = new EventEmitter<void>();

  onToggleSidebar() {
    this.toggleSidebar.emit();
  }
  //Hàm đăng xuất
  logout(): void {
    this.tokenAdminSevice.logout();
    this.authService.clearUser();
    this.router.navigate(['/signin']);
  }
}
