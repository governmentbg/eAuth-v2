import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpEvent, HttpResponse } from '@angular/common/http';
import { appConfig } from '../app.config';
import { map } from 'rxjs/operators';

@Injectable()
export class AuthService {
    private headers = new HttpHeaders();
    private options = {};

    constructor(
        private _httpClient: HttpClient)
    {
        this.headers = this.headers.set('Content-Type', 'application/json; charset=utf-8');
        this.headers.delete('Authorization');
        this.options = { headers: this.headers };
    }


    createProfile (user, authorization){
        this.headers = this.headers.set('Access-Control-Allow-Origin', '*');
        this.headers = this.headers.set('Authorization', 'Bearer ' + authorization);

        this.options = {
            headers: this.headers,
        };
        // return this._httpClient.put('api/user', user, this.options);

        return this._httpClient.put(appConfig.url + 'api/user', user, this.options);
    }

    getUserProfile (authorization){
        this.headers = this.headers.set('Access-Control-Allow-Origin', '*');
        this.headers = this.headers.set('Authorization', 'Bearer ' + authorization);

        this.options = {
            headers: this.headers,
        };

        // return this._httpClient.get('api/user', this.options);
        return this._httpClient.get(appConfig.url + 'api/user', this.options);
   }

   authUserProfile (){
        this.headers = this.headers.set('Access-Control-Allow-Origin', '*');

        this.options = {
            headers: this.headers,
            responseType:'text'
        };

        // return this._httpClient.get('auth/token', this.options);
        return this._httpClient.get(appConfig.url + 'auth/token', this.options);
    }
}
