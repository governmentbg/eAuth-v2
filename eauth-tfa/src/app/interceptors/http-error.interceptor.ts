import {
	HttpEvent,
	HttpInterceptor,
	HttpHandler,
	HttpRequest,
	HttpErrorResponse,
} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
	constructor(private _toastr: ToastrService,
        private _router: Router) { }

	intercept(
		request: HttpRequest<any>,
		next: HttpHandler): Observable<HttpEvent<any>> {
		return next.handle(request).pipe(
			catchError((error) => {
				// console.log('catchError', error);

				if (error instanceof HttpErrorResponse) {
					const regex = /SingleSignOnService/g;
					if (error.url.match(regex)) {
						// console.log(error.url);
						window.location.href = error.url;
					}
				}

				return throwError(error);
			}),
		);
	}

	// intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
	//     return next.handle(request)
	//         .pipe(
	//             catchError((error: HttpErrorResponse) => {
	//                 let errorMessage = '';
	//                 if (error.error instanceof ErrorEvent) {
	//                     // client-side error
	//                     // this._toastr.error(this._translate.instant(error.error.code));
	//                 }
	//                 switch (error.status) {
	//                     case 400:

	//                         break;
	//                     case 401:
	//                         // if(error.error) {
	//                             this._toastr.error('Текста ще се прецизира', 'Грешка')
	//                             localStorage.clear();
	//                             this._router.navigate(['/']);
	//                         // }
	//                         break;
	//                     case 404:
	//                         break;
	//                     case 500:
	//                         break;
	//                     default:
	//                         break;
	//                 }
	//                 return throwError(errorMessage);
	//             })
	//         )
	// }
}
