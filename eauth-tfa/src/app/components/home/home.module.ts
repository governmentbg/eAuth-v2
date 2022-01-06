import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedMaterialModule } from '../../shared/shared-material.module';
import { CustomInputModule }
	from '../custom-components/custom-input/custom-input.module';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { HomeComponent } from './home.component';
import { HomeRoutingModule } from './home-routing.module';

@NgModule({
	declarations: [HomeComponent],
	imports: [
		HomeRoutingModule,
		ReactiveFormsModule,
		FormsModule,
		CommonModule,
		SharedMaterialModule,
		CustomInputModule,
		TranslateModule
	],
	exports: [HomeComponent]
})
export class HomeModule {}
