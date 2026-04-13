import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {BehaviorSubject, firstValueFrom} from 'rxjs';
import {LoginResponse, UserModel} from './User.model';
import {AlertService} from './alert-service';
import {PollPreview} from './Poll';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private httpClient: HttpClient, private alertService: AlertService) {
  }

  private baseUrl = "/api/"
  private user = new BehaviorSubject<UserModel | null>(null);
  private pollPreview = new BehaviorSubject<PollPreview[] | null>(null);

  async loginRequest(email: string, password: string) {
    try {
      let user = await firstValueFrom(
        this.httpClient.post<UserModel>(
          this.baseUrl + "auth/login",
          {email, password},
          {
            withCredentials: true,
            headers: new HttpHeaders({
              "Content-Type": "application/json"
            })
          }
        )
      );
      await this.loadCsrfToken();
      if (user !== null) {
        console.log(user);
        this.user.next(user);
        this.alertService.showToast("Erfolgreich Angemeldet", "success", 2500);
      }
    } catch (error: any) {
      this.alertService.showToast(error.error.message || "Anmeldung fehlgeschlagen", "danger", 2500);
      this.user.next(null);
    }
  }


  async loadCsrfToken() {
    try {
      await firstValueFrom(this.httpClient.get(this.baseUrl + "auth/csrf", {withCredentials: true}));
    } catch (error) {
      console.log(error);
      this.alertService.showToast("Es ist ein unerwarteter Fehler aufgetreten", "danger", 2500);
      throw error;
    }
  }

  getUser() {
    return this.user.asObservable();
  }

  async loadUser() {
    if (this.user.getValue() !== null) {
      return this.user.getValue();
    }
    try {
      let user = await firstValueFrom(this.httpClient.get<UserModel>(this.baseUrl + "user", {withCredentials: true}));
      if (user !== null) {
        this.user.next(user);
        return user;
      }
    } catch (e) {
      // IGNORE
    }
    return null;
  }

  loadPolls() {
    this.httpClient.get<PollPreview[]>(this.baseUrl + "user/polls/all", {withCredentials: true}).subscribe(response => {
      console.log(response);
      this.pollPreview.next(response);
    }, error => {
      this.alertService.showToast(error.error.message || "Es ist etwas schiefgelaufen", "danger", 2500);
    })
  }

  getPollPreviews() {
    return this.pollPreview.asObservable();
  }

  async register(email: string, password: string, vorname: string, nachname: string) {
    try {
      let user = await firstValueFrom(this.httpClient.post<UserModel>(this.baseUrl + "auth/register", {
        email: email,
        firstname: vorname,
        lastname: nachname,
        password: password
      }, {
        withCredentials: true, headers: new HttpHeaders({
          "Content-Type": "application/json"
        })
      }));
      await this.loadCsrfToken();
      if (user !== null) {
        this.user.next(user);
        this.alertService.showToast("Erfolgreich Registriert", "success", 2500);
      }
    } catch (error: any) {
      console.log(error.error.message);
      this.alertService.showToast(error.error.message || "Es ist etwas schiefgelaufen", "danger", 2500);
    }
  }

  async logout() {
    try {
      this.user.next(null);
      await firstValueFrom(this.httpClient.delete(this.baseUrl + "auth/logout", {withCredentials: true}));
    } catch (e) {
      this.alertService.showToast("Abmelden fehlgeschlagen", "warning", 2500);
    }
  }
}
