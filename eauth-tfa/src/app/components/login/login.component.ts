import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { IdentityProvider } from 'src/app/model/identityProvider';
import { appConfig } from 'src/app/app.config';
import { ProgressSpinnerService }
	from '../custom-components/progress-spinner/progress-spinner.service';
import { OverlayRef } from '@angular/cdk/overlay';
import { ProgressSpinnerComponent }
	from '../custom-components/progress-spinner/progress-spinner.component';

@Component({
	selector: 'app-login',
	templateUrl: './login.component.html',
	styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit, OnDestroy {
	@Input()
	public identityProvider: IdentityProvider;

	public authMap = new Map<string, string>();
	public overlayRef: OverlayRef;

	public get isQes() {
		return this.identityProvider.id === 'qes';
	}

	public get isPik() {
		return (
			this.identityProvider.id === 'nap_pik' ||
			this.identityProvider.id === 'nap_pik_tfa'
		);
	}

	public get loginEndpoint() {
		return this.identityProvider.id === 'nap_pik' ||
			this.identityProvider.id === 'nap_pik_tfa'
			? appConfig.napPikURL
			: appConfig.loginEndpoint;
	}

	public get qesUrl() {
		return appConfig.qesUrl;
	}

	constructor(private _previewProgressSpinner: ProgressSpinnerService) {}

	ngOnInit() {}

	public inputChange(event) {
		this.authMap.set(event.id, event.value);
	}

	public qesAuth() {
		window.location.href = appConfig.qesUrl;
	}

	public showSpinner() {
		this.overlayRef = this._previewProgressSpinner.open(
			{ hasBackdrop: true },
			ProgressSpinnerComponent
		);
	}

	ngOnDestroy() {
		this._previewProgressSpinner.close(this.overlayRef);
	}
}
