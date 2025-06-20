import {Component, Input} from '@angular/core';
import {NgClass} from '@angular/common';

@Component({
    selector: 'app-alert',
    imports: [
        NgClass
    ],
    templateUrl: './alert.html',
    standalone: true,
    styleUrl: './alert.scss'
})
export class Alert {
  @Input() type: "success" | "danger" | "warning" | "info" = 'info';
  @Input() message = '';
  @Input() duration = 3000;


  visible = false;

  show(msg: string, type: typeof this.type = 'info', duration = 3000) {
    this.message = msg;
    this.type = type;
    this.duration = duration;
    this.visible = true;

    setTimeout(() => (this.visible = false), this.duration);
  }
}
