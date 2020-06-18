import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SharedMaterialModule} from '../../../shared/shared-material.module';
import {CustomInputComponent} from './custom-input.component';
import { SharedPipeModule } from 'src/app/shared/shared-pipe.module';

@NgModule({
	declarations: [CustomInputComponent],
	imports: [
		ReactiveFormsModule,
		FormsModule,
		SharedMaterialModule,
		SharedPipeModule,
	],
	exports: [CustomInputComponent],
})
export class CustomInputModule {
}
