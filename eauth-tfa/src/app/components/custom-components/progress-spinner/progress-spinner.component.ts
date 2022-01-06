import { Component, OnInit, Input, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedMaterialModule } from 'src/app/shared/shared-material.module';

@Component({
	selector: 'app-progress-spinner',
	templateUrl: './progress-spinner.component.html',
	styleUrls: ['./progress-spinner.component.scss']
})
export class ProgressSpinnerComponent implements OnInit {
  @Input() public showProgressSpinnner: boolean;

  constructor() { }

  ngOnInit() {
  }
}

@NgModule({
	declarations: [ProgressSpinnerComponent],
	imports: [
		CommonModule,
		SharedMaterialModule
	],
	exports: [ProgressSpinnerComponent]
})
export class ProgressSpinnerModule {

}
