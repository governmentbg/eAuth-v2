import {NgModule} from '@angular/core';
import {SharedMaterialModule} from 'src/app/shared/shared-material.module';
import {CommonModule} from '@angular/common';
import {TempLoginComponent} from './temp-login.component';
import {TempLoginRoutingModule} from './temp-login-routing.module';
import {ReactiveFormsModule, FormsModule} from '@angular/forms';
import {TempLoginService} from './temp-login.service';
import {EAuthButtonModule} from '../e-auth-button/e-auth-button.component';

@NgModule({
	declarations: [TempLoginComponent],
	imports: [
		TempLoginRoutingModule,
		SharedMaterialModule,
		CommonModule,
		ReactiveFormsModule,
		FormsModule,

		EAuthButtonModule,
	],
	exports: [TempLoginComponent],
	providers: [TempLoginService],
})
export class TempLoginModule {}
