import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { EditProfileComponent } from './edit-profile/edit-profile.component';
import { UserService } from '../../services/user.service';
import { distinctUntilChanged, filter, Observable, Subject, takeUntil } from 'rxjs';
import { User } from '../../models/user.model';
import { ChangePasswordComponent } from './change-password/change-password.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, RouterModule, EditProfileComponent, ChangePasswordComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent {
  user: User | null = null;
  private destroy$ = new Subject<void>();
  isEditing = false;
  isChangingPassword: boolean = false;

  constructor(private userService: UserService) { }

ngOnInit(): void {
  this.userService.user$
    .pipe(
      filter((user): user is User => user !== null), // ✅ lọc null an toàn cho TS
      distinctUntilChanged((prev, curr) => prev.id === curr.id),
      takeUntil(this.destroy$)
    )
    .subscribe(user => {
      this.user = user;
    });
}
  handleSave(updatedUser: any) {
    this.user = updatedUser;
    this.isEditing = false;
  }

  handlePasswordChanged() {
    this.isChangingPassword = false;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}