import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';

@Injectable({
  providedIn: 'root' // optional, registriert den Guard global
})
export class AuthGuard implements CanActivate {

  constructor(private router: Router) {}

  canActivate(): boolean {
    const token = localStorage.getItem("token");
    const expires = localStorage.getItem("exp");

    if (token && expires) {
      const exp = new Date(expires);
      const now = new Date();

      if (exp > now) {
        return true;
      } else {
        this.router.navigate(['/login']);
        localStorage.removeItem("token");
        localStorage.removeItem("exp");
      }
    }

    this.router.navigate(['/login']);
    return false;
  }
}
