import {Component} from '@angular/core';
import {PollServiceService} from '../poll-service.service';
import {ActivatedRoute} from '@angular/router';
import {Poll} from '../Poll';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';

@Component({
  selector: 'app-poll',
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './poll.component.html',
  styleUrl: './poll.component.scss'
})
export class PollComponent {
  loading: boolean = false;
  private id: string | null = "";
  protected poll: Poll | null = null;

  form: FormGroup = new FormGroup({});


  constructor(private pollService: PollServiceService, private route: ActivatedRoute) {
    this.id = this.route.snapshot.paramMap.get('id');
    if (this.id != null) {
      this.loading = true;
      this.pollService.fetchPoll(this.id);
      this.pollService.getActivePoll().subscribe(fetchedPoll => {
        console.log(fetchedPoll);
        if (fetchedPoll != null) {
          this.poll = fetchedPoll;
          this.loading = false;
          this.poll.elements.forEach(element => {
            this.form.addControl(element.id.toString(), new FormControl('', element.required ? Validators.required : null));
          });
        }
      })
    }
  }

  async onSubmit() {
    if (this.form.valid) {
      if (this.id != null) {
        let value: any[] = [];

        Object.entries(this.form.value).forEach(([key, element]) => {
          if (typeof element === "boolean") {
            console.log("JAAAA")
            value.push(element ? "checked" : "unchecked");
          } else if (typeof element === "number"){
            value.push(element.toString());
          } else {
            value.push(element);
          }
        });
        console.log(value);
        this.loading = true;
        await this.pollService.submitAnswer(this.id, value);
        this.loading = false;
      }
    }
  }

  checkForm() {
    return !this.form.valid;
  }

  isElementRequired(element: {id: number; label: string; placeholder: string; type: string; required: boolean}) {
    return element.required ? "*" : "";
  }
}
