import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {AuthService} from './auth.service';
import {firstValueFrom} from 'rxjs';

@Injectable({
  providedIn: 'root' // optional, registriert den Guard global
})
export class LoginGuard implements CanActivate {

  constructor(private router: Router, private authService: AuthService) {}

  async canActivate(): Promise<boolean> {
    let user = await firstValueFrom(this.authService.getUser());
    if(user !== null) {
      this.router.navigate(['/dashboard']);
      return false;
    } else {
      return true;
    }
  }
}
