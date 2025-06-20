import { Component } from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {LoaderComponent} from '../loader/loader.component';

@Component({
  selector: 'app-home',
  imports: [
    RouterLink,
    FormsModule,
    LoaderComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {
  codeContent: string = "";

  constructor(private router: Router) {
  }

  goToForm() {
    this.router.navigate(['/poll/' + this.codeContent]);
  }
}
