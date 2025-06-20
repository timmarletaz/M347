import { Component } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {AuthService} from '../auth.service';
import {Router, RouterLink} from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [
    FormsModule,
    RouterLink
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  email: string = "";
  password: string = "";
  loading: boolean = false;

  constructor(private authService: AuthService, private router: Router) {
  }

  isLoginValid() {
    return !(this.email.trim() !== "" && this.password.trim() !== "");
  }

  login() {
    this.loading = true;
    this.authService.loginRequest(this.email, this.password);
    this.authService.getUser().subscribe(user => {
      if(user !== null) {
        this.router.navigate(['/dashboard']);
      }
      this.loading = false;
    })
  }
}
