import {NgModule} from '@angular/core';
import {SelectSupplierComponent} from './select-supplier.component';
import {SharedMaterialModule} from 'src/app/shared/shared-material.module';
import {SelectSupplierRoutingModule} from './select-supplier-routing.module';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {LoginModule} from '../login/login.module';
import {AuthService} from 'src/app/services/auth.service';
import {CustomInputModule}
	from '../custom-components/custom-input/custom-input.module';
import {TimerService} from 'src/app/services/timer.service';
import { TranslateModule } from '@ngx-translate/core';
import { LangButtonModule }
	from '../custom-components/lang-button/lang-button.module';
import { SharedPipeModule } from 'src/app/shared/shared-pipe.module';

@NgModule({
	declarations: [SelectSupplierComponent],
	imports: [
		CommonModule,
		FormsModule,
		ReactiveFormsModule,
		SelectSupplierRoutingModule,
		SharedMaterialModule,
		SharedPipeModule,
		CustomInputModule,
		TranslateModule,

		LoginModule,
		LangButtonModule,
	],
	exports: [SelectSupplierComponent],
	providers: [AuthService, TimerService],
})
export class SelectSupplierModule {
}
