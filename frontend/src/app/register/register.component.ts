import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import {AuthService} from '../auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  imports: [FormsModule, RouterLink],
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent {
  protected username: string = '';
  protected password: string = '';
  protected message: string = '';
  protected email: string = '';

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  isLoading: boolean = false;

  register() {
    this.isLoading = true;
    this.authService.register(this.username, this.password, this.email)
      .subscribe((response) => {
        this.isLoading = false;
        if (response === 'Benutzer erfolgreich registriert') {
          this.message = 'Registration successful. Please login.';
          this.router.navigate(['/login']);
        } else {
          this.message =
            'Registration failed: ' + (response || 'Please try again.');
          this.clearMessageAfterDelay();
        }
      });
  }
  clearMessageAfterDelay() {
    setTimeout(() => {
      this.message = '';
    }, 3000);
  }
}
