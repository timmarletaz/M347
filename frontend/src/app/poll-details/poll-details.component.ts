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
    FormsModule
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
    if (confirm("Sind sie sicher, dass sie dieses Element sowie alle dazugehörigen Daten löschen möchten?")) {
      await this.pollService.deletePoll(this.id!);
    }
  }

  async addNewElement() {
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
      console.log(this.newElements);

      let response: PollDetails | null = await this.pollService.saveNewElement(this.newElements, this.id!);
      if (response) {
        this.pollDetail = response!;
      }
    }
  }

  async deleteElement(elementId: number) {
    if (confirm("Sind sie sicher, dass sie dieses Element sowie alle dazugehörigen Daten löschen möchten?")) {
      this.loading = true;
      let response: PollDetails | null = await this.pollService.deleteElement(elementId, this.id!);
      this.loading = false;
      if (response) {
        this.pollDetail = response!;
      }
    }
  }

}
