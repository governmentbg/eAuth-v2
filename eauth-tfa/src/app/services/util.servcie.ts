import { Injectable } from '@angular/core';
import { Transaction } from '../model/transacation';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';

@Injectable()
export class UtilService {
	private _transaction: Transaction = new Transaction();
	private _timeLeft: number;

	public timer: any;
	public isTimeout = false;

	public get timeLeft() {
		return this._timeLeft;
	}

	public set timeLeft(timeLeft) {
		this._timeLeft = timeLeft;
	}

	constructor(
		private _translate: TranslateService,
		private _activatedRoute: ActivatedRoute,
		private _toastr: ToastrService
	) {}

	public timeoutMethod() {
		this.timer = setInterval(() => {
			if (this.transaction && this.transaction.timestamp) {
				this.timeBetweenDates(this.transaction.timestamp);
			}
		}, 1000);
	}

	public getIsTimeout() {
		return this.isTimeout;
	}

	public get transaction() {
		this._transaction = JSON.parse(localStorage.getItem('transaction'));
		return this._transaction;
	}

	public set transaction(transaction) {
		this._transaction = transaction;
	}

	public translateErrorCodes() {
		this._activatedRoute.queryParams.subscribe((params) => {
			this._translate.get('' + params['error']).subscribe((value) => {
				if (value != 'undefined') {
					this._toastr.error(value, '', {
						timeOut: 120000
					});
				}
			});
		});
	}

	public timeBetweenDates(timestamp) {
		const now = new Date();
		const difference = timestamp - now.getTime();

		this.timeLeft = Math.floor((difference / 1000) % 300);

		if (difference <= 0) {
			// Timer done
			this.timeLeft = 0;

			this._translate.get('warning-msg-time-end').subscribe((value) => {
				if (value != 'undefined') {
					this._toastr.warning(value, '', {
						timeOut: 120000
					});
				}
			});

			this.isTimeout = true;
			clearInterval(this.timer);
			//   localStorage.clear();
		}
	}

	public useLanguage(language: string) {
		this._translate.use(language);
	}
}
