import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {BehaviorSubject, catchError} from 'rxjs';
import {LoginResponse, UserModel} from './User.model';
import {AlertService} from './alert-service';
import {Poll, PollPreview} from './Poll';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private httpClient: HttpClient, private alertService: AlertService) { }

  private baseUrl = "http://localhost:8080/api/"
  private user = new BehaviorSubject<UserModel|null>(null);
  private pollPreview = new BehaviorSubject<PollPreview[] | null>(null);

  loginRequest(email: string, password: string) {
    this.httpClient.post<LoginResponse>(this.baseUrl + "auth/login", {password: password, email: email}, {headers: new HttpHeaders({
        "ContentType": "application/json"
        }
      )}).subscribe(response => {
        console.log(response);
        localStorage.setItem("token", response.token);
        console.log(localStorage.getItem("token"));
        localStorage.setItem("exp", response.expires.toString());
        this.user.next(response.user);
        this.alertService.showToast("Erfolgreich eingeloggt", "success", 2500);
    }, error => {
        this.alertService.showToast(error.error.message || "Es ist etwas schiefgelaufen", "danger", 2500);
        this.user.next(null);
    })
  }

  getUser() {
    return this.user.asObservable();
  }

  loadUser() {
    let token = localStorage.getItem("token");
    if(token) {
      this.httpClient.get<UserModel>(this.baseUrl + "user", {headers: new HttpHeaders({
          token: token as string
        })}).subscribe(response => {
        console.log(response);
        this.user.next(response);
      }, error => {
          this.alertService.showToast("Daten konnten nicht geladen werden", "danger", 2500);
      })
    }
  }

  loadPolls() {
    let token = localStorage.getItem("token");
    if(token) {
      this.httpClient.get<PollPreview[]>(this.baseUrl + "user/polls/all", {headers: new HttpHeaders({
          token: token as string
        })}).subscribe(response => {
          console.log(response);
          this.pollPreview.next(response);
      }, error => {
          this.alertService.showToast(error.error.message || "Es ist etwas schiefgelaufen", "danger", 2500);
      })
    }
  }

  getPollPreviews() {
    return this.pollPreview.asObservable();
  }

}
