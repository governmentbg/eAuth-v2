import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {appConfig} from '../app.config';
import {IdentityProvider} from '../model/identityProvider';
import {Observable} from 'rxjs';
import {LoginRequest} from '../model/loginRequest';
import {LoginResponse} from '../model/loginResponse';

@Injectable()
export class AuthService {
	constructor(private _httpClient: HttpClient) {
	}

	public getIdentityProviders(): Observable<IdentityProvider[]> {
		return this._httpClient
			.get<IdentityProvider[]>(appConfig.apiUrl + 'idp/providers');
	}

	public login(requestBody: LoginRequest): Observable<LoginResponse> {
		return this._httpClient
			.post<LoginResponse>(appConfig.apiUrl + 'idp/login', requestBody);
	}
}
