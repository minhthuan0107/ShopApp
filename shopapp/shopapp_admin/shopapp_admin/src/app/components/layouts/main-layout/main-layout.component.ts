import { Component } from '@angular/core';
import { HeaderComponent } from '../../header/header.component';
import { FooterComponent } from '../../footer/footer.component';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../../sidebar/sidebar.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [HeaderComponent,FooterComponent,RouterOutlet,SidebarComponent,CommonModule],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.scss'
})
export class MainLayoutComponent {
   isSidebarVisible = true;

  toggleSidebar() {
    this.isSidebarVisible = !this.isSidebarVisible;
  }

}
