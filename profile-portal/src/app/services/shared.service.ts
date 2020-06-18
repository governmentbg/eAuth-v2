import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { appConfig } from '../app.config';

@Injectable()
export class SharedServive {
    private headers = new HttpHeaders();
    private options = {};

    constructor(
        private _httpClient: HttpClient)
    {
        this.headers = this.headers.set('Content-Type', 'application/json; charset=utf-8');
        this.headers.delete('Authorization');
        this.options = { headers: this.headers };
    }
    
    getPlaces(place, authorization): Observable<any[]> {
        this.headers = this.headers.set('Authorization', 'Bearer ' + authorization);
        this.options = { headers: this.headers };

        // let response = this._httpClient.post<any[]>('/api/ekatte/place', { place: place }, this.options);
        let response = this._httpClient.post<any[]>(appConfig.url + 'api/ekatte/place', { place: place }, this.options);

        return response;
    }
}