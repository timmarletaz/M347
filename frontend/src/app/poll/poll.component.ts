import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule} from '@angular/forms';
import {clearInterval} from 'node:timers';
import {Poll} from '../Poll';
import {Question} from '../Question';

@Component({
  selector: 'app-poll',
  imports: [
    FormsModule
  ],
  templateUrl: './poll.component.html',
  styleUrl: './poll.component.scss'
})
export class PollComponent {
  @ViewChild('createQuestion') dialogRef!: ElementRef<HTMLDialogElement>;

  protected title: string = '';
  protected description: string = '';

  protected label: string = '';
  protected type: string = '';
  protected placeholder: string = '';
  protected required: boolean = false;

  constructor(private formBuilder: FormBuilder) {}

  // polls: Poll[] = [];

  questions: Question[] = [];


  openDialog() {
    this.dialogRef.nativeElement.showModal();
  }

  createNewQuestion() {
      console.log(this.label + ' ' + this.type + ' ' + this.placeholder + ' ' + this.required);

      // TODO: Alle Fragenattribute dem Questionsarry hinzufügen
  }


}
