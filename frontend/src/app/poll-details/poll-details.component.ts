import { Component } from '@angular/core';
import {LoaderComponent} from '../loader/loader.component';
import {ActivatedRoute, Router} from '@angular/router';
import {PollServiceService} from '../poll-service.service';
import {PollDetails} from '../Poll';

@Component({
  selector: 'app-poll-details',
  imports: [
    LoaderComponent
  ],
  templateUrl: './poll-details.component.html',
  styleUrl: './poll-details.component.scss'
})
export class PollDetailsComponent {
  loading: boolean = false;
  private id: string|null = null;
  protected pollDetail: PollDetails|null = null;

  constructor(private router: Router, private pollService: PollServiceService, private route: ActivatedRoute) {
    this.id = this.route.snapshot.paramMap.get("id");
    if(this.id) {
      this.loading = true;
      this.pollService.getAdminPoll(this.id);
      this.pollService.getPollDetails().subscribe(pollDetails => {
        if(pollDetails != null) {
          this.pollDetail = pollDetails;
        }
        this.loading = false;
      })
    }
  }

}
