import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IConfig } from 'app/shared/model/config.model';

type EntityResponseType = HttpResponse<IConfig>;
type EntityArrayResponseType = HttpResponse<IConfig[]>;

@Injectable({ providedIn: 'root' })
export class ConfigService {
  public resourceUrl = SERVER_API_URL + 'api/configs';

  constructor(protected http: HttpClient) {}

  create(config: IConfig): Observable<EntityResponseType> {
    return this.http.post<IConfig>(this.resourceUrl, config, { observe: 'response' });
  }

  update(config: IConfig): Observable<EntityResponseType> {
    return this.http.put<IConfig>(this.resourceUrl, config, { observe: 'response' });
  }

  find(propertyKey: string): Observable<EntityResponseType> {
    return this.http.get<IConfig>(`${this.resourceUrl}/${propertyKey}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IConfig[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(propertyKey: string): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${propertyKey}`, { observe: 'response' });
  }
}
