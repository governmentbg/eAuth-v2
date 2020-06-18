import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-change-name-dialog',
  templateUrl: './change-name-dialog.html',
  styleUrls: ['./change-name-dialog.scss']
})
export class ChangeNameDialog implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<ChangeNameDialog>,
    @Inject(MAT_DIALOG_DATA) public data: any) {}

  onNoClick(): void {
    this.dialogRef.close('No');
  }

  onYesClick(): void {
    this.dialogRef.close('Yes');
  }

  ngOnInit() {
  }

}
