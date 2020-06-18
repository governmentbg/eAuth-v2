import {NgModule} from '@angular/core';
import {AppLayoutComponent} from './app-layout.component';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {AppHeaderModule} from '../app-header/app-header.module';
import {AppFooterModule} from '../app-footer/app-footer.module';

@NgModule({
	declarations: [AppLayoutComponent],
	imports: [
		CommonModule,
		RouterModule,

		AppHeaderModule,
		AppFooterModule,
	],
	exports: [],
})
export class AppLayoutModule {

}
