import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {SharedMaterialModule} from './shared/shared-material.module';
import {AppLayoutModule} from './_layout/app-layout/app-layout.module';
import {AppFooterModule} from './_layout/app-footer/app-footer.module';
import {
	HttpClientModule,
	HTTP_INTERCEPTORS,
	HttpClient
} from '@angular/common/http';
import {ToastrModule} from 'ngx-toastr';
import {HttpErrorInterceptor} from './interceptors/http-error.interceptor';
import {AuthGuard} from './guards/auth.guard';
import {UtilService} from './services/util.servcie';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {TimerService} from './services/timer.service';
import { APP_BASE_HREF } from '@angular/common';
import { MethodButtonComponent } from './components/verifier/method-button/method-button.component';
import { VerifyInputComponent } from './components/verifier/verify-input/verify-input.component';

@NgModule({
	declarations: [
		AppComponent,
		MethodButtonComponent,
		VerifyInputComponent
	],
	imports: [
		BrowserModule,
		AppRoutingModule,
		BrowserAnimationsModule,

		TranslateModule.forRoot({
			loader: {
				provide: TranslateLoader,
				useFactory: HttpLoaderFactory,
				deps: [HttpClient],
			},
		}),

		ToastrModule.forRoot({
			timeOut: 10000,
			positionClass: 'toast-top-center',
			preventDuplicates: true,
		}), // ToastrModule added

		AppLayoutModule,
		AppFooterModule,

		SharedMaterialModule,
		HttpClientModule,
	],
	exports: [SharedMaterialModule],
	providers: [
		AuthGuard,
		UtilService,
		TimerService,
		{
			provide: HTTP_INTERCEPTORS,
			useClass: HttpErrorInterceptor,
			multi: true,
		},
		{provide: APP_BASE_HREF, useValue: '/eauth'}
	],
	bootstrap: [AppComponent],
})
export class AppModule { }

// required for AOT compilation
export function HttpLoaderFactory(http: HttpClient) {
	return new TranslateHttpLoader(http);
}
