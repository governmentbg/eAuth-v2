import {
    HttpEvent,
    HttpInterceptor,
    HttpHandler,
    HttpRequest,
    HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';
import { appConfig } from '../app.config';

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
    constructor(private _toastr: ToastrService,
         private _translate: TranslateService) {

    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request)
            .pipe(
                catchError((error: HttpErrorResponse) => {
                    let errorMessage = '';
                    if (error.error instanceof ErrorEvent) {
                        // client-side error
                        // this._toastr.error(this._translate.instant(error.error.code));
                    }
                    switch (error.status) {
                        case 302:
                            console.log(error);
                            break;  
                        case 400:
                            if(error.error) {
                                this._translate.get(""+error.error.code).subscribe((value: string) => {
                                    if (value != 'undefined') {
                                        this._toastr.error(value);
                                    } else {
                                        this._toastr.error(error.error.message);
                                    }
                                });
                            }
                            break;  
                        case 401:
                            if (appConfig.localhostAuth === error.url) {
                                // window.location.href = appConfig.profileCallback;
                            } else if(error.error) {
                                this._translate.get(""+error.error.code).subscribe((value: string) => {
                                    if (value != 'undefined') {
                                        this._toastr.error(value);
                                    } else {
                                        this._toastr.error(error.error.message);
                                    }
                                });
                            }
                            break;  
                        case 404:
                            if(error.error) {
                                this._translate.get(""+error.error.code).subscribe((value: string) => {
                                    if (value != 'undefined') {
                                        this._toastr.error(value);
                                    } else {
                                        this._toastr.error(error.error.message);
                                    }
                                });
                            }
                            break;
                        case 500:
                            if(error.error) {
                                this._translate.get(""+error.error.code).subscribe((value: string) => {
                                    
                                    if (value != 'undefined') {
                                        if (value === '1') {
                                            this._toastr.error(error.error.message);
                                        } else {
                                        this._toastr.error(value);
                                        }
                                    } else {
                                        this._toastr.error(error.error.message);
                                    }
                                });
                            }
                            break;  
                        default:
                            break;
                    }
                    return throwError(errorMessage);
                })
            )
    }
}