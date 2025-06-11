import {Inject, Injectable, PLATFORM_ID} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {Router} from '@angular/router';
import {isPlatformBrowser} from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private authState = new BehaviorSubject<boolean>(!!this.getToken());

  constructor(private http: HttpClient, private router: Router, @Inject(PLATFORM_ID) private platformId: Object) {  }

  login(username: string, password: string): Observable<string> {
     return this.http.post(
      'http://localhost:8080/api/auth/login',
      { username, password },
      { responseType: 'text' },
    );
  }

  logout() {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('token');
    }
    this.authState.next(false);
    this.router.navigate(['/login']);
  }

  register(username: string, password: string, email: string): Observable<string> {
    return this.http.post(
      'http://localhost:8080/api/auth/register',
      { username, password, email },
      { responseType: 'text' },
    );
  }

  getToken(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem('token');
    }
    return null;
  }

  setToken(token: string) {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('token', token);
    }
    this.authState.next(true);
  }
}
