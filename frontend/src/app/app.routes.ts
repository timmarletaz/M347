import { Routes } from '@angular/router';
import {HomeComponent} from './home/home.component';
import {AuthguardService} from './authguard.service';
import {LoginComponent} from './login/login.component';
import {RegisterComponent} from './register/register.component';
import {PollComponent} from './poll/poll.component';
import {NotfoundComponent} from './notfound/notfound.component';

export const routes: Routes = [
  {path: "", redirectTo: "home", pathMatch: "full" },
  {path: "home", component: HomeComponent, pathMatch: "full", canActivate: [AuthguardService]},
  {path: "login", component: LoginComponent, pathMatch: "full"},
  {path: "register", component: RegisterComponent, pathMatch: "full"},
  // Canactivate noch hinzufügen
  {path: "poll", component: PollComponent, pathMatch: "full"},
  {path: "**", component: NotfoundComponent, pathMatch: "full"}
];
