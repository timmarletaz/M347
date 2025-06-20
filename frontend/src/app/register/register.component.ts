import { Component } from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../auth.service';

@Component({
  selector: 'app-register',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    FormsModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  password: string = "";
  email: string = "";
  vorname: string = "";
  nachname: string = "";
  private loading: boolean = false;

  constructor(private authService: AuthService, private router: Router) {
  }


  login() {
    if (this.email.trim() !== "" && this.password.trim() !== "" && this.vorname.trim() !== "" && this.nachname.trim() !== ""){
      this.loading = true;
      this.authService.register(this.email, this.password, this.vorname, this.nachname);
      this.authService.getUser().subscribe(user => {
        if(user !== null) {
          this.router.navigate(['/dashboard']);
        }
        this.loading = false;
      })
    }
  }

  isLoginValid() {
    return !(this.email.trim() !== "" && this.password.trim() !== "" && this.vorname.trim() !== "" && this.nachname.trim() !== "");
  }
}
