import { Component, OnInit, OnDestroy } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';
import { IdentityProvider } from 'src/app/model/identityProvider';
import { UtilService } from 'src/app/services/util.servcie';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { appConfig } from 'src/app/app.config';

@Component({
	selector: 'app-select-supplier',
	templateUrl: './select-supplier.component.html',
	styleUrls: ['./select-supplier.component.scss']
})
export class SelectSupplierComponent implements OnInit, OnDestroy {
	private _unsubscribe: Subject<void> = new Subject();

	public eIdSelector: IdentityProvider[] = [];

	public selectedIdentifier: string;
	public showLogin = false;
	public selectedIdentityProvider: IdentityProvider;

	public get locale() {
		return this._translate.currentLang;
	}

	constructor(
		private _authService: AuthService,
		private _utilservice: UtilService,
		private _translate: TranslateService
	) {}

	ngOnInit() {
		this._utilservice.translateErrorCodes();
		localStorage.clear();
		localStorage.setItem('isSend', 'true');

		this._getProviders();
	}

	public getSelectedidentifier(event) {
		const selectedValue = event.value;
		this.selectedIdentityProvider = selectedValue;
		this.showLogin = true;
	}

	public useLanguage(language: string) {
		this._utilservice.useLanguage(language);
	}

	public get profileUrl() {
		return appConfig.profileUrl;
	}

	private _getProviders() {
		this._authService
			.getIdentityProviders()
			.pipe(takeUntil(this._unsubscribe))
			.subscribe((res: IdentityProvider[]) => {
				this.eIdSelector = res;
			});
	}

	ngOnDestroy() {
		this._unsubscribe.next();
		this._unsubscribe.complete();
	}
}
