import { Injectable } from '@angular/core';
import { timer, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { appConfig } from '../app.config';
import { AuthTimeout } from '../model/authTimeout';

@Injectable()
export class TimerService {
	private timeLeft: any;

	constructor(private _httpClient: HttpClient) {}

	public timeLeftCounter(timeLeft): Observable<any> {
		this.timeLeft = timeLeft;
		console.log(this.timeLeft);
		return timer(timeLeft);
	}

	public authTimeout(): Observable<AuthTimeout> {
		return this._httpClient.get<AuthTimeout>(
			appConfig.apiUrl + 'idp/auth-timeout'
		);
	}
}
