import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Router, RouterOutlet} from '@angular/router';
import {NavigationComponent} from './navigation/navigation.component';
import {NgClass, NgStyle} from '@angular/common';
import {filter} from 'rxjs';
import {LoaderComponent} from './loader/loader.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavigationComponent, NgClass, LoaderComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  title = 'frontend';
  isAnswering: boolean = false;

  constructor(private router: Router) {
  }

  ngOnInit() {
    this.router.events.pipe(filter(event => event instanceof NavigationEnd)).subscribe((event) => {
      this.isAnswering = this.router.url.includes("poll") && !this.router.url.includes("details");
    })
  }

}
