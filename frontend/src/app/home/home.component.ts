import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-home',
  imports: [
    FormsModule,
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
