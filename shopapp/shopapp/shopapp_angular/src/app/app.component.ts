import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { UserService } from './services/user.service';
import { TokenService } from './services/token.service';

@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.component.html',
  imports: [RouterModule, HeaderComponent, FooterComponent],
})
export class AppComponent  {
  constructor(private userService: UserService, private tokenService: TokenService) {}
  ngOnInit() {
    this.loadUserFromToken();

}
private loadUserFromToken(): void {
  const token = this.tokenService.getAccessToken();
  if (!token) return;
  this.userService.fetchUserInfo().subscribe({
    next: user => this.userService.setCurrentUser(user),
    error: error => console.error('Lỗi khi load user:', error)
  });
}
}



