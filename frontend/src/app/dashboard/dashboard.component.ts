import {Component} from '@angular/core';
import {AuthService} from '../auth.service';
import {PollServiceService} from '../poll-service.service';
import {UserModel} from '../User.model';
import {LoaderComponent} from '../loader/loader.component';
import {PollPreview} from '../Poll';
import {Router} from '@angular/router';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {ElementRequest} from '../elementRequest.model';
import {AlertService} from '../alert-service';

@Component({
  selector: 'app-dashboard',
  imports: [
    LoaderComponent,
    ReactiveFormsModule,
    FormsModule
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
  protected code: string | null = null;

  constructor(private alertServive: AlertService, private authService: AuthService, private pollService: PollServiceService, private router: Router, private fb: FormBuilder) {
    this.loading = true;
    setTimeout(() => {
      this.authService.loadUser();
    }, 2000);
    this.authService.getUser().subscribe(user => {
      if (user !== null) {
        this.user = user;
        this.loading = false;
      }
    })
    this.pollsLoading = true;
    this.authService.loadPolls();
    this.authService.getPollPreviews().subscribe(pollPreview => {
      if (pollPreview != null) {
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
    console.log(this.form.value, this.elements);
    this.loading = true;
    this.pollService.createPoll(this.form.value.title, this.form.value.description, this.elements);
    this.pollService.getPollCode().subscribe(code => {
      if(code != null){
        let pollPreview: PollPreview = {uuid: code.uuid, description: code.description, id: code.id, title: code.title, creator: code.creator};
        this.pollPreviews?.push(pollPreview);
        this.abortNewPoll();
      }
      this.loading = false;
    })
  }

  protected elementOverview = false;
  type: string = "";
  label: string = "";
  placeholder: string = "";
  required: boolean = false;

  addNewElement() {
    if (this.type && this.label && this.placeholder) {
      this.elements.push({label: this.label, type: this.type, placeholder: this.placeholder, required: this.required});
      this.elementOverview = false;
      this.type = "";
      this.label = "";
      this.placeholder = "";
      this.required = false;
    }
  }

  isElementRequired(element: ElementRequest) {
    return element.required ? "*":"";
  }

  logout() {
    console.log("hallo");
    localStorage.clear();
    this.alertServive.showToast("Erfolgreich Ausgeloggt", "success", 2500);
    this.router.navigate(["/"]);
  }
}

