import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PollDetailsComponent } from './poll-details.component';

describe('PollDetailsComponent', () => {
  let component: PollDetailsComponent;
  let fixture: ComponentFixture<PollDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PollDetailsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PollDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
