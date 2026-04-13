import {Component} from '@angular/core';
import {LoaderComponent} from '../loader/loader.component';
import {ActivatedRoute, Router} from '@angular/router';
import {PollServiceService} from '../poll-service.service';
import {PollDetails} from '../Poll';
import {ElementRequest} from '../elementRequest.model';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'app-poll-details',
  standalone: true,
  imports: [
    LoaderComponent,
    ReactiveFormsModule,
    FormsModule,
    RouterModule
  ],
  templateUrl: './poll-details.component.html',
  styleUrl: './poll-details.component.scss'
})
export class PollDetailsComponent {
  loading: boolean = false;
  private id: string | null = null;
  protected pollDetail: PollDetails | null = null;
  baseUrl = "/api/"

  elementOverview = false;
  newElements: ElementRequest[] = [];
  type: string = "";
  label: string = "";
  placeholder: string = "";
  required: boolean = false;

  constructor(private router: Router, private pollService: PollServiceService, private route: ActivatedRoute) {
    this.id = this.route.snapshot.paramMap.get("id");
    if (this.id) {
      this.loading = true;
      this.pollService.getAdminPoll(this.id);
      this.pollService.getPollDetails().subscribe(pollDetails => {
        if (pollDetails != null) {
          this.pollDetail = pollDetails;
        }
        this.loading = false;
      })
    }
  }

  async deletePoll() {
    await this.pollService.deletePoll(this.id!);
  }

  addNewElement() {
    if (this.type && this.label && this.placeholder) {
      this.newElements.push({
        label: this.label,
        type: this.type,
        placeholder: this.placeholder,
        required: this.required
      });
      this.elementOverview = false;
      this.type = "";
      this.label = "";
      this.placeholder = "";
      this.required = false;
    }
  }

}
