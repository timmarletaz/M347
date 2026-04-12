import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {firstValueFrom} from 'rxjs';
import {AuthService} from './auth.service';

@Injectable({
  providedIn: 'root' // optional, registriert den Guard global
})
export class AuthGuard implements CanActivate {

  constructor(private router: Router, private http: HttpClient, private authService: AuthService) {
  }

  async canActivate(): Promise<boolean> {
    let user = await this.authService.loadUser();

    if (user !== null) {
      return true;
    } else {
      await this.router.navigate(['/login']);
      return false;
    }
  }


}
