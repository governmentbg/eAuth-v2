import { Component, OnInit } from '@angular/core';
import { appConfig } from 'src/app/app.config';

@Component({
	selector: 'app-home',
	templateUrl: './home.component.html',
	styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
	constructor() {}

	ngOnInit() {}

	public get profileUrl() {
		return appConfig.profileUrl;
	}

	public get invalidateSSO() {
		return appConfig.invalidateSSO;
	}
}
