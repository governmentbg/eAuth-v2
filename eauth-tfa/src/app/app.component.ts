import { Component, ViewChild } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { TimerService } from './services/timer.service';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { AuthTimeout } from './model/authTimeout';
import { appConfig } from './app.config';

@Component({
	selector: 'app-root',
	templateUrl: './app.component.html'
})
export class AppComponent {
	title = 'eAuth';

	@ViewChild('cancelAuthForm', { static: false }) cancelAuthForm;

	private _unsubscribe: Subject<void> = new Subject();

	public get cancelAuthEndpoint() {
		return appConfig.cancelAuthEndpoint;
	}

	constructor(
		private _translateService: TranslateService,
		private _timerService: TimerService
	) {
		this._translateService.use('bg');
		this.authTimeout();
	}

	private _timeoutListener(timeLeft) {
		this._timerService
			.timeLeftCounter(timeLeft)
			.pipe(takeUntil(this._unsubscribe))
			.subscribe(() => {
				this.cancelAuth();
			});
	}

	public cancelAuth() {
		this.cancelAuthForm.nativeElement.submit();
	}

	private authTimeout() {
		this._timerService.authTimeout().subscribe((res: AuthTimeout) => {
			const timeLeft = this._timeBetweenDates(res.expirationTimestamp);
			this._timeoutListener(timeLeft);
		});
	}

	private _timeBetweenDates(timestamp) {
		const now = new Date();
		const timeLeft = timestamp - now.getTime();

		return timeLeft;
	}

	ngOnDestroy() {
		this._unsubscribe.next();
		this._unsubscribe.complete();
	}
}
