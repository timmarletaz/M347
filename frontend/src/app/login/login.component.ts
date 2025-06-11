import { Component } from '@angular/core';
import {AuthService} from '../auth.service';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  imports: [FormsModule, RouterLink],
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  message: string = '';

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  isLoading: boolean = false;

  login() {
    this.isLoading = true;
    this.authService.login(this.username, this.password).subscribe((token) => {
      this.isLoading = false;
      if (this.isOwnToken(token)) {
        this.authService.setToken(token);
        this.router.navigate(['/home']);
      } else {
        this.message = 'Invalid or not a correct token received';
        this.clearMessageAfterDelay();
      }
    });
  }

  clearMessageAfterDelay() {
    setTimeout(() => {
      this.message = '';
    }, 3000);
  }

  isOwnToken(token: string): boolean {
    // TODO: Überprüfung, ob das Token korrekt von uns erstellt worden ist.

    return !!token;
  }
}
