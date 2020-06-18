import {TestBed, async} from '@angular/core/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {AppComponent} from './app.component';
import { TranslateModule } from '@ngx-translate/core';
import { TimerService } from './services/timer.service';
import { HttpClientModule } from '@angular/common/http';

describe('AppComponent', () => {
	beforeEach(async(() => {
		TestBed.configureTestingModule({
			imports: [
				RouterTestingModule,
				TranslateModule.forRoot(),
				HttpClientModule
			],
			declarations: [
				AppComponent,
			],
			providers: [TimerService]
		}).compileComponents();
	}));

	it('should create the app', () => {
		const fixture = TestBed.createComponent(AppComponent);
		const app = fixture.debugElement.componentInstance;
		expect(app).toBeTruthy();
	});

	it(`should have as title 'eAuth'`, () => {
		const fixture = TestBed.createComponent(AppComponent);
		const app = fixture.debugElement.componentInstance;
		expect(app.title).toEqual('eAuth');
	});
});
