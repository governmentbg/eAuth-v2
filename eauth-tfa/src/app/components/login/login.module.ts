import { NgModule } from '@angular/core';
import { LoginComponent } from './login.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedMaterialModule } from '../../shared/shared-material.module';
import { CustomInputModule }
	from '../custom-components/custom-input/custom-input.module';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';

@NgModule({
	declarations: [LoginComponent],
	imports: [
		ReactiveFormsModule,
		FormsModule,
		CommonModule,
		SharedMaterialModule,
		CustomInputModule,
		TranslateModule
	],
	exports: [LoginComponent]
})
export class LoginModule {}
