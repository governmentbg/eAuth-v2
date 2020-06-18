import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LangButtonComponent } from './lang-button.component';
import { UtilService } from 'src/app/services/util.servcie';

@NgModule({
	declarations: [LangButtonComponent],
	imports: [
		CommonModule
	],
	exports: [LangButtonComponent],
	providers: [UtilService]
})
export class LangButtonModule { }
