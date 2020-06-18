import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { appConfig } from '../app.config';

@Injectable()
export class TfaService {
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

	sendCode(authorization) {
		this.headers = this.headers.set('Access-Control-Allow-Origin', '*');
		this.headers = this.headers.set(
			'Authorization',
			'Bearer ' + authorization
		);

		this.options = {
			headers: this.headers
		};

		return this._httpClient.get(
			appConfig.apiUrl + 'idp/tfa/send',
			this.options
		);
		// return this._httpClient.get('api/tfa/send', this.options);
	}

	resendCode(codeRequest, authorization) {
		this.headers = this.headers.set('Access-Control-Allow-Origin', '*');
		this.headers = this.headers.set(
			'Authorization',
			'Bearer ' + authorization
		);

		this.options = {
			headers: this.headers
		};

		return this._httpClient.put(
			appConfig.apiUrl + 'idp/tfa/resend',
			codeRequest,
			this.options
		);
		// return this._httpClient.put('api/tfa/resend', codeRequest, this.options);
	}

	verifyCode(verifierBody, authorization) {
		this.headers = this.headers.set('Access-Control-Allow-Origin', '*');
		this.headers = this.headers.set(
			'Authorization',
			'Bearer ' + authorization
		);

		this.options = {
			headers: this.headers
		};
		return this._httpClient.post(
			appConfig.apiUrl + 'idp/tfa/validate',
			verifierBody,
			this.options
		);
		// return this._httpClient.post('api/tfa/validate', verifierBody, this.options);
	}

	generateQRCode(methodType, authorization) {
		this.headers = this.headers.set('Access-Control-Allow-Origin', '*');
		this.headers = this.headers.set(
			'Authorization',
			'Bearer ' + authorization
		);

		this.options = {
			headers: this.headers
		};
		return this._httpClient.put(
			appConfig.apiUrl + 'idp/tfa/register-user',
			{ method: methodType },
			this.options
		);
		// return this._httpClient.put('api/tfa/register-user', {method: methodType}, this.options);
	}

	getAvailableMethods() {
		return this._httpClient.get(appConfig.apiUrl + 'idp/tfa/otpmethods');
		// return this._httpClient.put('api/tfa/register-user', {method: methodType}, this.options);
	}
}
