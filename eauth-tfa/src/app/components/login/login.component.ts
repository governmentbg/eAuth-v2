import { Component, OnInit, Input } from '@angular/core';
import { IdentityProvider } from 'src/app/model/identityProvider';
import { appConfig } from 'src/app/app.config';

@Component({
	selector: 'app-login',
	templateUrl: './login.component.html',
	styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
	@Input()
	public identityProvider: IdentityProvider;

	public authMap = new Map<string, string>();

	public get isQes() {
		return this.identityProvider.id === 'qes';
	}

	public get loginEndpoint() {
		return appConfig.loginEndpoint;
	}

	public get qesUrl() {
		return appConfig.qesUrl;
	}

	constructor() {}

	ngOnInit() {}

	public inputChange(event) {
		this.authMap.set(event.id, event.value);
	}

	public qesAuth() {
		window.location.href = appConfig.qesUrl;
	}
}
