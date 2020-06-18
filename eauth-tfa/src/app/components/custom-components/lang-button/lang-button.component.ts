import { Component, OnInit, Input } from '@angular/core';
import { UtilService } from 'src/app/services/util.servcie';

@Component({
	selector: 'app-lang-button',
	templateUrl: './lang-button.component.html',
	styleUrls: ['./lang-button.component.scss'],
})
export class LangButtonComponent implements OnInit {
	@Input()
	public icon: string;

	@Input()
	public lang: string;

	constructor(private _utilservice: UtilService) {}

	ngOnInit() {}

	public useLanguage() {
		this._utilservice.useLanguage(this.lang);
	}
}
