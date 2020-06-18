import {NgModule} from '@angular/core';
import {AppHeaderComponent} from './app-header.component';
import {RouterModule} from '@angular/router';

@NgModule({
	declarations: [AppHeaderComponent],
	imports: [
		RouterModule,
	],
	exports: [AppHeaderComponent],
})
export class AppHeaderModule {

}
