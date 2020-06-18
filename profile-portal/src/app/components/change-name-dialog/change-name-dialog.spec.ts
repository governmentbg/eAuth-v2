import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeNameDialog } from './change-name-dialog';

describe('ChangeNameDialogComponent', () => {
  let component: ChangeNameDialog;
  let fixture: ComponentFixture<ChangeNameDialog>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChangeNameDialog ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChangeNameDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
