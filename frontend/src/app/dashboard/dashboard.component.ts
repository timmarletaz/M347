import { Component } from '@angular/core';
import {AuthService} from '../auth.service';
import {PollServiceService} from '../poll-service.service';
import {UserModel} from '../User.model';
import {LoaderComponent} from '../loader/loader.component';
import {PollPreview} from '../Poll';
import {Router} from '@angular/router';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ElementRequest} from '../elementRequest.model';

@Component({
  selector: 'app-dashboard',
  imports: [
    LoaderComponent,
    ReactiveFormsModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent {

  protected user: UserModel | null = null;
  loading = false;
  pollsLoading: boolean = true;
  pollPreviews: PollPreview[] | null = null;
  protected creatingNewPoll = false;
  form!: FormGroup;
  protected elements: ElementRequest[] = [];

  constructor(private authService: AuthService, private pollService: PollServiceService, private router: Router, private fb: FormBuilder) {
    this.loading = true;
    this.createNewPoll();
    this.authService.loadUser();
    this.authService.getUser().subscribe(user => {
      if(user !== null){
        this.user = user;
        this.loading = false;
      }
    })
    this.pollsLoading = true;
    this.authService.loadPolls();
    this.authService.getPollPreviews().subscribe(pollPreview => {
      if(pollPreview != null){
        console.log(pollPreview);
        this.pollPreviews = pollPreview;
      }
      this.pollsLoading = false;
    })
  }

  loadPollDetails(pollPreview: PollPreview) {
    console.log(pollPreview.uuid);
    this.router.navigate(['/poll/details/' + pollPreview.uuid]);
  }

  createNewPoll() {
      this.creatingNewPoll = true;
      this.form = this.fb.group({
        title: ["", Validators.required],
        description: [''],
      })
  }

  abortNewPoll() {
    this.creatingNewPoll = false;
  }

  onSubmit() {
    console.log(this.form.value);
  }

  protected elementOverview = false;

  addNewElement() {

  }
}

