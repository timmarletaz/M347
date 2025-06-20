import {Injectable, signal, WritableSignal} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {BehaviorSubject} from 'rxjs';
import {Poll, PollDetails} from './Poll';
import {AlertService} from './alert-service';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class PollServiceService {

  private activePoll = new BehaviorSubject<Poll | null>(null);
  private pollDetails = new BehaviorSubject<PollDetails | null>(null);
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
}
