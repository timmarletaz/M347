import {Routes} from '@angular/router';
import {HomeComponent} from './home/home.component';
import {NotfoundComponent} from './notfound/notfound.component';
import {PollComponent} from './poll/poll.component';
import {LoginComponent} from './login/login.component';
import {RegisterComponent} from './register/register.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {AuthGuard} from './AuthGuard';
import {LoginGuard} from './LoginGuard';
import {PollDetailsComponent} from './poll-details/poll-details.component';

export const routes: Routes = [
  {path: "", component: HomeComponent, pathMatch: "full"},
  {path: "poll/:id", component: PollComponent},
  {path: "login", component: LoginComponent, canActivate: [LoginGuard]},
  {path: "register", component: RegisterComponent, canActivate: [LoginGuard]},
  {path: "dashboard", component: DashboardComponent, canActivate: [AuthGuard]},
  {path: "poll/details/:id", component: PollDetailsComponent, canActivate: [AuthGuard]},
  {path: "**", component: NotfoundComponent, pathMatch: "full"}
];
