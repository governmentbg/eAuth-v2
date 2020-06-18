import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { appConfig } from 'src/app/app.config';

@Injectable()
export class TempLoginService {
	private headers = new HttpHeaders();
	private options = {};

	constructor(private _httpClient: HttpClient) {
		this.headers = this.headers.set(
			'Content-Type',
			'application/json; charset=utf-8'
		);
		this.headers.delete('Authorization');
		this.options = { headers: this.headers };
	}

	tempLogin(loginRequest) {
		this.headers = this.headers.set('Access-Control-Allow-Origin', '*');
		// this.headers = this.headers.set('Authorization', 'Bearer ' + authorization);

		this.options = {
			headers: this.headers
		};
		return this._httpClient.post(
			appConfig.apiUrl + 'eauth/login',
			loginRequest,
			this.options
		);
		// return this._httpClient.post('api/eauth/login', loginRequest, this.options);
	}
}
