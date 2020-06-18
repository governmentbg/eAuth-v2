import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SharedMaterialModule} from '../../../shared/shared-material.module';
import {TfaButtonComponent} from './tfa-button.component';
import {CommonModule} from '@angular/common';

@NgModule({
	declarations: [TfaButtonComponent],
	imports: [
		ReactiveFormsModule,
		FormsModule,
		CommonModule,
		SharedMaterialModule,
	],
	exports: [TfaButtonComponent],
})
export class TfaButtonModule {
}
