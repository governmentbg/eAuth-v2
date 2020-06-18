import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { TempLoginService } from './temp-login.service';
import { DomSanitizer } from '@angular/platform-browser';
import { appConfig } from 'src/app/app.config';

@Component({
	selector: 'app-temp-login',
	templateUrl: './temp-login.component.html',
	styleUrls: ['./temp-login.component.scss']
})
export class TempLoginComponent implements OnInit {
	public tempLoginForm: FormGroup;
	public token: string;

	sanitaizeHtml: any;

	public get sp1Url() {
		return appConfig.serviceProvider1Url;
	}

	public get sp2Url() {
		return appConfig.serviceProvider2Url;
	}

	public get sp3Url() {
		return appConfig.serviceProvider3Url;
	}

	public get profileUrl() {
		return appConfig.profileUrl;
	}

	constructor(
		private sanitizer: DomSanitizer,
		private _formBuiler: FormBuilder,
		private _tempLoginService: TempLoginService
	) {}

	ngOnInit() {}
}
