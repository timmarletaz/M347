import {Injectable, signal, WritableSignal} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {BehaviorSubject} from 'rxjs';
import {Poll, PollDetails} from './Poll';
import {AlertService} from './alert-service';
import {Router} from '@angular/router';
import {ElementRequest} from './elementRequest.model';

@Injectable({
  providedIn: 'root'
})
export class PollServiceService {

  private activePoll = new BehaviorSubject<Poll | null>(null);
  private pollDetails = new BehaviorSubject<PollDetails | null>(null);
  private pollCode = new BehaviorSubject<Poll | null>(null);
  private baseUrl: string = "http://localhost:8080/api/";
  private isSubmitted: WritableSignal<boolean> = signal(false);

  constructor(private httpClient: HttpClient, private alertService: AlertService, private router: Router) {}

  fetchPoll(uuid: string) {
    this.httpClient.get<Poll>(this.baseUrl + "polls/" + uuid).subscribe(response => {
      this.activePoll.next(response);
    }, error => {
      console.log(error.error.error);
      this.alertService.showToast("Es ist etwas schiefgelaufen", "danger", 2500);
    })
  }

  getActivePoll() {
    return this.activePoll.asObservable();
  }

  async submitAnswer(uuid: string, values: string[]) {
    this.httpClient.post<string[]>(this.baseUrl + "polls/" + uuid + "/submit", {values}, {headers: new HttpHeaders({
        "ContentType": "application/json"
      })}).subscribe(next => {
        this.alertService.showToast("Erfolgreich gesendet", "success", 2000);
        this.router.navigate(['/']);
    }, error => {
        console.log(error);
        this.alertService.showToast(error.error.message, "danger", 2000);
    });
  }

  getAdminPoll(id: string) {
    let token = localStorage.getItem("token");
    if(token) {
      this.httpClient.get<PollDetails>(this.baseUrl + "polls/" + id + "/admin", {headers: new HttpHeaders({
          token: token as string
        })}).subscribe(response => {
            console.log(response);
            this.pollDetails.next(response);
      }, error => {
          this.alertService.showToast(error.error.message, "danger", 2000);
      })
    }
  }

  getPollDetails() {
    return this.pollDetails.asObservable();
  }

  createPoll(title: string, description: string, elements: ElementRequest[]) {
    let token = localStorage.getItem("token");
    if(token) {
      this.httpClient.post<Poll>(this.baseUrl + "polls/create", {title: title, description: description, elements: elements}, {headers: new HttpHeaders({
          token: token as string
        })}).subscribe(response => {
          this.pollCode.next(response);
          this.alertService.showToast("Erfolgreich erstellt", "success", 2000);
      }, error => {
          console.log(error);
          this.alertService.showToast(error.error.message || "Speichern fehlgeschlagen", "danger", 2000);
      })
    }
  }

  getPollCode() {
    return this.pollCode.asObservable();
  }
}
