import {NgModule} from '@angular/core';
import {AppFooterComponent} from './app-footer.component';
import { TranslateModule } from '@ngx-translate/core';

@NgModule({
	declarations: [AppFooterComponent],
	imports: [
		TranslateModule
	],
	exports: [AppFooterComponent],
})
export class AppFooterModule {}
