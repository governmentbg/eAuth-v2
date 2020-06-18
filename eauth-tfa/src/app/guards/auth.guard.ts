import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable()
export class AuthGuard implements CanActivate {
	constructor(private router: Router) {}

	canActivate(): boolean {
		const token = localStorage.getItem('twoFAToken');

		if (token != null) {
			return true;
		} else {
			this.router.navigate(['/']);
			return false;
		}
	}
}
